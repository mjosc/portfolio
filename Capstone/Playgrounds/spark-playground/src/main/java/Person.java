/*
 * TODO: Potentially fix getters and setters to appropriately assign object references.
 */

import java.util.ArrayList;

public class Person {

  private int xref; // id
  private String name;

  private int[] parents; // Persons identified by id
  private ArrayList<Integer> partners; // Persons identified by id
  private ArrayList<Integer> children; // Persons identified by id

  private Event birth;
  private Event death;

  /* The default constructor is the only necessary constructor.
   *
   * A Person instance may eventually get quite large. It would be overwhelming to provide a constructor for every
   * possible combination of parameters. Instead, simply use the appropriate setter.
   */
  public Person() {

  }

  public int getXref() {
    return xref;
  }

  public void setXref(int xref) {
    this.xref = xref;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int[] getParents() {
    return parents;
  }

  public void setParents(int[] parents) {
    this.parents = parents;
  }

  public ArrayList<Integer> getPartners() {
    return partners;
  }

  public void setPartners(ArrayList<Integer> partners) {
    this.partners = partners;
  }

  public ArrayList<Integer> getChildren() {
    return children;
  }

  public void setChildren(ArrayList<Integer> children) {
    this.children = children;
  }

  public Event getBirth() {
    return birth;
  }

  public void setBirth(Event birth) {
    this.birth = birth;
  }

  public Event getDeath() {
    return death;
  }

  public void setDeath(Event death) {
    this.death = death;
  }
}
