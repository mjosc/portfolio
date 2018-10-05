import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.List;

/**
 * A JSON serializer for use with the JsonAdapter annotation in order to serialize {@link Playground.InnerClassB} to
 * only its id field.
 */
public class InnerClassBListSerializer implements JsonSerializer<List<Playground.InnerClassB>> {

  @Override
  public JsonElement serialize(List<Playground.InnerClassB> icbs, Type type, JsonSerializationContext
          jsonSerializationContext) {

    JsonArray arr = new JsonArray();
    for (Playground.InnerClassB icb : icbs) {
      arr.add(icb.getId());
    }

    return arr;
  }
}
