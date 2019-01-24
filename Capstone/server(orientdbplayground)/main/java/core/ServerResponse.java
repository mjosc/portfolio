package core;

import com.google.gson.JsonElement;

/**
 * Provides a serializable interface for Http responses. Intended for use with SparkJava's Routes.
 */
public class ServerResponse {

  private int status; // Http status
  private String type; // e.g. "Conflict"
  private String description;
  private JsonElement data; // Response body.

  public ServerResponse(int status, JsonElement data) {
    this.status = status;
    this.data = data;
  }

  public ServerResponse(int status, String type, String description) {
    this.status = status;
    this.type = type;
    this.description = description;
  }

  public ServerResponse(int status, String description) {
    this.status = status;
    this.description = description;
  }

}
