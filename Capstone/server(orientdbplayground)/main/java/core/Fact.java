package core;

/**
 * Representation of a fact. As opposed to an event, a fact has no date nor location.
 *
 * Ex. age or number of children.
 */
public class Fact extends AbstractAttribute {

  public String description;

  public Fact(String type) {
    super(type);
  }

  public Fact(String type, String description) {
    super(type);
    this.description = description;
  }

}
