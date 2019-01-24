package core;

import com.google.gson.*;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

public class SerializerUtil {

  /**
   * A simple helper class for customizing the deserialization of User objects on attempted login
   * or account creation.
   */
  public static class UserDeserializer implements JsonDeserializer<User> {

    /**
     * {@inheritDoc}
     *
     * Intended for use in deserializing post requests where the request body contains a Json
     * representation of the User object. Any errors in the body format will result in this method
     * returning null. See {@link User} for the expected fields.
     */
    @Override
    @Nullable
    public User deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {

      if (!jsonElement.isJsonObject()) {
        return null;
      }

      JsonObject obj = jsonElement.getAsJsonObject();
      boolean isLoginAttempt = obj.size() == 2 ? true : false;

      String[] props = isLoginAttempt ? new String[]{"username", "password"} :
              new String[]{"username", "email", "password"};
      String[] values = new String[props.length];

      for (int i = 0; i < props.length; ++i) {
        JsonElement element = obj.get(props[i]);
        if (element == null) {
          return null;
        }
        values[i] = element.getAsString();
      }

      return isLoginAttempt ? new User(values[0], values[1]) : new User(values[0], values[1], values[2]);
    }
  }


}
