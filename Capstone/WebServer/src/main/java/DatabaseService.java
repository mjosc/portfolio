import java.util.Collection;

public interface DatabaseService {

  /**
   * Returns the {@link Person} with the specified id.
   *
   * @param id id of the Person to retrieve (corresponds to the GEDCOM xref assignment).
   * @return the Person with the specified id.
   */
  public Person getPerson(int id);

  /**
   * Returns a Collection of {@link Person} representing all the direct line ancestors of the Person
   * specified by the rootId argument and within the number of generations as given by the generations argument.
   *
   * @param rootId id of the Person whose ancestors are to be retrieved.
   * @param generations the number of generations within which to search for ancestors.
   * @return a Collection of Person representing all direct-line ancestors according to the specified arguments.
   */
  public Collection<Person> getDirectLineAncestors(int rootId, int generations);

}
