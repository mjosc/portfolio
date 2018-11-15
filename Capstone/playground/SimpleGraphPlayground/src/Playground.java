import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class Playground {

  public static void main(String[] args) {

    SimpleGraph graph = new SimpleGraph();
    SimpleGraph.SimpleNode before = graph.AddNode("@P1@"); // This should return null
    SimpleGraph.SimpleNode after = graph.getNode("@P1@");
    System.out.println(after.Xref);



  }

  public static class SimpleGraph {

    // Hashmap to integer which corresponds to string "@P1" etc. to save space from String objects?? Is this valid in
    // Java with String objects and Integer wrapper? How many bytes each???
    private HashMap<String, SimpleNode> nodes = new HashMap<>();

    public class SimpleNode {

      private String Xref; // ID
      private LinkedList<SimpleNode> neighbors = new LinkedList<>();

      private SimpleNode(String Xref) {
        this.Xref = Xref;
      }
    }

    private SimpleNode AddNode(String Xref) {
      return nodes.put(Xref, new SimpleNode(Xref));
    }

    private SimpleNode getNode(String Xref) {
      return nodes.get(Xref);
    }

  }
}
