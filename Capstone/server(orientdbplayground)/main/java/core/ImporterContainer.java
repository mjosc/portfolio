package core;

/**
 * A container for all known subclasses of {@link AbstractAttributeImporter}. This class has no
 * independent functionality.
 */
final class ImporterContainer {

  /**
   * A simple subclass of {@link AbstractAttributeImporter} providing any further subclass access to
   * all {@link PersonFacts} methods, as implemented in that subclass.
   *
   * @param <T> Return type of all {@link PersonFacts} methods. Should be a functional interface.
   */
  abstract static class AbstractPersonFactImporter<T> extends AbstractAttributeImporter<T> implements PersonFacts<T> {

    protected AbstractPersonFactImporter() {
      super(PersonFacts.class);
    }
  }

  /**
   * A simple subclass of {@link AbstractAttributeImporter} providing any further subclass access to
   * all {@link PersonEvents} methods, as implemented in that subclass.
   *
   * @param <T> Return type of all {@link PersonEvents} methods. Should be a functional interface.
   */
  abstract static class AbstractPersonEventImporter<T> extends AbstractAttributeImporter<T> implements PersonEvents<T> {

    protected AbstractPersonEventImporter() {
      super(PersonEvents.class);
    }
  }

  /**
   * A simple subclass of {@link AbstractAttributeImporter} providing any further subclass access to
   * all {@link FamilyFacts} methods, as implemented in that subclass.
   *
   * @param <T> Return type of all {@link FamilyFacts} methods. Should be a functional interface.
   */
  abstract static class AbstractFamilyFactImporter<T> extends AbstractAttributeImporter<T> implements FamilyFacts<T> {

    protected AbstractFamilyFactImporter() {
      super(FamilyFacts.class);
    }
  }

  /**
   * A simple subclass of {@link AbstractAttributeImporter} providing any further subclass access to
   * all {@link FamilyEvents} methods, as implemented in that subclass.
   *
   * @param <T> Return type of all {@link FamilyEvents} methods. Should be a functional interface.
   */
  abstract static class AbstractFamilyEventImporter<T> extends AbstractAttributeImporter<T> implements FamilyEvents<T> {

    protected AbstractFamilyEventImporter() {
      super(FamilyEvents.class);
    }
  }

}