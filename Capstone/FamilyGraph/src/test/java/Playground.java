import com.google.gson.Gson;

import java.util.ArrayList;

public class Playground {
  public static void main(String[] args) {

    FamilyGraph fGraph = new FamilyGraph();

    FamilyGraph.Person me = new FamilyGraph.Person(new TestIndividual(1, "me"));
    FamilyGraph.Person father = new FamilyGraph.Person(new TestIndividual(2, "father"));
    FamilyGraph.Person mother = new FamilyGraph.Person(new TestIndividual(3, "mother"));
    FamilyGraph.Person grandFatherFather = new FamilyGraph.Person(new TestIndividual(4, "grandFatherFather"));
    FamilyGraph.Person grandMotherFather = new FamilyGraph.Person(new TestIndividual(5, "grandMotherFather"));
    FamilyGraph.Person grandFatherMother = new FamilyGraph.Person(new TestIndividual(6, "grandFatherMother"));
    FamilyGraph.Person grandMotherMother = new FamilyGraph.Person(new TestIndividual(7, "grandMotherMother"));

    FamilyGraph.Family f1 = new FamilyGraph.Family(new TestFamily(1)); // father & mother
    FamilyGraph.Family f2 = new FamilyGraph.Family(new TestFamily(2)); // grandFatherFather & grandMotherFather
    FamilyGraph.Family f3 = new FamilyGraph.Family(new TestFamily(3)); // grandFatherMother & grandMotherMother

    fGraph.addPerson(me);
    fGraph.addPerson(father);
    fGraph.addPerson(mother);
    fGraph.addPerson(grandFatherFather);
    fGraph.addPerson(grandMotherFather);
    fGraph.addPerson(grandFatherMother);
    fGraph.addPerson(grandMotherMother);

    fGraph.addFamily(f1);
    fGraph.addFamily(f2);
    fGraph.addFamily(f3);

    fGraph.addParentToFamily(father, f1);
    fGraph.addParentToFamily(mother, f1);
    fGraph.addParentToFamily(grandFatherFather, f2);
    fGraph.addParentToFamily(grandMotherFather, f2);
    fGraph.addParentToFamily(grandFatherMother, f3);
    fGraph.addParentToFamily(grandMotherMother, f3);

    fGraph.addChildToFamily(me, f1);
    fGraph.addChildToFamily(father, f2);
    fGraph.addChildToFamily(mother, f3);

    ArrayList<FamilyGraph.Person> myDirectLine = fGraph.getDirectLineAncestry(me);
    System.out.println(myDirectLine.size());
    myDirectLine.forEach(person -> System.out.println(new Gson().toJson(person)));

  }
}
