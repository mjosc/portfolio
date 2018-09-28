import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Supplier;

public class DAG implements Graph<Person, DefaultEdge> {

  private int size = 0; // Number of vertices
  private HashMap<Integer, Person> vertexMap = new HashMap<>(); // Maps Person.id to Person
  private DirectedAcyclicGraph<Person, DefaultEdge> dag = new DirectedAcyclicGraph<>(DefaultEdge.class);

  public DAG() {

  }


  @Override
  public Set<DefaultEdge> getAllEdges(Person person, Person v1) {
    return dag.getAllEdges(person, v1);
  }

  @Override
  public DefaultEdge getEdge(Person person, Person v1) {
    return dag.getEdge(person, v1);
  }

  @Override
  public EdgeFactory<Person, DefaultEdge> getEdgeFactory() {
    return dag.getEdgeFactory();
  }

  @Override
  public Supplier<Person> getVertexSupplier() {
    return dag.getVertexSupplier();
  }

  @Override
  public Supplier<DefaultEdge> getEdgeSupplier() {
    return dag.getEdgeSupplier();
  }

  @Override
  public DefaultEdge addEdge(Person a, Person b) {

    int aId = a.getId();
    int bId = b.getId();

    a = vertexMap.get(aId);
    b = vertexMap.get(bId);

    return dag.addEdge(a, b);
  }

  @Override
  public boolean addEdge(Person person, Person v1, DefaultEdge defaultEdge) {
    return dag.addEdge(person, v1, defaultEdge);
  }

  @Override
  public Person addVertex() {
    return dag.addVertex();
  }

  /**
   * {@inheritDoc}
   * <p>
   * In addition to the described behavior of org.jgrapht.graph.DirectedAcyclicGraph.addVertex,
   * this method also ensures this graph never contains two or more vertices with duplicate ids.
   * If this graph already contains a vertex with the same id, the call leaves this graph
   * unchanged and returns false.
   * <p>
   * Id, as used here, refers to the id of the Person object stored as the vertex.
   *
   * @param person vertex to be added to this graph.
   * @return true if this graph did not already contain the specified vertex.
   */
  @Override
  public boolean addVertex(Person person) {

    int id = person.getId();
    if (vertexMap.containsKey(id)) {
      return false;
    }

    /* The previous value associated with this key will always be null so long as this method returns
     * false when vertexMap.containsKey returns true.
     *
     * This implementation is required—as opposed to the default behavior of
     * DirectedAcyclicGraph.addVertex—in order to guarantee the correct mapping between vertexMap and
     * the underlying graph structure. Otherwise, the graph may contain vertexes with the exact same
     * id and other member fields but different memory addresses.
     */
    vertexMap.put(id, person);
    size++;
    return dag.addVertex(person);
  }

  /**
   * Updates the specified vertex if already present. More formally, if the graph contains a vertex p
   * such that p.equals(person), the call updates p to contain fields equivalent to person. If no such
   * p exists, it is added to the graph.
   *
   * @param person vertex to updated (or added) in this graph.
   * @return true if this graph did not already contain the specified vertex.
   */
  public boolean updateVertex(Person person) {

    /* TODO
     *
     * Decide whether this method should add a vertex if person does not exist.
     */
    int id = person.getId();
    if (!vertexMap.containsKey(id)) {
      return addVertex(person);
    }

    /* TODO
     *
     * Iterate through all of the Person fields and copy person.field to p.field.
     */
    Person p = this.getVertex(id);
    p.setName(person.getName());

    return true;
  }

  @Override
  public boolean containsEdge(Person person, Person v1) {
    return dag.containsEdge(person, v1);
  }

  @Override
  public boolean containsEdge(DefaultEdge defaultEdge) {
    return dag.containsEdge(defaultEdge);
  }

  @Override
  public boolean containsVertex(Person person) {
    return dag.containsVertex(person);
  }

  public boolean containsVertex(int id) {
    return vertexMap.containsKey(id);
  }

  @Override
  public Set<DefaultEdge> edgeSet() {
    return dag.edgeSet();
  }

  @Override
  public int degreeOf(Person person) {
    return dag.degreeOf(person);
  }

  @Override
  public Set<DefaultEdge> edgesOf(Person person) {
    return dag.edgesOf(person);
  }

  @Override
  public int inDegreeOf(Person person) {
    return dag.inDegreeOf(person);
  }

  @Override
  public Set<DefaultEdge> incomingEdgesOf(Person person) {
    return dag.incomingEdgesOf(person);
  }

  @Override
  public int outDegreeOf(Person person) {
    return dag.outDegreeOf(person);
  }

  @Override
  public Set<DefaultEdge> outgoingEdgesOf(Person person) {
    return dag.outgoingEdgesOf(person);
  }

  @Override
  public boolean removeAllEdges(Collection<? extends DefaultEdge> collection) {
    return dag.removeAllEdges(collection);
  }

  @Override
  public Set<DefaultEdge> removeAllEdges(Person person, Person v1) {
    return dag.removeAllEdges(person, v1);
  }

  @Override
  public boolean removeAllVertices(Collection<? extends Person> collection) {
    return dag.removeAllVertices(collection);
  }

  @Override
  public DefaultEdge removeEdge(Person person, Person v1) {
    return dag.removeEdge(person, v1);
  }

  @Override
  public boolean removeEdge(DefaultEdge defaultEdge) {
    return dag.removeEdge(defaultEdge);
  }

  @Override
  public boolean removeVertex(Person person) {

    /* TODO
     *
     * Implement parallel to addVertex & updateVertex. Beware of edge removal.
     */
    return dag.removeVertex(person);
  }

  @Override
  public Set<Person> vertexSet() {
    return dag.vertexSet();
  }

  /**
   * Returns the vertex to which pertains the specified id, or null if this graph does not
   * contain the vertex with the provided id.
   * <p>
   * Id, as used here, refers to the id of the Person object stored as the vertex.
   *
   * @param id Person id of the Person (vertex) to be retrieved.
   * @return the Person (vertex) to which pertains the specified id, or null if this graph does
   * not contain the vertex.
   */
  public Person getVertex(int id) {
    return vertexMap.get(id);
  }

  @Override
  public Person getEdgeSource(DefaultEdge defaultEdge) {
    return dag.getEdgeSource(defaultEdge);
  }

  @Override
  public Person getEdgeTarget(DefaultEdge defaultEdge) {
    return dag.getEdgeTarget(defaultEdge);
  }

  @Override
  public GraphType getType() {
    return dag.getType();
  }

  @Override
  public double getEdgeWeight(DefaultEdge defaultEdge) {
    return dag.getEdgeWeight(defaultEdge);
  }

  @Override
  public void setEdgeWeight(DefaultEdge defaultEdge, double v) {
    dag.setEdgeWeight(defaultEdge, v);
  }

  /* TODO
   *
   * Reserve functionality for returning either number of vertices or number of edges.
   */
  public int size() {
    return size;
  }
}
