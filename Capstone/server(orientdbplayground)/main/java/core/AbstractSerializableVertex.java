package core;


import java.util.ArrayList;
import java.util.List;

/**
 * An abstraction container for a vertex's id, facts, and events.
 */
public abstract class AbstractSerializableVertex {

  protected long id;
  protected List<Fact> facts = new ArrayList<>();
  protected List<Event> events = new ArrayList<>();

  /*
   * All lists should be non-null in order to simplify serialization. Storage requirements for large
   * data sets may require these initializations be replaced with custom serializer logic.
   *
   * Example: { facts: [] } vs. {}
   */

  protected AbstractSerializableVertex(long id, List<Fact> facts, List<Event> events) {
    this.id = id;
    this.facts = facts;
    this.events = events;
  }

}
