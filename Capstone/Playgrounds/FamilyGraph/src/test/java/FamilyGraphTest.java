import junit.framework.TestCase;

import java.util.ArrayList;

public class FamilyGraphTest extends TestCase {

  private FamilyGraph fgraph = new FamilyGraph();
  private ArrayList<TestIndividual> individuals = new ArrayList<>();

  public void setUp() throws Exception {
    super.setUp();
  }

  public void tearDown() throws Exception {
  }

  public void testAddPerson() {

    TestIndividual a = new TestIndividual(1, "a");
    TestIndividual b = new TestIndividual(2, "b");
    TestIndividual c = new TestIndividual(3, "c");

    TestIndividual d = new TestIndividual(1, "d");

    int handle;

    handle = fgraph.addPerson(a); assertEquals(1, handle);
    handle = fgraph.addPerson(b); assertEquals(2, handle);
    handle = fgraph.addPerson(c); assertEquals(3, handle);

    /* Adding the same vertex 2x or two different vertices with the same id should result in an aborted operation
     * no matter the uniqueness of any other member variables.
     */
    handle = fgraph.addPerson(a); assertEquals(1, handle);
    handle = fgraph.addPerson(d); assertEquals(1, handle);

    // The previous two calls should have aborted before adding an additional vertex.
    assertTrue(fgraph.size() == 3);
  }
}