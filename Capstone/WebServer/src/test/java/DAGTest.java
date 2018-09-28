import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

public class DAGTest extends TestCase {

  // https://jgrapht.org/guide/UserOverview.html#graph-structures
  // Note that the default graph implementations guarantee predictable ordering for the collections
  // that they maintain; so, for example, if you add vertices in the order [B, A, C], you can expect
  // to see them in that order when iterating over the vertex set. However, this is not a requirement
  // of the Graph interface, so other graph implementations are not guaranteed to honor it.

  private Person[] persons = new Person[2];
  private DAG dag = new DAG();

  @Before
  public void setUp() throws Exception {
    super.setUp();

    for (int i = 0; i < persons.length; i++) {
      char c = (char)(i + 97);
      persons[i] = new Person(i, String.valueOf(c));
    }

  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testAddVertex() throws IllegalAccessException {

    Person a = persons[0];
    Person b = persons[1];

    assertFalse(a.equals(b));
    assertTrue(dag.addVertex(a));
    assertTrue(dag.addVertex(b));
    assertFalse(dag.addVertex(a));
    assertFalse(dag.addVertex(b));

    Set<Person> vertexSet = dag.vertexSet();

    assertTrue(vertexSet.size() == dag.size());

    assertTrue(vertexSet.contains(a));
    assertTrue(vertexSet.contains(b));

  }

  @Test
  public void testUpdateVertex() {

    Person a = persons[0];
    Person b = persons[1];

    dag.addVertex(a);
    dag.addVertex(b);

    Person A = new Person(persons[0].getId(), "A");
    Person B = new Person(persons[1].getId(), "B");

    dag.updateVertex(A);
    dag.updateVertex(B);

    Set<Person> vertexSet = dag.vertexSet();

    assertFalse(vertexSet.contains(A));
    assertFalse(vertexSet.contains(B));

    Object[] vertexArr = vertexSet.toArray();
    assertTrue(vertexArr[0].equals(persons[0]));
    assertTrue(vertexArr[1].equals(persons[1]));
  }
}
