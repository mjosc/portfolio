import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * A simple nested-class with parallels to the FamilyGraph graph implementation. The purpose of this playground is to
 * explore custom implementations of JsonSerializer in order to avoid StackOverflowErrors due to cycles created
 * between the nested-classes.
 */
public class Playground {

  public static class InnerClassA {

    private int id;
    @JsonAdapter(InnerClassBListSerializer.class)
    private ArrayList<InnerClassB> listOfB;

    public InnerClassA(int id, InnerClassB... listOfB) {
      this.id = id;
      this.listOfB = new ArrayList<>(Arrays.asList(listOfB));
    }

    public int getId() {
      return id;
    }

    public void addInnerClassB(InnerClassB icb) {
      listOfB.add(icb);
    }
  }

  public static class InnerClassB {
    private int id;
    @JsonAdapter(InnerClassAListSerializer.class)
    private ArrayList<InnerClassA> listOfA;

    public InnerClassB(int id, InnerClassA... listOfA) {
      this.id = id;
      this.listOfA = new ArrayList<>(Arrays.asList(listOfA));
    }

    public int getId() {
      return id;
    }

    public void addInnerClassA(InnerClassA ica) {
      listOfA.add(ica);
    }

  }

  public static void main(String[] args) {

    InnerClassA ica1 = new Playground.InnerClassA(1);
    InnerClassA ica2 = new InnerClassA(2);

    InnerClassB icb1 = new InnerClassB(1);
    InnerClassB icb2 = new InnerClassB(2);

    // Create cycles to elicit StackOverflowError if the serializers are not implemented correctly.
    ica1.addInnerClassB(icb1);
    ica1.addInnerClassB(icb2);
    icb1.addInnerClassA(ica1);
    icb1.addInnerClassA(ica2);

    assertEquals(new Gson().toJson(ica1), "{\"id\":1,\"listOfB\":[1,2]}");
  }

}
