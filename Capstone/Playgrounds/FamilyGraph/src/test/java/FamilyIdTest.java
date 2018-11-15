import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

public class FamilyIdTest extends TestCase {

  private FamilyId a = new FamilyId(0, 1);
  private FamilyId b = new FamilyId(1, 0);

  private FamilyId c = new FamilyId(0, 0);

  @Before
  public void setUp() throws Exception {
    super.setUp();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testEquals() {

    assertTrue(a.equals(b));
    assertFalse(a.equals(c));

    // HashMap is used here as it is the most common use case for the FamilyGraph.
    HashMap<FamilyId, Integer> hashMap = new HashMap<>();
    hashMap.put(a, 0);
    hashMap.put(b, 1);
    hashMap.put(c, 2);

    /* Though they do not point to the same address, FamilyId a and b are functionally equivalent. The value
     * mapped to a should be overwritten by the value mapped to b. This is as though a:0 was assigned and then
     * overwritten by a:1;
     */
    assertTrue(hashMap.get(a) == 1);
    assertTrue(hashMap.get(b) == 1);
    assertTrue(hashMap.get(c) == 2);

    // FamilyId a and b are the same key, as far as the HashMap is concerned.
    assertTrue(hashMap.size() == 2);

  }

  @Test
  public void testHashCode() {

    assertTrue(a.hashCode() == b.hashCode());
    assertFalse(b.hashCode() == c.hashCode());

  }
}