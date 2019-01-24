package core;

import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.exception.OConcurrentModificationException;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import com.orientechnologies.orient.core.tx.OTransaction;
import org.gedcom4j.model.Family;
import org.gedcom4j.model.FamilyChild;
import org.gedcom4j.model.FamilySpouse;
import org.gedcom4j.model.Gedcom;
import sun.misc.BASE64Encoder;

import java.sql.Date;

import javax.annotation.Nullable;
import java.util.*;

public class OrientDBService implements Dao {

  private OrientDB mServer;
  private ODatabasePool mDbPool;

  private static int TEMP_GEDCOM_ID = 15;

  /**
   * Initializes the OrientDB database instance.
   *
   * @param serverUrl        Address of the OrientDB server.
   * @param serverUser       User of the OrientDB server.
   * @param serverPassword   Password for serverUser.
   * @param database         Name of the specific database on the OrientDB server.
   * @param databaseUser     User of the database on the OrientDB server.
   * @param databasePassword Password for databaseUser.
   */
  public OrientDBService(String serverUrl, String serverUser, String serverPassword, String database, String
          databaseUser, String databasePassword) {
    mServer = new OrientDB(serverUrl, serverUser, serverPassword, OrientDBConfig.defaultConfig());
    mDbPool = new ODatabasePool(mServer, database, databaseUser, databasePassword);
  }

  @Override
  public boolean isExistingUser(String username) {
    try (ODatabaseSession db = mDbPool.acquire()) {
      return isExistingUser(db, username);
    }
  }

  @Override
  public boolean isAuthenticatedUser(String username, String password) {

    try (ODatabaseSession db = mDbPool.acquire()) {
      // No transaction necessary. Read-only operations.

      byte[] salt = getSalt(db, username);
      if (salt == null) {
        return false; // No salt == no user.
      }

      byte[] hash = SecurityUtil.hash(password, salt);
      return passwordIsValid(db, username, hash);
    }
  }

  @Override
  public boolean addUser(User user) {
    try (ODatabaseSession db = mDbPool.acquire()) {
      // An arbitrary value. A single failure due to simultaneous account creation would be
      // remarkable itself.
      int maxRetries = 3;
      for (int retry = 0; retry < maxRetries; ++retry) {
        try {
          db.begin(OTransaction.TXTYPE.OPTIMISTIC);

          if (isExistingUser(db, user.username)) {
            db.rollback();
            break; // Will return false.
          }

          byte[] salt = SecurityUtil.salt(32);
          byte[] hash = SecurityUtil.hash(user.password, salt);

          String command = "INSERT INTO User SET username = ?, password = encode(?, 'base64'), " + "salt = ?, email "
                  + "=" + " ?";
          db.command(command, user.username, hash, salt, user.email);
          db.commit();
          return true;

        } catch (OConcurrentModificationException e) {
          db.rollback();
        }
      }
      return false; // Failed too many times.
    }
  }

  /**
   * Returns true if the user with the provided username already exists in the database. This is a
   * known helper method to both isExistingUser and addUser.
   * <p>
   * This method does NOT close the ODatabaseSession instance. Take care in managing this resource.
   *
   * @param db       ODatabaseSession instance with which to query for the given username.
   * @param username Username to be queried.
   * @return True if the database contains the provided username.
   */
  private boolean isExistingUser(ODatabaseSession db, String username) {
    String query = "SELECT FROM User where username = ?";
    OResultSet rs = db.query(query, username);
    boolean result = false;
    if (rs.hasNext()) {
      result = true;
    }
    rs.close();
    return result;
  }

  /**
   * Returns the salt stored on the same record as the provided username. If no user exists with
   * the provided username, returns null.
   * <p>
   * This method does NOT close the ODatabaseSession instance. Take care in managing this resource.
   *
   * @param db       ODatabaseSession instance with which to query for the user's salt.
   * @param username Username linked to the queried salt.
   * @return salt if username exists in the database, null otherwise.
   */
  @Nullable
  private byte[] getSalt(ODatabaseSession db, String username) {
    String query = "SELECT salt FROM User WHERE username = ?";
    OResultSet rs = db.query(query, username);
    byte[] salt = null;
    if (rs.hasNext()) {
      salt = rs.next().getProperty("salt");
    }
    rs.close();
    return salt;
  }

  /**
   * Returns true if the provided hash matches that stored on the same record to which the provided
   * username is found.
   * <p>
   * This method does NOT close the ODatabaseSession instance. Take care in managing this resource.
   *
   * @param db       ODatabaseSession instance with which to query for the user's hash.
   * @param username Username linked to the queried hash.
   * @param hash     Hash to compare with that on record for the given user.
   * @return true if the provided hash matches that on record.
   */
  private boolean passwordIsValid(ODatabaseSession db, String username, byte[] hash) {
    String base64Hash = new BASE64Encoder().encode(hash);
    String query = "SELECT FROM User WHERE username = ? AND password = decode(?, 'base64')";

    boolean isValid = false;
    OResultSet rs = db.query(query, username, base64Hash);
    if (rs.hasNext()) {
      isValid = true;
    }
    rs.close();
    return isValid;
  }

  @Override
  public boolean addGedcom(String username, String path) {

    try (ODatabaseSession db = mDbPool.acquire()) {
      db.begin();

      Gedcom gedcom = GedcomImporter.load(path);
      if (gedcom == null) {
        return false;
      }

      // -----------------------------------------------------------------------------------------

      /*
       * This method should be refactored in an attempt to better separate concerns. Otherwise,
       * the OrientDBService may end up containing multiple addGedcom methods for each additional
       * gedcom version and/or importer used.
       *
       * However, large data sets may make this more difficult, given that extracting all conversions
       * to AbstractSerializableVertex and then back to OGraph will double the number of operations
       * before committing the data to the database.
       */

      HashMap<Long, OVertex> familyVertexMap = new HashMap<>();
      gedcom.getIndividuals().forEach((xref, ind) -> {

        SerializablePerson serializablePerson = GedcomImporter.importIndividual(ind);
        OVertex personVertex = createVertex(db, username, serializablePerson);

        List<FamilyChild> fwc = ind.getFamiliesWhereChild();
        if (fwc != null) {
          for (FamilyChild fc : fwc) {
            Family fam = fc.getFamily();
            long id = xrefToId(fam.getXref());

            OVertex familyVertex = getVertexFromMapCreateIfNeeded(username, db, familyVertexMap, fam, id);
            createChildEdge(personVertex, familyVertex);
          }
        }

        List<FamilySpouse> fws = ind.getFamiliesWhereSpouse();
        if (fws != null) {
          for (FamilySpouse fs : fws) {
            Family fam = fs.getFamily();
            long id = xrefToId(fam.getXref());

            OVertex familyVertex = getVertexFromMapCreateIfNeeded(username, db, familyVertexMap, fam, id);
            createParentEdge(personVertex, familyVertex);
          }
        }
      });

      // -----------------------------------------------------------------------------------------

      db.commit();
    }
    return false;
  }

  private OVertex getVertexFromMapCreateIfNeeded(String username, ODatabaseSession db, HashMap<Long, OVertex> familyVertexMap, Family
          fam, long id) {

    OVertex familyVertex;
    if (familyVertexMap.containsKey(id)) {
      familyVertex = familyVertexMap.get(id);
    } else {
      SerializableFamily serializableFamily = GedcomImporter.importFamily(fam);
      familyVertex = createVertex(db, username, serializableFamily);
      familyVertexMap.put(id, familyVertex);
    }
    return familyVertex;
  }


  private static OVertex createVertex(ODatabaseSession db, String username, SerializablePerson person) {
    return createVertex(db, "Person", username, person);
  }

  private static OVertex createVertex(ODatabaseSession db, String username, SerializableFamily family) {

    return createVertex(db, "Family", username, family);
  }

  private static OVertex createVertex(ODatabaseSession db, String type, String username, AbstractSerializableVertex serializable) {
    /*
     * TODO
     * Add front-end multi-gedcom upload support.
     * The default gedcomId is hard-coded below.
     *
     * Use the following link to work out the auto-increment query given OrientDB does NOT natively
     * support auto-increment.
     *
     * https://orientdb.com/docs/2.0/orientdb.wiki/Sequences-and-auto-increment.html
     */
    OVertex vertex = db.newVertex(type);
    vertex.setProperty("user", username);
    vertex.setProperty("gedcomId", TEMP_GEDCOM_ID);
    vertex.setProperty("vertexId", serializable.id);
    vertex.setProperty("facts", embedFacts(db, serializable.facts));
    vertex.setProperty("events", embedEvents(db, serializable.events));
    vertex.save();
    return vertex;
  }

  private static Long xrefToId(String xref) {
    return Long.parseLong(xref.substring(2, xref.length() - 1));
  }

  private static List<OElement> embedFacts(ODatabaseSession db, List<Fact> facts) {
    List<OElement> result = new ArrayList<>();
    facts.forEach(fact -> {
      OElement element = db.newElement("Fact");
      element.setProperty("type", fact.type);
      element.setProperty("description", fact.description);
      result.add(element);
    });
    return result;
  }

  private static List<OElement> embedEvents(ODatabaseSession db, List<Event> events) {
    List<OElement> result = new ArrayList<>();
    events.forEach(event -> {
      OElement element = db.newElement("Event");
      element.setProperty("type", event.type);
      if (event.date != null) {
        // Necessary null check to convert LocalDate to database-supported java.sql.Date
        element.setProperty("date", Date.valueOf(event.date));
      }
      element.setProperty("place", event.place);
      result.add(element);
    });
    return result;
  }

  //  private static List<OElement> embeddedList(ODatabaseSession db, List<? extends
  // AbstractAttribute> attributes) {
  //    attributes.forEach(attr -> {
  //      OElement element = db.newElement();
  //    });
  //  }

  //  private static OVertex createVertex(ODatabaseSession db, String username, Person person) {
  //    OVertex vertex = db.newVertex("Person");
  //
  //    List<OElement> list = new ArrayList<>();
  //
  //    List<Fact> facts = person.getFacts();
  //    for (Fact fact : facts) {
  //      OElement element = db.newElement("Fact");
  //      element.setProperty("type", fact.type);
  //      element.setProperty("description", fact.description);
  //      list.add(element);
  //    }
  //
  //
  //
  //
  //    vertex.setProperty("user", username);
  //    vertex.setProperty("id", person.getId());
  //    vertex.setProperty("facts", list, OType.EMBEDDEDLIST);
  //    vertex.save();
  //    return vertex;
  //  }

  private static OVertex createVertex(ODatabaseSession db, String username, Familiable family) {
    OVertex vertex = db.newVertex("Family");
    vertex.setProperty("user", username);
    vertex.setProperty("id", family.getId());
    vertex.save();
    return vertex;
  }

  private static void createChildEdge(OVertex child, OVertex family) {
    OEdge childToFamily = child.addEdge(family, "ChildToFamily");
    childToFamily.save();

    OEdge familyToChild = family.addEdge(child, "FamilyToChild");
    familyToChild.save();
  }

  private static void createParentEdge(OVertex parent, OVertex family) {
    OEdge parentToFamily = parent.addEdge(family, "ParentToFamily");
    parentToFamily.save();

    OEdge familyToParent = family.addEdge(parent, "FamilyToParent");
    familyToParent.save();
  }


  //  private static OVertex createVertex(ODatabaseSession db, String user, GedcomPerson person) {
  //    OVertex vertex = db.newVertex("GedcomPerson");
  //    vertex.setProperty("user", user);
  //    vertex.setProperty("id", person.id);
  //    vertex.setProperty("name", person.facts.get(0).description);
  //    vertex.save();
  //    return vertex;
  //  }
  //
  //  private static OVertex createVertex(ODatabaseSession db, String user, GedcomFamily family) {
  //    OVertex vertex = db.newVertex("GedcomFamily");
  //    vertex.setProperty("user", user);
  //    vertex.setProperty("id", family.id);
  //    vertex.save();
  //    return vertex;
  //  }

  //  @Override
  //  public Collection<GedcomPerson> addGedcom(String user, String path) {
  //    try (ODatabaseSession db = mDbPool.acquire()) {
  //
  //      OVertex matt = createVertex(db, user, new GedcomPerson(1, new Fact("name", "matt")));
  //      OVertex david = createVertex(db, user, new GedcomPerson(2, new Fact("name", "david")));
  //      OVertex sarah = createVertex(db, user, new GedcomPerson(3, new Fact("name", "sarah")));
  //      OVertex mike = createVertex(db, user, new GedcomPerson(4, new Fact("name", "mike")));
  //      OVertex calvin = createVertex(db, user, new GedcomPerson(5, new Fact("name", "calvin")));
  //      OVertex dale = createVertex(db, user, new GedcomPerson(6, new Fact("name", "dale")));
  //
  //      OVertex mattChildFamily = createVertex(db, user, new GedcomFamily(1));
  //      OVertex davidChildFamily = createVertex(db, user, new GedcomFamily(2));
  //      OVertex sarahChildFamily = createVertex(db, user, new GedcomFamily(3));
  //      OVertex calvinChildFamily = createVertex(db, user, new GedcomFamily(4));
  //
  //      createChildEdge(matt, mattChildFamily);
  //      createChildEdge(david, davidChildFamily);
  //      createChildEdge(sarah, sarahChildFamily);
  //      createChildEdge(calvin, calvinChildFamily);
  //      createChildEdge(dale, davidChildFamily);
  //
  //      createParentEdge(matt, calvinChildFamily);
  //      createParentEdge(david, mattChildFamily);
  //      createParentEdge(sarah, mattChildFamily);
  //      createParentEdge(mike, davidChildFamily);
  //
  //    }
  //    return null;
  //  }

  @Override
  public void addPerson(String user) {
  }

  @Override
  public void addFamily(String user) {

  }

  @Override
  public GedcomPerson getPerson(String user, long id) {

    //    String query = "SELECT FROM GedcomPerson WHERE user = ? AND id = ?";
    //
    //    OResultSet rs;
    //    try (ODatabaseSession db = mDbPool.acquire()) {
    //      rs = db.query(query, user, id);
    //    }
    //
    //    GedcomPerson person = null;
    //    if (rs.hasNext()) {
    //      OResult vertex = rs.next();
    //      id = vertex.getProperty("id");
    //      person = new GedcomPerson(id, new Fact("name", vertex.getProperty("name")));
    //    }

    return null;
  }

  @Override
  public GedcomFamily getFamily(String user) {
    return null;
  }

  @Override
  public Collection<GedcomPerson> getAllPersons(String user) {
    return null;
  }

  @Override
  public Collection<GedcomFamily> getAllFamilies(String user) {
    return null;
  }

  @Override
  public long getPersonCount(String user) {
    return 0;
  }

  @Override
  public long getFamilyCount(String user) {
    return 0;
  }

  @Override
  public Collection<GedcomPerson> getAllPersons(String user, long id, int generations) {

    List<GedcomPerson> result;
    String query = "SELECT FROM GedcomPerson WHERE id = ? AND user = ?";


    return null;
  }

  @Override
  public Collection<GedcomFamily> getAllFamilies(String user, long id, int generations) {
    return null;
  }

  @Override
  public Collection<GedcomPerson> getDirectLine(String user, long id, int generations) {

    //    List<GedcomPerson> result;
    //    String query = "SELECT FROM (TRAVERSE out() FROM (SELECT FROM PERSON WHERE id = ? AND
    // user = " +
    //    "" + "?) WHILE $depth < ?) WHERE @class = 'GedcomPerson'";
    //    generations = (generations * 2) + 1; // Account for family vertices between people. +1
    //    // due to
    //    // < depth (change if using maxdepth to *2 - 1.
    //
    //    try (ODatabaseSession db = mDbPool.acquire()) {
    //      OResultSet rs = db.query(query, id, user, generations);
    //      result = getPersonList(rs);
    //    }

    return null;
  }

  @Override
  public void describe(String user) {

  }

  //  private static boolean isAuthenticatedUser(ODatabaseSession db, String username) {
  //    String query = "SELECT FROM User where username = ?";
  //    OResultSet rs = db.query(query, username);
  //    boolean result = false;
  //    if (rs.hasNext()) {
  //      result = true;
  //    }
  //    rs.close();
  //    return result;
  //  }


  //  private static List<GedcomPerson> getPersonList(OResultSet rs) {
  //    List<GedcomPerson> result = new ArrayList<>();
  //
  //    while (rs.hasNext()) {
  //      OResult vertex = rs.next();
  //      GedcomPerson person = new GedcomPerson(vertex.getProperty("id"));
  //      person.facts = new ArrayList<Fact>(Arrays.asList(new Fact[]{new Fact("name", vertex
  //              .getProperty("name"))}));
  //      result.add(person);
  //    }
  //    rs.close();
  //    return result;
  //  }
  //
  //  private static OVertex createVertex(ODatabaseSession db, String user, GedcomPerson person) {
  //    OVertex vertex = db.newVertex("GedcomPerson");
  //    vertex.setProperty("user", user);
  //    vertex.setProperty("id", person.id);
  //    vertex.setProperty("name", person.facts.get(0).description);
  //    vertex.save();
  //    return vertex;
  //  }
  //
  //  private static OVertex createVertex(ODatabaseSession db, String user, GedcomFamily family) {
  //    OVertex vertex = db.newVertex("GedcomFamily");
  //    vertex.setProperty("user", user);
  //    vertex.setProperty("id", family.id);
  //    vertex.save();
  //    return vertex;
  //  }

}
