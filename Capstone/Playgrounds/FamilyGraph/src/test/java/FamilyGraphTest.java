import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class FamilyGraphTest extends TestCase {

  private FamilyGraph fgraph = new FamilyGraph();
  private ArrayList<TestIndividual> individuals = new ArrayList<>();

  @Before
  public void setUp() throws Exception {
    super.setUp();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testAddPerson() {

    TestIndividual a = new TestIndividual(1, "a");
    TestIndividual b = new TestIndividual(2, "b");
    TestIndividual c = new TestIndividual(3, "c");

    TestIndividual d = new TestIndividual(1, "d");

    Person person;

    person = fgraph.addPerson(a); assertEquals(1, person.getId());
    person = fgraph.addPerson(b); assertEquals(2, person.getId());
    person = fgraph.addPerson(c); assertEquals(3, person.getId());

    /* Adding the same vertex twice or two different vertices with the same id should result in an aborted operation no
     * matter the uniqueness of any other member variables.
     */
    Person pa = fgraph.addPerson(a); assertEquals(1, pa.getId());
    Person pb = fgraph.addPerson(d); assertEquals(1, pb.getId());

    // The previous two calls should have aborted before adding an additional vertex.
    assertSame(pa, pb);
    assertTrue(fgraph.nPersons() == 3);

  }

//  public void testAddPerson() {
//
//    TestIndividual a = new TestIndividual(1, "a");
//    TestIndividual b = new TestIndividual(2, "b");
//    TestIndividual c = new TestIndividual(3, "c");
//
//    TestIndividual d = new TestIndividual(1, "d");
//
//    int handle;
//
//    handle = fgraph.addPerson(a); assertEquals(1, handle);
//    handle = fgraph.addPerson(b); assertEquals(2, handle);
//    handle = fgraph.addPerson(c); assertEquals(3, handle);
//
//    /* Adding the same vertex 2x or two different vertices with the same id should result in an aborted operation
//     * no matter the uniqueness of any other member variables.
//     */
//    handle = fgraph.addPerson(a); assertEquals(1, handle);
//    handle = fgraph.addPerson(d); assertEquals(1, handle);
//
//    // The previous two calls should have aborted before adding an additional vertex.
//    assertTrue(fgraph.size() == 3);
//  }
}