import com.google.gson.annotations.JsonAdapter;

import java.util.LinkedList;

public class Person {

  private int id;
  private String name;
  @JsonAdapter(FamilyListSerializer.class)
  public LinkedList<Family> parentFamilies = new LinkedList<>();

  /* TODO
   * Add support for families where this person was adopted.
   */
  public Family childFamily;

  public Person(IndividualConstruct individual) {
    this.id = individual.getId();
    this.name = individual.getName();
  }

  public void addParentFamily(Family family) {
    parentFamilies.add(family);
  }

  public void addChildFamily(Family family) {
    childFamily = family;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

} // Person
