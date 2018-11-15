import com.google.gson.JsonElement;

import java.util.ArrayList;

public class Family {

  private static final int A = 0;
  private static final int B = 1;

  private int id;
  public Person[] parents = new Person[2];
  public ArrayList<Person> children = new ArrayList<>();

  public Family(FamilyConstruct family) {
    this.id = family.getId();
  }

  // Currently can be overwritten
  public void addParents(Person parentA, Person parentB) {
    parents[A] = parentA;
    parents[B] = parentB;
  }

  // Guaranteed to add
  public void addChildren(Person... children) {
    for (Person child : children) {
      this.children.add(child);
    }
  }

  public int getId() {
    return id;
  }

} // Family
