package core;

import java.util.List;

public interface Vertex {
  long id();
  List<Fact> facts();
  List<Event> events();

  void id(long id);
  void facts(List<Fact> facts);
  void events(List<Event> events);
}
