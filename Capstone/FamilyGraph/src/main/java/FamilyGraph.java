import com.google.gson.annotations.JsonAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class FamilyGraph {

  private HashMap<Integer, Person> personMap = new HashMap<>();
  private HashMap<Integer, Family> familyMap = new HashMap<>();

  public static class Person {

    private int id;
    private String name;
    @JsonAdapter(SerializerUtil.FamilySerializer.class)
    private Family childFamily;
    @JsonAdapter(SerializerUtil.FamilyListSerializer.class)
    private ArrayList<Family> parentFamilies;

    public int getId() {
      return id;
    }
  }

  public static class Family {

    private int id;
    @JsonAdapter(SerializerUtil.PersonArraySerializer.class)
    private Person[] parents;
    @JsonAdapter(SerializerUtil.PersonListSerializer.class)
    private ArrayList<Person> children;

    public int getId() {
      return id;
    }
  }
}
