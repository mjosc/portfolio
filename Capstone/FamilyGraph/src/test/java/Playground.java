import com.google.gson.Gson;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Playground {
  public static void main(String[] args) {

    FamilyGraph fGraph = new FamilyGraph();

    HashMap<Integer, FamilyGraph.Person> people = new HashMap<Integer, FamilyGraph.Person>() {{

      // Generation 1
      put(1, new FamilyGraph.Person(new TestIndividual(1, "Calvin Carter", 27, LocalDate.of(1991, 3, 20), null)));
      // Generation 2
      put(2, new FamilyGraph.Person(new TestIndividual(2, "Steve Carter", 53, LocalDate.of(1991, 3, 20), null)));
      put(3, new FamilyGraph.Person(new TestIndividual(3, "Helen Wood", 46, LocalDate.of(1991, 3, 20), null)));
      // Generation 3
      put(4, new FamilyGraph.Person(new TestIndividual(4, "Philip Carter", 88, LocalDate.of(1991, 3, 20),
              null)));
      put(5, new FamilyGraph.Person(new TestIndividual(5, "Julie Harris", 72, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(6, new FamilyGraph.Person(new TestIndividual(6, "Jon Wood", 80, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(7, new FamilyGraph.Person(new TestIndividual(7, "Mary Allen", 79, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      // Generation 4
      put(8, new FamilyGraph.Person(new TestIndividual(8, "Steven Carter", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(9, new FamilyGraph.Person(new TestIndividual(9, "Frances Flores", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(10, new FamilyGraph.Person(new TestIndividual(10, "Mark Harris", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(11, new FamilyGraph.Person(new TestIndividual(11, "Julie Ryan", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(12, new FamilyGraph.Person(new TestIndividual(12, "John Wood", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(13, new FamilyGraph.Person(new TestIndividual(13, "Marie Henderson", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(14, new FamilyGraph.Person(new TestIndividual(14, "Mark Allen", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(15, new FamilyGraph.Person(new TestIndividual(15, "Anne Torres", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      // Generation 5
      put(16, new FamilyGraph.Person(new TestIndividual(16, "Stephen Carter", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(17, new FamilyGraph.Person(new TestIndividual(17, "Alicia Moonves", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(18, new FamilyGraph.Person(new TestIndividual(18, "Brian Flores", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(19, new FamilyGraph.Person(new TestIndividual(19, "Deborah Jackson", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(20, new FamilyGraph.Person(new TestIndividual(20, "Christian Harris", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(21, new FamilyGraph.Person(new TestIndividual(21, "Patricia Lewis", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(22, new FamilyGraph.Person(new TestIndividual(22, "William Ryan", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(23, new FamilyGraph.Person(new TestIndividual(23, "Elizabeth Johnston", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(24, new FamilyGraph.Person(new TestIndividual(24, "Mark Woodward", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(25, new FamilyGraph.Person(new TestIndividual(25, "Martha Smith", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(26, new FamilyGraph.Person(new TestIndividual(26, "Albert Henderson", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(27, new FamilyGraph.Person(new TestIndividual(27, "Catherine Smith", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(28, new FamilyGraph.Person(new TestIndividual(28, "Robert Allen", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(29, new FamilyGraph.Person(new TestIndividual(29, "Oliva Bishop", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(30, new FamilyGraph.Person(new TestIndividual(30, "Marco Torres", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));
      put(31, new FamilyGraph.Person(new TestIndividual(31, "Maria Sanders", 0, LocalDate.of(1991, 3, 20),
              LocalDate.of(1991, 3, 20))));

    }};

    HashMap<Integer, FamilyGraph.Family> families = new HashMap<Integer, FamilyGraph.Family>() {{

      // Generation 2
      put(1, new FamilyGraph.Family(new TestFamily(1))); // father & mother
      // Generation 3
      put(2, new FamilyGraph.Family(new TestFamily(2))); // grandFatherFather & grandMotherFather
      put(3, new FamilyGraph.Family(new TestFamily(3))); // grandFatherMother & grandMotherMother
      // Generation 4
      put(4, new FamilyGraph.Family(new TestFamily(4)));
      put(5, new FamilyGraph.Family(new TestFamily(5)));
      put(6, new FamilyGraph.Family(new TestFamily(6)));
      put(7, new FamilyGraph.Family(new TestFamily(7)));
      // Generation 5
      put(8, new FamilyGraph.Family(new TestFamily(8)));
      put(9, new FamilyGraph.Family(new TestFamily(9)));
      put(10, new FamilyGraph.Family(new TestFamily(10)));
      put(11, new FamilyGraph.Family(new TestFamily(11)));
      put(12, new FamilyGraph.Family(new TestFamily(12)));
      put(13, new FamilyGraph.Family(new TestFamily(13)));
      put(14, new FamilyGraph.Family(new TestFamily(14)));
      put(15, new FamilyGraph.Family(new TestFamily(15)));

    }};

    people.keySet().forEach(key -> fGraph.addPerson(people.get(key)));
    families.keySet().forEach(key -> fGraph.addFamily(families.get(key)));

    linkFamily(fGraph, people.get(2), people.get(3), people.get(1), families.get(1));

    linkFamily(fGraph, people.get(4), people.get(5), people.get(2), families.get(2));
    linkFamily(fGraph, people.get(6), people.get(7), people.get(3), families.get(3));

    linkFamily(fGraph, people.get(8), people.get(9), people.get(4), families.get(4));
    linkFamily(fGraph, people.get(10), people.get(11), people.get(5), families.get(5));
    linkFamily(fGraph, people.get(12), people.get(13), people.get(6), families.get(6));
    linkFamily(fGraph, people.get(14), people.get(15), people.get(7), families.get(7));

    linkFamily(fGraph, people.get(16), people.get(17), people.get(8), families.get(8));
    linkFamily(fGraph, people.get(18), people.get(19), people.get(9), families.get(9));
    linkFamily(fGraph, people.get(20), people.get(21), people.get(10), families.get(10));
    linkFamily(fGraph, people.get(22), people.get(23), people.get(11), families.get(11));
    linkFamily(fGraph, people.get(24), people.get(25), people.get(12), families.get(12));
    linkFamily(fGraph, people.get(26), people.get(27), people.get(13), families.get(13));
    linkFamily(fGraph, people.get(28), people.get(29), people.get(14), families.get(14));
    linkFamily(fGraph, people.get(30), people.get(31), people.get(15), families.get(15));

    ArrayList<FamilyGraph.Person> myDirectLine = fGraph.getDirectLineAncestry(people.get(1));
    System.out.println(new Gson().toJsonTree(myDirectLine));

  }

  public static void linkFamily(FamilyGraph fgraph, FamilyGraph.Person parent1, FamilyGraph.Person parent2,
                                FamilyGraph.Person child, FamilyGraph.Family family) {
    fgraph.addParentToFamily(parent1, family);
    fgraph.addParentToFamily(parent2, family);
    fgraph.addChildToFamily(child, family);
  }

}
