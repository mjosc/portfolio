public class Edge {

  private Person source;
  private Person target;

  Edge(Person source, Person target) {
    this.source = source;
    this.target = target;
  }

  public Person getSource() {
    return source;
  }

  public Person getTarget() {
    return target;
  }
}
