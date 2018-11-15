import javafx.util.Pair;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.StringWithCustomFacts;

import java.util.HashMap;
import java.util.LinkedList;

public class CustomGraph {

  private int nextPartnershipId = 0;

  private HashMap<Integer, CustomPerson> vertexMap = new HashMap<>(); // Person.id : Person
  private HashMap<Integer, Partnership> edgeMap = new HashMap<>(); // Person.id : Partnership

  public CustomGraph() {

  }

//  public CustomPerson addPerson(CustomPerson p) {
//
//  }

//  // returns null if object was null or no object was stored there previously (see hashmap docs)
//  public CustomPerson addPerson(CustomPerson person) {
//
//    return vertexMap.put(person.getId(), person);
//  }
//
//  // returns null if object does not exist
//  public CustomPerson getPerson(int id) {
//    return vertexMap.get(id);
//  }
//
//  private boolean containsPersons(CustomPerson... persons) {
//    for (CustomPerson p : persons) {
//      if (!vertexMap.containsKey(p.getId())) {
//        return false;
//      }
//    }
//    return true;
//  }
//
//  public Partnership addPartnership(CustomPerson a, CustomPerson b) {
//
//    if (!containsPersons(a, b)) {
//      return null;
//    }
//
//    Partnership partnership = new Partnership(nextPartnershipId);
//
//  }
//
//  // Get back the edge (relationship) connecting the child to parent, null on failure
//  // TODO: Return boolean instead? Best practice?
//  public Partnership addParent(CustomPerson child, CustomPerson parent) {
//
//    int childId = child.getId();
//    int parentId = parent.getId();
//
//    // The vertices must exist in the graph before creating a relationship between them.
//    if (!vertexMap.containsKey(childId) || !vertexMap.containsKey(parentId)) {
//      return null; // TODO: Throw exception?
//    }
//
//    Partnership parents = child.getParents();
//    if (parents == null) {
//      parents = new Partnership(nextPartnershipId);
//      parents.addPartner(parent); // shouldn't return false if this is a new Partnership (is this safe practice?)
//
//      return edgeMap.put(nextPartnershipId++, parents);
//    }
//
//    if (!parents.addPartner(parent)) {
//      return null; // TODO: Throw exception?
//    }
//
//    return parents;
//
//  }
//
//  public Partnership getParents(int id) {
//    CustomPerson child = vertexMap.get(id);
//    return child.getParents();
//  }




}
