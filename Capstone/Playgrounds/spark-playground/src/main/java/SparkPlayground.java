import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

/* This playground is built around a Spark Framework tutorial by Baeldung:
 * https://www.baeldung.com/spark-framework-rest-api
 */
public class SparkPlayground {

  public static void main(String[] args) {

    final PersonService personService = new PersonServiceGraph();

    /* TODO: How to appropriately document Spark routes (or more generally, a framework)?
     *
     * Returns the direct-line family tree of the Person referenced by the xref parameter.
     * The number of generations is given by k.
     */
    get("/family-trees/direct-line/:xref/:k", (Request req, Response res) -> {
      res.type("application/json");

      return "";
    });

    final UserService userService = new UserServiceMap();

    post("/users", (req, res) -> {
      res.type("application/json");

      User user = new Gson().fromJson(req.body(), User.class);
      userService.addUser(user);

      return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(userService.getUsers())));
    });

    get("/users", (req, res) -> {
      res.type("application/json");
      return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(userService.getUsers())));
    });

    get("/users/:id", (req, res) -> {
      res.type("application/json");
      return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(userService
              .getUser(req.params(":id")))));
    });

    put("/users/:id", (req, res) -> {
      res.type("application/json");

      User toEdit = new Gson().fromJson(req.body(), User.class);
      User editedUser = userService.editUser(toEdit);

      String result;
      if (editedUser != null) {
        result = new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, new Gson().toJsonTree(editedUser)));
      } else {
        result = new Gson().toJson(new StandardResponse(StatusResponse.ERROR, new Gson().toJson("User not found or " +
                "error in edit")));
      }

      return result;
    });

    delete("/users/:id", (req, res) -> {
      res.type("application/json");

      userService.deleteUser(req.params(":id"));
      return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, "user deleted"));
    });

    options("/users/:id", (req, res) -> {
      res.type("application/json");
      return new Gson().toJson(new StandardResponse(StatusResponse.SUCCESS, (userService.userExists(req.params
              (":id"))) ? "User exists" : "User does not exist"));
    });

  }
}