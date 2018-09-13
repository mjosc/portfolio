/*
 * TODO: Potentially fix getters and setters to appropriately assign object references.
 */

import java.time.LocalDate;

public class Event {

  private int id;
  private LocalDate date;
  private String place;

  /* The default constructor is the only necessary constructor.
   *
   * Similar to a Person instance, an Event instance may eventually get quite large. It will likely also have many null
   * attributes. It would be overwhelming to provide a constructor for every possible combination of parameters.
   * Instead, simply use the appropriate setter.
   */
  public Event() {

  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public String getPlace() {
    return place;
  }

  public void setPlace(String place) {
    this.place = place;
  }
}
