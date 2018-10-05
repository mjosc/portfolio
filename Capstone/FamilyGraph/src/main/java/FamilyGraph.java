import com.google.gson.annotations.JsonAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class FamilyGraph {

  private HashMap<Integer, Person> personMap = new HashMap<>(); // Person.id : Person
  private HashMap<Integer, Family> familyMap = new HashMap<>(); // Family.id : Family

  public FamilyGraph() {
    // Not implemented.
  }

  public static class Person {

    private int id;
    private String name;
    @JsonAdapter(SerializerUtil.FamilySerializer.class)
    private Family childFamily;
    @JsonAdapter(SerializerUtil.FamilyListSerializer.class)
    private ArrayList<Family> parentFamilies = new ArrayList<>();

    public Person(PersonConstruct pc) {
      this.id = pc.getId();
      this.name = pc.getName();
    }

    public int getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    private void addParentFamily(Family f) {
      parentFamilies.add(f);
    }

    private void addChildFamily(Family f) {
      childFamily = f;
    }
  } // End Person

  public static class Family {

    private static final int MAX_PARENTS = 2;

    private int id;
    private int nParents = 0;
    @JsonAdapter(SerializerUtil.PersonArraySerializer.class)
    private Person[] parents = new Person[MAX_PARENTS];
    @JsonAdapter(SerializerUtil.PersonListSerializer.class)
    private ArrayList<Person> children = new ArrayList<>();

    public Family(FamilyConstruct fc) {
      this.id = fc.getId();
    }

    public int getId() {
      return id;
    }

    private void addParent(Person parent, int index) {
      parents[index] = parent;
      nParents++;
    }

    private void addChild(Person child) {
      children.add(child);
    }
  } // End Family

  public boolean addPerson(Person p) {
    int id = p.getId();
    if (personMap.containsKey(id)) {
      return false;
    }
    personMap.put(id, p);
    return true;
  }

  public Person getPerson(int id) {
    return personMap.get(id);
  }

  public boolean addFamily(Family f) {
    int id = f.getId();
    if (familyMap.containsKey(id)) {
      return false;
    }
    familyMap.put(id, f);
    return true;
  }

  public Family getFamily(int id) {
    return familyMap.get(id);
  }

  public boolean addChildToFamily(Person child, Family f) {
    if (!personMap.containsKey(child.getId()) || !familyMap.containsKey(f.getId())) {

      /* TODO
       * Throw Exception or return false? Returning false is more consistent with the other methods in this class and
       * other libraries I've seen/used. However, this provides no user-feedback as to what went wrong. For example, the
       * Person, the Family, or both may not have existed in the graph before this method call was made.
       */
      throw new IllegalArgumentException();
    }

    // Add a bidirectional edge.
    f.addChild(child);
    child.addChildFamily(f);

    return true;
  }

  public boolean addParentToFamily(Person parent, Family f) {
    if (!personMap.containsKey(parent.getId()) || !familyMap.containsKey(f.getId())) {
      /* TODO
       * See addChildToFamily for a similar concern.
       */
      throw new IllegalArgumentException();
    }

    int nParents = f.nParents;
    if (nParents >= f.MAX_PARENTS) {
      return false;
    }
    int index = nParents == 0 ? 0 : 1;
    // Add a bidirectional edge.
    f.addParent(parent, index);
    parent.addParentFamily(f);

    return true;
  }

  public ArrayList<Person> getDirectLineAncestry(Person root) {
    if (!personMap.containsKey(root.getId())) {
      return null;
    }

    ArrayList<Person> directLine = new ArrayList<>();
    directLine.add(root);
    recurseDirectLine(root, directLine);

    return directLine;
  }

  private void recurseDirectLine(Person root, ArrayList<Person> directLine) {

    /* TODO
     * Refactor.
     */

    Family childFamily = root.childFamily;
    if (childFamily == null) {
      return;
    }

    Person[] parents = childFamily.parents;
    Person parentA = parents[0];
    Person parentB = parents[1];

    if (parentA != null) { directLine.add(parentA); }
    if (parentB != null) { directLine.add(parentB); }

    if (parentA != null) { recurseDirectLine(parentA, directLine); }
    if (parentB != null) { recurseDirectLine(parentB, directLine); }

  }

} // End FamilyGraph
