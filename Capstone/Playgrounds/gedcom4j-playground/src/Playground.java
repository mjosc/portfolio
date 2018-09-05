import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.*;
import org.gedcom4j.parser.GedcomParser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A playground to get to know the gedcom4j library before interfacing with the graph structure.
 */
public class Playground {

  /**
   * Prints all Individual objects contained in the Map. The output is ordered by generation,
   * youngest to oldest.
   *
   * @param individuals a Map consisting of Xref:Individual key-value pairs
   */
  public static void PrintAllIndividuals(Map<String, Individual> individuals) {
    for (int i = 1; i <= individuals.size(); i++) {
      String key = "@P" + i + "@"; // Xref
      System.out.println(key + ", " + individuals.get(key));
    }
  }

  /**
   * Prints an Individual's parents, if they exist. "null", otherwise. Also includes the
   * family Xref from which the data is obtained.
   *
   * @param individual the Individual object whose parents will be printed
   */
  public static void PrintParentsOfIndividual(Individual individual) {
    List<FamilyChild> familiesWhereChild = individual.getFamiliesWhereChild();
    familiesWhereChild.forEach((fwc) -> {
      Family f = fwc.getFamily();
      System.out.println("Xref: " + f.getXref() + "\nFather: " + f.getHusband().getIndividual() + "\nMother: " +
              f.getWife().getIndividual());
    });
  }

  /**
   * Prints all Family objects contained in the Map. The output is ordered by generation,
   * youngest to oldest.
   *
   * @param families a Map consisting of Xref:Family key-value pairs
   */
  public static void PrintAllFamilies(Map<String, Family> families) {
    for (int i = 1; i <= families.size(); i++) {
      String key = "@F" + i + "@"; // Xref
      System.out.println(key + ", " + families.get(key));
    }
  }

  public static void main(String[] args) throws IOException, GedcomParserException {

    GedcomParser gp = new GedcomParser();
    gp.load("../Resources/Tests/TestTree4GenNameOnly.ged");

    Gedcom g = gp.getGedcom();

//    Playground.PrintAllFamilies(g.getFamilies());
//    Playground.PrintAllIndividuals(g.getIndividuals());
    Playground.PrintParentsOfIndividual(g.getIndividuals().get("@P1@"));
  }

}
