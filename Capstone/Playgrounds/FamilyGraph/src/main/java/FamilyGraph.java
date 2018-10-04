import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class FamilyGraph {

  private static final int NON_EXISTENT = -1;

  private HashMap<Integer, Person> personMap = new HashMap<>();
  private HashMap<PartnershipKey, Partnership> familyMap = new HashMap<>();

  public class Person {

    private int id;
    private String name;
    private Partnership parents = new Partnership();
    private LinkedList<Partnership> partners = new LinkedList<>();

    public Person(IndividualConstruct individual) {
      this.id = individual.getId();
      this.name = individual.getName();
    }

    public int getId() {
      return id;
    }

    public Partnership getParents() {
      return parents;
    }

  } // class::Person

  public class Partnership {

    private static final int MAX_PERSONS_IN_PARTNERSHIP = 2;
    private static final int A = 0;
    private static final int B = 1;

    private Person[] partners = new Person[MAX_PERSONS_IN_PARTNERSHIP];
    private ArrayList<Person> children = new ArrayList<>(); // ArrayList vs LinkedList b/c most common operation is
    // iteration, not removal/insertion

    public Partnership() {

    }

    public Partnership(Person a, Person b) {
      this.partners[A] = a;
      this.partners[B] = b;
    }

    public boolean addChild(Person child) {
      return children.add(child);
    }

    public Person[] getPartners() {
      return partners;
    }

  } // class::Partnership

  public FamilyGraph() {

  }

  /**
   * Adds a new vertex to this graph if not already present; the vertex being derived from the
   * {@link IndividualConstruct}. In other words, if this graph contains no vertex, v, such that v.id ==
   * IndividualConstruct.getId, a new vertex is added to the graph. Either way, the id of the vertex is returned.
   *
   * @param individual object implementing IndividualConstruct from which a new vertex is to be created and added to
   *                   this graph.
   * @return id of the vertex, whether new or already contained in the graph.
   */
  public int addPerson(IndividualConstruct individual) {
    int id = individual.getId();
    if (!personMap.containsKey(id)) {
      Person person = new Person(individual);
      personMap.put(id, person);
    }
    /* TODO
     * Would it be better to return the DUPLICATE_ID (-1) if a vertex already exists? The benefit of the current
     * implementation is a valid handle for the vertex with the specified id is guaranteed to be returned. Should a
     * duplicate IndividualConstruct exist, the original handle will be returned for subsequent use in creating edges
     * between vertices. This eliminates the need to check for an error and make an additional call to a getter in
     * order to retrieve the correct handle. However, the addition of a duplicate IndividualConstruct will fail
     * silently. This implementation requires the user to use update and replace methods to modify the intended vertex.
     */
    return id;
  }

  public Person getPerson(int id) {
    /* TODO
     * Implement deep copy.
     */
    return personMap.get(id);
  }

  public void addParents(int childId, int fatherId, int motherId) {

    if (!contains(childId, fatherId, motherId)) {
      return;
    }

    Person child = personMap.get(childId);
    Person father = personMap.get(fatherId);
    Person mother = personMap.get(motherId);

    Partnership partnership = child.getParents();
    Person[] parents = partnership.getPartners();

    parents[0] = father;
    parents[1] = mother;

  }

  public PartnershipKey addFamily(int a, int b) {

    // Both partners must exist in the graph before a family can be defined between them.
    if (!contains(a, b)) {
      return null;
    }

    PartnershipKey partnershipKey = new PartnershipKey(a, b);
    if (!familyMap.containsKey(partnershipKey)) {
      Person personA = personMap.get(a); // May be null if a == NON_EXISTENT
      Person personB = personMap.get(b); // May be null if a == NON_EXISTENT
      Partnership partnership = new Partnership(personA, personB);
      familyMap.put(partnershipKey, partnership);
    }

    return partnershipKey;
  }

  public boolean addChild(PartnershipKey partnershipKey, int childId) {
    if (!familyMap.containsKey(partnershipKey)) {
      return false;
    }
    Partnership partnership = familyMap.get(partnershipKey);
    Person child = personMap.get(childId);
    return partnership.addChild(child);
  }

  public int size() {
    return personMap.size();
  }

  private boolean contains(int... ids) {
    for (int id : ids) {
      if (!personMap.containsKey(id)) {
        return false;
      }
    }
    return true;
  }

  public ArrayList<Person> getDirectLine(int rootId) {
    Person root = personMap.get(rootId);
    ArrayList<Person> directLine = new ArrayList<>();
    recurseParents(root, directLine);
    return directLine;
  }

  private void recurseParents(Person root, ArrayList<Person> persons) {

    persons.add(root);

    Partnership partnership = root.getParents();
    if (partnership == null) {
      return;
    }

    Person[] parents = partnership.getPartners();

    persons.add(parents[0]);
    persons.add(parents[1]);

    // breadth-first search
    recurseParents(parents[0], persons);
    recurseParents(parents[1], persons);
  }

} // class::FamilyGraph
