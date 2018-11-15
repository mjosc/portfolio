import com.google.gson.JsonElement;

public class ServerResponse {

  private Status status;
  private String message;
  private JsonElement data;

  public ServerResponse() {

  }

  public ServerResponse(Status status, String message) {
    this.status = status;
    this.message = message;
  }

  public ServerResponse(Status status, JsonElement data) {
    this.status = status;
    this.data = data;
  }


}
