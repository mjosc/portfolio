import org.gedcom4j.model.Individual;

import java.util.LinkedList;

public class Person {

  /*
   * TODO
   *
   * This class may eventually be required to support more than one parent partnership. Adoptions, for
   * example, may necessitate a LinkedList of parental partnerships.
   */

  private int id;
  private String name;
  private LinkedList<Partnership> parents = new LinkedList<>();
  private LinkedList<Partnership> partners; // Children are implicitly stored here.

  public Person(Individual individual) {
    this.id = xrefId(individual.getXref());
    this.name = individual.getFormattedName();
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  /**
   * A helper method to convert a GEDCOM xref to its corresponding id. For example, "@P14@" is returned
   * as int 14.
   *
   * @param xref the String form of the xref.
   * @return the integer form of the xref.
   */
  private int xrefId(String xref) {
    String substring = xref.substring(1, xref.length() - 1);
    return Integer.parseInt(substring);
  }

}
