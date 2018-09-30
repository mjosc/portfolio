import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.*;
import org.gedcom4j.parser.GedcomParser;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class GedcomImporter {

  public static final int FATHER = 0;
  public static final int MOTHER = 1;

  private Gedcom gedcom;
  private DAG dag = new DAG();


  public GedcomImporter(String filename) throws IOException, GedcomParserException {

    GedcomParser gp = new GedcomParser();
    gp.load(filename);

    gedcom = gp.getGedcom();
    generateDAG(gedcom);

    Person start = dag.getVertex(1);
    ArrayList<Person> directLineAncestors = dag.getDirectLineAncestors(start, 3);
    directLineAncestors.forEach(person -> System.out.println(person.getName()));

  }

  public DAG getDag() {
    return dag;
  }

  /**
   *
   *
   * @param gedcom
   */
  private void generateDAG(Gedcom gedcom) {

    Map<String, Individual> individualMap = gedcom.getIndividuals();
    IndividualReference[] individualReferences = new IndividualReference[2];

    for (String xref : individualMap.keySet()) {

      Individual individualChild = individualMap.get(xref);
      Person personChild = new Person(individualChild);
      dag.addVertex(personChild);

      List<FamilyChild> familiesWhereChild = individualChild.getFamiliesWhereChild();
      if (familiesWhereChild == null) {
        continue;
      }

      for (FamilyChild fc : familiesWhereChild) {
        Family family = fc.getFamily();

        individualReferences[FATHER] = family.getHusband();
        individualReferences[MOTHER] = family.getWife();

        for (int i = 0; i < individualReferences.length; i++) {
          IndividualReference individualReference = individualReferences[i];
          if (individualReference != null) {
            Individual individualParent = individualReference.getIndividual();
            Person personParent = new Person(individualParent);
            dag.addVertex(personParent);
            dag.addEdge(personChild, personParent);
          }
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
