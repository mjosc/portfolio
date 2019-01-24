package core;

import java.time.LocalDate;

/**
 * Representation of a life event. An event is defined by having a date and location, though they
 * may not be known.
 *
 * Ex. birth or death.
 */
public class Event extends AbstractAttribute {

  public LocalDate date;
  public String place;

  public Event(String type) {
    super(type);
  }

  public Event(String type, LocalDate date, String place) {
    super(type);
    this.date = date;
    this.place = place;
  }

}
