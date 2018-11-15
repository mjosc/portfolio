import javafx.util.Pair;

import java.util.HashMap;
import java.util.HashSet;

public class Graph {

  private HashMap<Integer, Person> vertexMap = new HashMap<>();
  private HashMap<Edge, Partnership> edgeMap = new HashMap<>(); // [Person, Person] : Partnership

  public Graph() {

  }

  /**
   * Adds the specified {@link Person} to this graph. For the Person to be added, the graph must not contain
   * a reference to the same Person. In other words, where v is the graph's current reference to p, the id of
   * v must not be equal to p such that v.id == p.id. If such a reference is found, the operation is abandoned
   * the graph is unchanged, and this method returns false.
   *
   * @param p Person to be added to the graph.
   * @return true if this graph did not already contain a reference to the specified Person.
   */
  public boolean addPerson(Person p) {
    if (vertexMap.containsKey(p.getId())) {
      return false;
    }
    vertexMap.put(p.getId(), p);
    return true;
  }

  /**
   * Returns from this graph the Person with the specified id, so long as such a Person is contained in the Graph.
   * Otherwise returns null. In other words the Person p is returned where p.id == id.
   *
   * @param id id of the Person to be returned from the graph, if present.
   * @return the Person with the specified id or null if none can be found.
   */
  public Person getPerson(int id) {
    return vertexMap.get(id);
  }

  public void addParent() {

  }

//  public Partnership addParentalRelationship(Person child, Person parent) {
//
//    // Both vertices must first exist in the graph.
//    if (!containsVertices(child, parent)) {
//      return null;
//    }
//
//    Person c = vertexMap.get(child.getId());
//    Person p = vertexMap.get(parent.getId());
//
//    /* TODO
//     *
//     * Throw exception in place of null? Inefficient validation?
//     */
//    if (!c.equals(child) || !p.equals(parent)) {
//      return null;
//    }
//
//    // Ensure this relationship does not already exist.
//    Edge edge = new Edge(child, parent);
//    if (edgeMap.containsKey(edge)) {
//      return null;
//    }
//
////    Partnership partnership
////    edgeMap.put(edge, );
//
//    return null;
//
//  }

  private boolean containsVertices(Person... persons) {
    for (Person person : persons) {
      if (!vertexMap.containsKey(person.getId())) {
        return false;
      }
    }
    return true;
  }







}
