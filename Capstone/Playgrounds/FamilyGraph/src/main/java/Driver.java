import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Driver {

  public static void main(String[] args) {

    FamilyGraph fgraph = new FamilyGraph();

    Person matt = fgraph.addPerson(new TestIndividual(1, "matt"));
    Person david = fgraph.addPerson(new TestIndividual(2, "david"));
    Person sarah = fgraph.addPerson(new TestIndividual(3, "sarah"));
    Person mike = fgraph.addPerson(new TestIndividual(4, "mike"));
    Person virginia = fgraph.addPerson(new TestIndividual(5, "virginia"));
    Person ned = fgraph.addPerson(new TestIndividual(6, "ned"));
    Person wanda = fgraph.addPerson(new TestIndividual(7, "wanda"));

    Family f1 = fgraph.addFamily(new TestFamily(1)); // david & sarah
    Family f2 = fgraph.addFamily(new TestFamily(2)); // mike & virginia
    Family f3 = fgraph.addFamily(new TestFamily(3)); // ned & wanda

    fgraph.addParentsToFamily(f1, david, sarah);
    fgraph.addParentsToFamily(f2, mike, virginia);
    fgraph.addParentsToFamily(f3, ned, wanda);

    fgraph.addChildrenToFamily(f1, matt);
    fgraph.addChildrenToFamily(f2, david);
    fgraph.addChildrenToFamily(f3, sarah);

    ArrayList<Person> directLineMatt = fgraph.getDirectLine(matt.getId());
    directLineMatt.forEach(person -> System.out.println(person.getName()));

    System.out.println(new Gson().toJson(matt));

//    Driver.printPersonLinks(ned);

  }

  public static void printPersonLinks(Person person) {
    System.out.println(person.getName());
    System.out.println("nParentFam: " + person.parentFamilies.size());
    System.out.println("nChildFam: " + (person.childFamily == null ? 0: 1));

    Family childFam = person.childFamily;

    System.out.println("parent: " + (childFam == null ? "n/a" : childFam.parents[0].getName()));
    System.out.println("parent: " + (childFam == null ? "n/a" : childFam.parents[1].getName()));

  }
}
