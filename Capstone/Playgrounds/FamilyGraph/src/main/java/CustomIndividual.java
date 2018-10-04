import org.gedcom4j.model.Individual;

public class CustomIndividual extends Individual implements IndividualConstruct {

  @Override
  public int getId() {
    String xref = getXref();
    return Integer.parseInt(xref.substring(1, xref.length() - 1));
  }

  @Override
  public String getName() {
    return getFormattedName();
  }

  private int xrefToId(String xref) {
    return Integer.parseInt(xref.substring(1, xref.length() - 1));
  }
}
