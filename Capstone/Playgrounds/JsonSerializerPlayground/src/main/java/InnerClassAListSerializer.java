import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.List;

/**
 * A JSON serializer for use with the JsonAdapter annotation in order to serialize {@link Playground.InnerClassA} to
 * only its id field.
 */
public class InnerClassAListSerializer implements JsonSerializer<List<Playground.InnerClassA>> {

  @Override
  public JsonElement serialize(List<Playground.InnerClassA> icas, Type type, JsonSerializationContext
          jsonSerializationContext) {

    JsonArray arr = new JsonArray();
    for (Playground.InnerClassA ica : icas) {
      arr.add(ica.getId());
    }

    return arr;
  }
}
