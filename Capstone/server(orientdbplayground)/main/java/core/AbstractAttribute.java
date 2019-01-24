package core;

/**
 * An abstraction of {@link Fact} and {@link Event}.
 */
abstract class AbstractAttribute {

  protected String type;

  protected AbstractAttribute(String type) {
    this.type = type;
  }

}
