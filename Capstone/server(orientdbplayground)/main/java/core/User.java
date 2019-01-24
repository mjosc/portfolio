package core;

import com.google.gson.annotations.JsonAdapter;

/**
 * A simple helper class for serializing and deserializing user data to and from a database.
 */
@JsonAdapter(SerializerUtil.UserDeserializer.class)
public class User {

  public String username;
  public String email;

  transient String password;

  /**
   * Creates a User with the provided username and password. This constructor is intended for use
   * with login attempts where the full user data is not required.
   */
  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  /**
   * Creates a complete User object. This constructor is intended for use with account creation or
   * other areas where the full user data may be required.
   */
  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }
}
