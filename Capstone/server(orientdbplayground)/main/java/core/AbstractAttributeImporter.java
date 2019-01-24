package core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Top level importer class. Uses reflection to register all methods of a particular interface into
 * a single {@link List} object. This is useful for deploying a potentially large number of
 * implemented
 * interface methods on a single object (e.g. iterating over all of an interfaces methods to import
 * facts and events from a gedcom object).
 * <p>
 * See also the classes defined in  {@link ImporterContainer}.
 *
 * @param <T> A functional interface to be registered to a single {@link List} instance.
 */
abstract class AbstractAttributeImporter<T> {

  private List<T> importers = new ArrayList<>();

  /**
   * Instantiates a {@link List} object containing all concrete instances of the specified
   * interface (one of {@link PersonFacts}, {@link PersonEvents}, {@link FamilyFacts}, or {@link FamilyEvents}.
   *
   * @param _interface Interface whose methods will be callable from {@link AbstractAttributeImporter#importers}.
   */
  protected <E extends PersonFacts & PersonEvents & FamilyFacts & FamilyEvents>
  AbstractAttributeImporter(Class<? super E> _interface) {
    register(_interface, importers);
  }

  /**
   * Registers all methods of the specified interface to the provided {@link List}. The interface must
   * be one of {@link PersonFacts}, {@link PersonEvents}, {@link FamilyFacts}, or {@link FamilyEvents}.
   *
   * This method implements reflection, and due to its intended use in importing potentially large
   * gedcom files, should be used in a manner that minimizes its invokation (i.e. reusing a single
   * instance of the subclass across the application).
   *
   * @param _interface Interface whose methods will be registered to {@link AbstractAttributeImporter#importers}.
   * @param list List object to which the methods will be registered.
   */
  private  <E extends PersonFacts & PersonEvents> void register(Class<? super E> _interface,
                                                                 List<T> list) {

    Method[] suppliers = _interface.getDeclaredMethods();
    for (Method m : suppliers) {
      try {
        list.add((T) m.invoke(this));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Returns the List of registered lambda expressions. Use this list to invoke all of an interface's
   * methods on a single object.
   */
  protected List<T> getImporters() {
    return importers;
  }
}
