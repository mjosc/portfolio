import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class FamilyGraph {

  private HashMap<Integer, Person> personMap = new HashMap<>();
  private HashMap<Integer, Family> familyMap = new HashMap<>();

  public FamilyGraph() {

  }

  /**
   * Adds a new {@link Person} to this graph if not already present; the Person being derived from the
   * {@link IndividualConstruct}. In other words, if this graph contains no Person, p, such that
   * p.id == ic.getId, a new Person is added to the graph and that Person is returned from this method.
   * Otherwise, the Person with the corresponding id is returned from the graph.
   *
   * @param ic implemenation of IndividualConstruct from which a new Person is to be created and added to this
   *           graph.
   * @return Person contained in the graph with an id corresponding to ic.getId, whether said Person was
   * created as a result of this method call or existed prior.
   */
  public Person addPerson(IndividualConstruct ic) {
    int id = ic.getId();
    Person person;
    if (!personMap.containsKey(id)) {
      person = new Person(ic);
      personMap.put(id, person);
    } else {
      person = personMap.get(id);
    }
    return person;
  }

  /**
   * Returns the {@link Person} as specified by the id, if said Person is present in this graph. In other words, if this
   * graph contains a Person, p, such that p.id == id, this Person is returned. If p does not exist in the graph,
   * returns null.
   *
   * @param id id of the Person to be returned.
   * @return Person with the specified id if present in the graph, null otherwise.
   */
  public Person getPerson(int id) {
    /* TODO
     * Implement deep copy?
     */
    return personMap.get(id);
  }

  public Family addFamily(FamilyConstruct fc) {
    int id = fc.getId();
    Family family;
    if (!familyMap.containsKey(id)) {
      family = new Family(fc);
      familyMap.put(id, family);
    } else {
      family = familyMap.get(id);
    }
    return family;
  }

  public Family getFamily(int id) {
    /* TODO
     * Implement deep copy?
     */
    return familyMap.get(id);
  }

  public void addParentsToFamily(Family family, Person parentA, Person parentB) {
    if (!familyMap.containsKey(family.getId()) || !contains(parentA, parentB)) {
      throw new IllegalArgumentException();
    }
    family.addParents(parentA, parentB);
    parentA.addParentFamily(family);
    parentB.addParentFamily(family);
  }

  public void addChildrenToFamily(Family family, Person... children) {
    if (!familyMap.containsKey(family.getId()) || !contains(children)) {
      throw new IllegalArgumentException();
    }
    family.addChildren(children);
    for (Person child : children) {
      child.addChildFamily(family);
    }
  }

  public ArrayList<Person> getDirectLine(int rootId) {
    ArrayList<Person> result = new ArrayList<>();
    Person root = personMap.get(rootId);
    result.add(root);
    recurseDirectLine(root, result);
    return result;
  }

  private void recurseDirectLine(Person root, ArrayList<Person> result) {

    System.out.println(root.getName());
    Family childFamily = root.childFamily;
    if (childFamily == null) {
      return;
    }

    Person[] parents = childFamily.parents;

    result.add(parents[0]);
    result.add(parents[1]);

    recurseDirectLine(parents[0], result);
    recurseDirectLine(parents[1], result);

  }

  public int nPersons() {
    return personMap.size();
  }

  public int nFamilies() {
    return familyMap.size();
  }

  private boolean contains(Person... persons) {
    for (Person p : persons) {
      if (!personMap.containsKey(p.getId())) {
        return false;
      }
    }
    return true;
  }

} // class::FamilyGraph
