import org.gedcom4j.factory.Sex;
import org.gedcom4j.model.Individual;
import org.gedcom4j.model.StringWithCustomFacts;

import java.util.LinkedList;

public class Person {

  private static final int UNASSIGNED = -1;

  private int id;
  private String name;
  private StringWithCustomFacts sex; // TODO: custom enum instead?

  private LinkedList<Integer> partners;
  private LinkedList<Integer> children;

  public Person() {
    this.id = UNASSIGNED;
  }

  public Person(Individual individual) {
    String xref = individual.getXref();

    this.id = xrefToId(xref);
    this.name = individual.getFormattedName();
    this.sex = individual.getSex();
  }

  public Person(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  private int xrefToId(String xref) {
    String substring = xref.substring(2, xref.length() - 1);
    return Integer.parseInt(substring);
  }
}
