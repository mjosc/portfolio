import java.util.Collection;

public interface PersonService {

  void addPerson(Person person);

  Collection<Person> getPersons();
  Person getPerson(int xref);

  Person updatePerson(Person person) throws PersonException;

  void removePerson(int xref);

  boolean personExists(int xref);
}
