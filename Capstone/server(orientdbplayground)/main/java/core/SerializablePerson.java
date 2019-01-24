package core;

import java.util.ArrayList;
import java.util.List;

public class SerializablePerson extends AbstractSerializableVertex {

  private List<SerializableFamily> childFamilies = new ArrayList<>();
  private List<SerializableFamily> parentFamilies = new ArrayList<>();

  public SerializablePerson(long id, List<Fact> facts, List<Event> events) {
    super(id, facts, events);
  }
}
