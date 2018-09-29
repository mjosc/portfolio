import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.*;
import org.gedcom4j.parser.GedcomParser;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class GedcomImporter {

  public static final int CHILD = 0;
  public static final int FATHER = 1;
  public static final int MOTHER = 2;

  private Gedcom gedcom;
  private DAG dag = new DAG();


  public GedcomImporter(String filename) throws IOException, GedcomParserException {

    GedcomParser gp = new GedcomParser();
    gp.load(filename);

    gedcom = gp.getGedcom();
    generateDAG(gedcom);

    Person start = dag.getVertex(1);
    ArrayList<Person> directLineAncestors = dag.getDirectLineAncestors(start, 2);
    directLineAncestors.forEach(person -> System.out.println(person.getName()));


//    dag.breadthFirstTraversal(start);
//    System.out.println();
//    dag.depthFirstTraversal(start);

  }

  /**
   * Populates the underlying directed acyclic graph structure with
   *
   * @param gedcom
   */
  private void generateDAG(Gedcom gedcom) {

    /*
     * TODO
     *
     * How cache friendly is this?
     */

    Map<String, Individual> individualMap = gedcom.getIndividuals();
    Individual[] individuals = new Individual[3];
    Person[] persons = new Person[3];

    for (String xref : individualMap.keySet()) {

      individuals[CHILD] = individualMap.get(xref);
      persons[CHILD] = new Person(individuals[CHILD]);

      dag.addVertex(persons[CHILD]);

      List<FamilyChild> familiesWhereChild = individuals[CHILD].getFamiliesWhereChild();
      if (familiesWhereChild == null) {
        continue;
      }

      for (FamilyChild fc : familiesWhereChild) {
        Family family = fc.getFamily();

        individuals[FATHER] = family.getHusband().getIndividual();
        individuals[MOTHER] = family.getWife().getIndividual();
        persons[FATHER] = new Person(individuals[FATHER]);
        persons[MOTHER] = new Person(individuals[MOTHER]);

        for (int i = 1; i < individuals.length; i++) {
          dag.addVertex(persons[i]);
          dag.addEdge(persons[CHILD], persons[i]);
        }
      }
    }
  }

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

}
