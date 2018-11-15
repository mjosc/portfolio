import com.google.gson.annotations.JsonAdapter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class FamilyGraph {

  public static final String ILLEGAL_ARGUMENT = FamilyGraph.class + " does not contain all the specified arguments";

  private HashMap<Integer, Person> personMap = new HashMap<>(); // Person.id : Person
  private HashMap<Integer, Family> familyMap = new HashMap<>(); // Family.id : Family


  /**
   * Adds the specified {@link Person} to the graph, if not already present. In other words, if the graph does not
   * contain a Person x, such that x.id == p.id, p is added to the graph and this method returns true.
   *
   * @param p Person to be added to the graph.
   * @return true if the graph does not already contain the id of p.
   */
  public boolean addPerson(Person p) {
    int id = p.getId();
    if (personMap.containsKey(id)) {
      return false;
    }
    personMap.put(id, p);
    return true;
  }

  /**
   * Returns the {@link Person} as specified by the id argument, if contained in the graph. In other words, if the
   * graph contains Person p, such that p.id == id, p is returned; otherwise, null.
   *
   * @param id id of the Person to be retrieved from the graph.
   * @return the Person as specified by the id argument, if contained in the graph, null otherwise.
   */
  public Person getPerson(int id) {
    return personMap.get(id);
  }

  /**
   * Adds the specified {@link Family} to the graph, if not already present. In other words, if the graph does not
   * contain a Family x, such that x.id == f.id, f is added to the graph and this method returns true.
   *
   * @param f Family to be added to the graph.
   * @return true if the graph does not already contain the id of f.
   */
  public boolean addFamily(Family f) {
    int id = f.getId();
    if (familyMap.containsKey(id)) {
      return false;
    }
    familyMap.put(id, f);
    return true;
  }

  /**
   * Returns the {@link Family} as specified by the id argument, if contained in the graph. In other words, if the
   * graph contains Family f, such that f.id == id, f is returned; otherwise, null.
   *
   * @param id id of the Family to be retrieved from the graph.
   * @return the Family as specified by the id argument, if contained in the graph, null otherwise.
   */
  public Family getFamily(int id) {
    return familyMap.get(id);
  }

  public boolean addChildToFamily(Person child, Family f) {
    if (!personMap.containsKey(child.getId()) || !familyMap.containsKey(f.getId())) {
      throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
    }

    addChildEdge(child, f);

    return true;
  }

  public boolean addParentToFamily(Person parent, Family f) {
    if (!personMap.containsKey(parent.getId()) || !familyMap.containsKey(f.getId())) {
      throw new IllegalArgumentException(ILLEGAL_ARGUMENT);
    }

    int nParents = f.nParents;
    if (nParents >= f.MAX_PARENTS) {
      return false;
    }
    int index = nParents == 0 ? 0 : 1;
    addParentEdge(parent, f, index);
    return true;
  }

  private void addChildEdge(Person child, Family f) {
    f.addChild(child);
    child.addChildFamily(f);
  }

  private void addParentEdge(Person parent, Family f, int index) {
    f.addParent(parent, index);
    parent.addParentFamily(f);
  }

  /**
   * Retrieves the direct-line ancestry of the {@link Person} specified by the root argument. Root is also returned as
   * the first item in the ArrayList. In other words, if the graph contains Person p, such that p.id == root.id, an
   * ArrayList containing at least root is guaranteed to be returned.
   *
   * @param root Person for whom the direct-line ancestry is to be returned (the starting point).
   * @return the direct-line ancestry of root; possibly resulting in only root itself.
   */
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

    ArrayList<Family> childFamilies = root.childFamilies;
    if (childFamilies.size() < 1) {
      return;
    }

    /* TODO
     * Remove hard-coded index 0. The addition of the ArrayList to support multiple child families is for adopted
     * families and to have a more uniform json interface on the client-side.
     */
    Person[] parents = childFamilies.get(0).parents;
    Person parentA = parents[0];
    Person parentB = parents[1];

    if (parentA != null) { directLine.add(parentA); }
    if (parentB != null) { directLine.add(parentB); }

    if (parentA != null) { recurseDirectLine(parentA, directLine); }
    if (parentB != null) { recurseDirectLine(parentB, directLine); }

  }

  public static class Person {

    private int id;
    private String name;
    private int age;
    private LocalDate birthDate;
    private LocalDate deathDate;

    /* TODO
     * Possibly change this back to a single Family instance. Changed to support adoptive families and a more uniform
     * json interface.
     */
    @JsonAdapter(SerializerUtil.FamilyListSerializer.class)
    private ArrayList<Family> childFamilies = new ArrayList<>();

    @JsonAdapter(SerializerUtil.FamilyListSerializer.class)
    private ArrayList<Family> parentFamilies = new ArrayList<>();

    public Person(PersonConstruct pc) {
      this.id = pc.getId();
      this.name = pc.getName();
      this.age = pc.getAge();
      this.birthDate = pc.getBirthDate();
      this.deathDate = pc.getDeathDate();
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
      childFamilies.add(f);
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

    /**
     * {@link #id}
     */
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

} // End FamilyGraph
