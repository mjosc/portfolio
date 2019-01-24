package core;

import org.gedcom4j.model.*;
import org.gedcom4j.parser.GedcomParser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import static core.ImporterContainer.*;

/**
 * An importer helper class integrated with gedcom4j.
 */
public final class GedcomImporter {

  /**
   * Loads the gedcom file from the specified path and if no exceptions are thrown, returns a
   * gedcom object. Otherwise returns null.
   *
   * @param path Path to the GEDCOM file in the local filesystem.
   * @return The Java representation of the GEDCOM file or null, if unable to load the file.
   */
  @Nullable
  public static Gedcom load(String path) {
    Gedcom gedcom = null;
    try {
      GedcomParser gp = new GedcomParser();
      gp.load(path);
      return gp.getGedcom();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return gedcom;
  }

  /**
   * Imports an individual to a graph-compatible object, including all attributes described by
   * the corresponding attribute importers.
   *
   * @param ind The individual to import.
   * @return A graph-compatible representation of the Individual.
   */
  public static SerializablePerson importIndividual(Individual ind) {
    long id = xrefToId(ind.getXref());
    List<Event> events = importPersonEvents(ind);
    List<Fact> facts = importPersonFacts(ind);
    return new SerializablePerson(id, facts, events);
  }

  /**
   * Imports a family to a graph-compatible object, including all attributes described by the
   * corresponding attribute importers.
   *
   * @param fam Family to import.
   * @return A graph-compatible representation of the Family.
   */
  public static SerializableFamily importFamily(Family fam) {
    long id = xrefToId(fam.getXref());
    List<Event> events = importFamilyEvents(fam);
    List<Fact> facts = importFamilyFacts(fam);
    return new SerializableFamily(id, facts, events);
  }

  /**
   * A helper function for converting XREFs to ids.
   *
   * @param xref The xref of the gedcom entity.
   * @return xref converted to a number.
   */
  public static Long xrefToId(String xref) {
    return Long.parseLong(xref.substring(2, xref.length() - 1));
  }

  /**
   * A helper function to import all person facts of an individual.
   *
   * @param ind
   * @return
   */
  private static List<Fact> importPersonFacts(Individual ind) {
    List<Fact> result = new ArrayList<>();
    HashMap<String, CustomFact> options = getFactOptions(ind.getCustomFacts());
    for (BiFunction importer : personFactImporters) {
      result.add((Fact) importer.apply(options, ind));
    }
    return result;
  }

  /**
   * A helper function to import all person events of an individual.
   *
   * @param ind
   * @return
   */
  private static List<Event> importPersonEvents(Individual ind) {
    List<Event> result = new ArrayList<>();
    List<IndividualEvent> allEvents = ind.getEvents();
    HashMap<String, IndividualEvent> options = null;
    if (allEvents != null) {
      options = new HashMap<>(allEvents.size());
      for (IndividualEvent event : allEvents) {
        if (event.getType() != null) {
          options.put(event.getType().toString().toLowerCase(), event);
        }
      }
    }
    for (BiFunction importer : personEventImporters) {
      result.add((Event) importer.apply(options, ind));
    }
    return result;
  }

  /**
   * A helper function to import all family facts.
   *
   * @param fam
   * @return
   */
  private static List<Fact> importFamilyFacts(Family fam) {
    List<Fact> result = new ArrayList<>();
    HashMap<String, CustomFact> options = getFactOptions(fam.getCustomFacts());
    for (BiFunction importer : familyFactImporters) {
      result.add((Fact) importer.apply(options, fam));
    }
    return result;
  }

  /**
   * A helper function to import all family events.
   *
   * @param fam
   * @return
   */
  private static List<Event> importFamilyEvents(Family fam) {
    List<Event> result = new ArrayList<>();
    List<FamilyEvent> allEvents = fam.getEvents();
    HashMap<String, FamilyEvent> options = null;
    if (allEvents != null) {
      options = new HashMap<>(allEvents.size());
      for (FamilyEvent event : allEvents) {
        if (event.getType() != null) {
          options.put(event.getType().toString().toLowerCase(), event);
        }
      }
    }
    for (BiFunction importer : familyEventImporters) {
      result.add((Event) importer.apply(options, fam));
    }
    return result;
  }

  /**
   * A helper function to map all CustomFacts by their type.
   *
   * @param facts
   * @return
   */
  private static HashMap<String, CustomFact> getFactOptions(List<CustomFact> facts) {
    HashMap<String, CustomFact> options = null;
    List<CustomFact> allFacts = facts;
    if (allFacts != null) {
      options = new HashMap<>(allFacts.size());
      for (CustomFact fact : allFacts) {
        StringWithCustomFacts type = fact.getType();
        if (type != null) {
          options.put(type.toString().toLowerCase(), fact);
        }
      }
    }
    return options;
  }

  // -----------------------------------------------------------------------------------------------
  // AbstractAttributeImporter implementations -- Import facts and events from gedcom objects.

  private static List<BiFunction<HashMap<String, CustomFact>, Individual, Fact>> personFactImporters = new
          AbstractPersonFactImporter<BiFunction<HashMap<String, CustomFact>, Individual, Fact>>() {


    @Override
    public BiFunction<HashMap<String, CustomFact>, Individual, Fact> name() {
      return (map, ind) -> new Fact("name", ind.getFormattedName());
    }

    @Override
    public BiFunction<HashMap<String, CustomFact>, Individual, Fact> sex() {
      return (map, ind) -> {
        String sex = null;
        StringWithCustomFacts embeddedSex = ind.getSex();
        if (embeddedSex != null) {
          sex = embeddedSex.getValue().equals("M") ? "male" : "female";
        }
        return new Fact("sex", sex);
      };
    }

  }.getImporters();

  private static List<BiFunction<HashMap<String, IndividualEvent>, Individual, Event>> personEventImporters = new
          AbstractPersonEventImporter<BiFunction<HashMap<String, IndividualEvent>, Individual, Event>>() {

    @Override
    public BiFunction<HashMap<String, IndividualEvent>, Individual, Event> birth() {
      return (map, ind) -> defaultPersonEventImporter("birth", map);
    }

    @Override
    public BiFunction<HashMap<String, IndividualEvent>, Individual, Event> christening() {
      return (map, ind) -> defaultPersonEventImporter("christening", map);
    }

    @Override
    public BiFunction<HashMap<String, IndividualEvent>, Individual, Event> death() {
      return (map, ind) -> defaultPersonEventImporter("death", map);
    }
  }.getImporters();

  private static List<BiFunction<HashMap<String, CustomFact>, Family, Fact>> familyFactImporters = new
          AbstractFamilyFactImporter<BiFunction<HashMap<String, CustomFact>, Family, Fact>>() {

    // Not implemented.

  }.getImporters();

  private static List<BiFunction<HashMap<String, CustomFact>, Family, Event>> familyEventImporters = new
          AbstractFamilyEventImporter<BiFunction<HashMap<String, CustomFact>, Family, Event>>() {

    // Not implemented.

  }.getImporters();


  private static DateTimeFormatter defaultDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

  /**
   * Returns an event with the specified name. If the event does not exist for the specified
   * individual, this method guarantees the shape of all objects at the very least.
   *
   * @param key Name of the event.
   * @param options
   * @return
   */
  private static Event defaultPersonEventImporter(String key, @Nullable HashMap<String, IndividualEvent> options) {
    Event event;
    if (options != null && options.containsKey(key)) {
      IndividualEvent e = options.get(key);
      LocalDate date = null;
      String place = null;

      StringWithCustomFacts embeddedDate = e.getDate();
      if (embeddedDate != null) {
        date = parseDate(embeddedDate);
      }

      Place embeddedPlace = e.getPlace();
      if (embeddedPlace != null) {
        place = embeddedPlace.getPlaceName();
      }

      event = new Event(key, date, place);
    } else {
      event = new Event(key);
    }

    return event;
  }

  /**
   * Uses the default date formatter.
   * @param customFacts
   * @return
   */
  @Nullable
  private static LocalDate parseDate(@Nonnull StringWithCustomFacts customFacts) {
    LocalDate date = null;
    try {
      date = LocalDate.parse(customFacts.toString(), defaultDateFormatter);
    } catch (DateTimeParseException e) {
      System.err.println(customFacts);
    }
    return date;
  }

}
