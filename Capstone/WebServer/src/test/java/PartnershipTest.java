import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class PartnershipTest {

  private ArrayList<Partnership> partnerships = new ArrayList<>();

  @Before
  public void setUp() throws Exception {
    for (int i = 0; i < 100; i++) {
      partnerships.add(new Partnership(i));
    }
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void getId() {
    int id = 0;
    for (Partnership partnership : partnerships) {
      System.out.println(partnership.getId());
    }
  }
}