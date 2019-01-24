package core;

import java.util.ArrayList;
import java.util.List;

public class SerializableFamily extends AbstractSerializableVertex {

  List<SerializablePerson> parents = new ArrayList<>();
  List<SerializablePerson> children = new ArrayList<>();

  public SerializableFamily(long id, List<Fact> facts, List<Event> events) {
    super(id, facts, events);
  }
}
