import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.List;

public class FamilyListSerializer implements JsonSerializer<List<Family>> {

  @Override
  public JsonElement serialize(List<Family> families, Type type, JsonSerializationContext jsonSerializationContext) {
    JsonArray jsonPerson = new JsonArray();

    for (Family f : families) {
      jsonPerson.add("" + f.getId());
    }

    return jsonPerson;
  }
}
