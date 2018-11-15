import org.gedcom4j.model.Individual;

import java.util.LinkedList;

public class CustomPerson {

  private int id;
  private String name;
  private LinkedList<Partnership> partners = new LinkedList<>(); // If need to cut down on memory, remove this init
  private Partnership parents; // TODO: Adoptions?

  public CustomPerson(Individual individual) {
    String xref = individual.getXref();

    this.id = xrefToId(xref);
    this.name = individual.getFormattedName();

  }

  public Partnership getParents() {
    return parents; // May be null if no parents have been added yet.
  }

  public boolean addPartnership(Partnership partnership) {
    return partners.add(partnership);
  }

  public void addParent(CustomPerson parent) {
    parents.addPartner(parent);
  }

  public int getId() {
    return id;
  }

  private int xrefToId(String xref) {
    String substring = xref.substring(2, xref.length() - 1);
    return Integer.parseInt(substring);
  }
}
