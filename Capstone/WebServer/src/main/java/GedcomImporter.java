import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.*;
import org.gedcom4j.parser.GedcomParser;

import java.io.IOException;
import java.util.*;

public class GedcomImporter {

  public static final int FATHER_INDEX = 0;
  public static final int MOTHER_INDEX = 1;

  private Gedcom gedcom;
  private DAG dag = new DAG();


  public GedcomImporter(String filename) throws IOException, GedcomParserException {

    GedcomParser gp = new GedcomParser();
    gp.load(filename);

    gedcom = gp.getGedcom();

    Map<String, Individual> individualMap = gedcom.getIndividuals();


    for (String xref : individualMap.keySet()) {

      Individual child = individualMap.get(xref);
      Person c = new Person(child);
      dag.addVertex(c);
      List<FamilyChild> familiesWhereChild = child.getFamiliesWhereChild();

      if (familiesWhereChild == null) {
        continue;
      }

      for (FamilyChild fc : familiesWhereChild) {


        Family family = fc.getFamily();

        Individual father = family.getHusband().getIndividual();
        Individual mother = family.getWife().getIndividual();

        Person f = new Person(father);
        Person m = new Person(mother);

        dag.addVertex(f);
        dag.addVertex(m);

        dag.addEdge(c, f);
        dag.addEdge(c, m);

      }

    }








  }



  private int xrefToId(String xref) {
    String id = xref.substring(2, xref.length() - 1);
    return Integer.parseInt(id);
  }

  //  private void printAllFamilies() {
  //
  //    StringBuilder output = new StringBuilder();
  //
  //    for (String xref : familyXrefs) {
  //      Family f = families.get(xref);
  //      output.append(xref + "\n" + f.getHusband() + "\n" + f.getWife() + "\n");
  //    }
  //
  //    System.out.println(output.toString());
  //  }

  /**
   * Prints the Xref of all Individuals followed by the Individual's formatted name.
   * <p>
   * The output is ordered by generation, youngest to eldest. However, the ordering is not
   * date-based.
   * <p>
   * See the gedcom4j Individual documentation for information about the formatted name.
   */
  public void printAllIndividuals() {

    /*
     * TODO
     *
     * For non-direct line data, the distinction between generations must be made clearer.
     */

    Map<String, Individual> individuals = gedcom.getIndividuals();
    StringBuilder output = new StringBuilder();

    for (int i = 1; i <= individuals.size(); i++) {
      String key = "@P" + i + "@"; // Xref: "@P1@"
      output.append(key + ", " + individuals.get(key).getFormattedName() + "\n");
    }

    System.out.println(output.toString());
  }

//  Person personChild = null;
//
//    for (int id = 1; id < individualMap.size(); id++) {
//    String xref = "@P" + id + "@";
//
//    Individual individualChild = individualMap.get(xref);
//
//
//    if (!dag.containsVertex(id)) {
//      System.out.println("Does not contain: " + personChild.getName() + " / " + personChild + " xref: " + id);
//      personChild = new Person(id, individualChild.getFormattedName());
//      dag.addVertex(personChild);
//    } else {
//      System.out.println("Already contains: " + personChild.getName() + " / " + personChild + " xref: " + id);
//    }
//
//    List<FamilyChild> familiesWhereChild = individualChild.getFamiliesWhereChild();
//
//    if (familiesWhereChild != null) {
//      for (FamilyChild familyChild : familiesWhereChild) {
//        Family family = familyChild.getFamily();
//
//        Individual individualFather = family.getHusband().getIndividual();
//        Individual individualMother = family.getWife().getIndividual();
//
//        String fatherXref = individualFather.getXref();
//        String motherXref = individualMother.getXref();
//
//        int fatherId = xrefToId(fatherXref);
//        int motherId = xrefToId(motherXref);
//
//        Person personFather = null;
//        Person personMother = null;
//
//        if (!dag.containsVertex(fatherId)) {
//          personFather = new Person(fatherId, individualFather.getFormattedName());
//          dag.addVertex(personFather);
//        }
//
//        if (!dag.containsVertex(motherId)) {
//          personMother = new Person(motherId, individualMother.getFormattedName());
//          dag.addVertex(personMother);
//        }
//
//        if (personFather != null) {
//          System.out.println("adding edge between " + personChild.getName() + " / " + personChild + " xref: " +
//                  id + " and " +
//                  personFather.getName() + " / " + personFather + " xref: " + fatherId);
//          dag.addEdge(personChild, personFather);
//        }
//        if (personMother != null) {
//          System.out.println("adding edge between " + personChild.getName() + " / " + personChild + " xref: " +
//                  id + " and " +
//                  personMother.getName() + " / " + personMother + " xref: " + motherId);
//          dag.addEdge(personChild, personMother);
//        }
//      }
//
//    }
//
//  }
//
//    System.out.println(dag.vertexSet().size());
//    System.out.println(dag.edgeSet().size());

  //    Map<String, Individual> individuals = gedcom.getIndividuals();
  //    for (String xref : individuals.keySet()) {
  //      Individual individual = individuals.get(xref);
  //      int id = xrefToId(xref);
  //      if (!dag.containsVertex(id)) {
  //        Person person = new Person(id, individual.getFormattedName());
  //        dag.addVertex(person);
  //      }
  //    }

  //    Map<String, Individual> individuals = gedcom.getIndividuals();
  //    for (int id = individuals.size() - 1; id >= 0; id--) {
  //      String xref = "@P" + id + "@";
  //
  //      Individual individual = individuals.get(xref);
  //      addVertexToGraph(id, individual);
  //
  //      List<FamilySpouse> familiesWhereSpouse = individual.getFamiliesWhereSpouse();
  //      for (FamilySpouse fws : familiesWhereSpouse) {
  //        List<IndividualReference> childReferences = fws.getFamily().getChildren();
  //
  //        for (IndividualReference childReference : childReferences) {
  //          Individual child = childReference.getIndividual();
  //
  //          String childXref = child.getXref();
  //
  //
  //        }
  //      }
  //    }


  //    Object[] vertexSet = dag.vertexSet().toArray();
  //    for (Object o : vertexSet) {
  //      System.out.println(((Person)o).getId());
  //    }

}
