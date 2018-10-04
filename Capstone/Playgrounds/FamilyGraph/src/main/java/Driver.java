import java.util.ArrayList;

public class Driver {

  public static void main(String[] args) {

//    PartnershipKey a = new PartnershipKey(0, 1);
//    PartnershipKey b = new PartnershipKey(1, 0);
//
//    System.out.println(a.equals(b));
//    System.out.println(b.equals(a));
//
//    System.out.println(a.hashCode());
//    System.out.println(b.hashCode());
//
//    HashMap<PartnershipKey, Integer> hashMap = new HashMap<>();
//    hashMap.put(a, 1);
//    hashMap.put(b, 1);
//
//    System.out.println(hashMap.size());

    FamilyGraph fgraph = new FamilyGraph();

    int child, father, mother;
    PartnershipKey partnershipKey;

    child = fgraph.addPerson(new TestIndividual(1, "matt"));
    father = fgraph.addPerson(new TestIndividual(2, "david"));
    mother = fgraph.addPerson(new TestIndividual(3, "sarah"));

    fgraph.addParents(child, father, mother);

    ArrayList<FamilyGraph.Person> directLine = fgraph.getDirectLine(child);

    directLine.forEach(person -> System.out.println(person.getId()));

//    child = father;
//
//
//    fgraph.addPerson(new TestIndividual(4, "mike"));
//    fgraph.addPerson(new TestIndividual(5, "virginia"));
//    fgraph.addPerson(new TestIndividual(6, "ned"));
//    fgraph.addPerson(new TestIndividual(7, "wanda"));

  }
}
