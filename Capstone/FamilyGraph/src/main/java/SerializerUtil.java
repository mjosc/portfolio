import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.List;

public class SerializerUtil {

  public static class FamilySerializer implements JsonSerializer<FamilyGraph.Family> {

    @Override
    public JsonElement serialize(FamilyGraph.Family family, Type type,
                                 JsonSerializationContext jsonSerializationContext) {

      return new JsonPrimitive(family.getId());
    }
  }

  public static class PersonArraySerializer implements JsonSerializer<FamilyGraph.Person[]> {

    @Override
    public JsonElement serialize(FamilyGraph.Person[] people, Type type, JsonSerializationContext jsonSerializationContext) {

      JsonArray arr = new JsonArray();
      for (FamilyGraph.Person p : people) {
        arr.add(p.getId());
      }

      return arr;
    }
  }

  public static class FamilyListSerializer implements JsonSerializer<List<FamilyGraph.Family>> {

    @Override
    public JsonElement serialize(List<FamilyGraph.Family> families, Type type,
                                 JsonSerializationContext jsonSerializationContext) {

      JsonArray arr = new JsonArray();
      for (FamilyGraph.Family f : families) {
        arr.add(f.getId());
      }
      return arr;
    }
  }

  public static class PersonListSerializer implements JsonSerializer<List<FamilyGraph.Person>> {

    @Override
    public JsonElement serialize(List<FamilyGraph.Person> personList, Type type,
                                 JsonSerializationContext jsonSerializationContext) {

      JsonArray arr = new JsonArray();
      for (FamilyGraph.Person p : personList) {
        arr.add(p.getId());
      }
      return arr;
    }
  }
}
