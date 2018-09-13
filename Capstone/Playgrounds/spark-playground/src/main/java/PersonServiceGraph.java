import java.util.Collection;

/* TODO:
 *
 * A database proxy. This class will be backed by my implementation of a DAG.
 */
public class PersonServiceGraph implements PersonService {

  @Override
  public void addPerson(Person person) {

  }

  @Override
  public Collection<Person> getPersons() {
    return null;
  }

  @Override
  public Person getPerson(int xref) {
    return null;
  }

  @Override
  public Person updatePerson(Person person) throws PersonException {
    return null;
  }

  @Override
  public void removePerson(int xref) {

  }

  @Override
  public boolean personExists(int xref) {
    return false;
  }
}
