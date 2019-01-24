package core;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import spark.ExceptionHandler;
import spark.Request;
import spark.Route;

public class AuthenticationHandlers {

  private Dao mDbs;

  /**
   * Initializes the underlying database service.
   */
  public AuthenticationHandlers(Dao databaseService) {
    mDbs = databaseService;
  }

  /**
   * Returns a 200 response if the user credentials (see {@link User}) provided in the body of the
   * request are successfully added to the database. If the provided username already exists, returns
   * a 409 Conflict.
   *
   * Syntax errors or missing fields within the request body will result in a 400 Syntax/Input error.
   *
   * {@inheritDoc}
   */
  public Route addUser = (req, res) -> {

    res.status(400); // Default to invalid input/syntax.
    String errorType = null;
    String description = null;

    try {
      User user = new Gson().fromJson(req.body(), User.class);
      if (user == null) {
        errorType = "Invalid input";
        description = "Provide the required fields { \"username\", \"email\", \"password\" }, ensure properly " +
                "formatted syntax, and try again.";
      } else {

        if (!mDbs.addUser(user)) {
          res.status(409);
          errorType = "Conflict";
          description = "This account already exists. Select a different username and try again.";
        } else {
          res.status(200);

          // TODO: Do session handling here.
          req.session().attribute("username", user.username);
          System.out.println(req.session().attribute("username").toString());
        }
      }
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
      System.err.println("Failed to parse request body: " + req.body());

      errorType = "Invalid syntax";
      description = "Ensure properly formatted JSON syntax and try again.";
    }

    return new Gson().toJsonTree(new ServerResponse(res.status(), errorType, description));
  };

  /**
   * Returns a 200 response if the username provided in the request url is not already taken. Otherwise
   * returns a 409 Conflict.
   *
   * {@inheritDoc}
   */
  public Route isExistingUser = (req, res) -> {
    String username = req.params(":username");
    String errorType = null;
    String description;

    if (mDbs.isExistingUser(username)) {
      res.status(409);
      errorType = "Conflict";
      description = "This account already exists. Select a different username and try again.";
    } else {
      description = "Account created.";
    }

    return new Gson().toJsonTree(new ServerResponse(res.status(), errorType, description));
  };

  /**
   * Returns a 200 response if the username and password provided in the body of the request match
   * those stored in the underlying database. Otherwise returns a 401 Unauthorized.
   *
   * Syntax errors or missing fields within the request body will result in a 400 Syntax/Input error.
   *
   * {@inheritDoc}
   */
  public Route login = (req, res) -> {
    res.status(400);
    String errorType = null;
    String description;
    try {
      User user = new Gson().fromJson(req.body(), User.class);
      if (user == null) {
        errorType = "Invalid input";
        description = "Provide the required fields { \"username\", \"password\" }, ensure properly " +
                "formatted syntax, and try again.";
      } else {

        if (mDbs.isAuthenticatedUser(user.username, user.password)) {
          res.status(200);
          description = "Authentication successful";
          // Do session handling here.
          req.session().attribute("username", user.username);
          System.out.println(req.session().attribute("username").toString());
          System.out.println(description);
        } else {

          res.status(401);
          errorType = "Unauthorized";
          description = "The provided username and password do not match.";

          System.out.println(description);
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
      errorType = "Invalid syntax";
      description = "Ensure properly formatted JSON syntax and try again.";
    }

    return new Gson().toJsonTree(new ServerResponse(res.status(), errorType, description));
  };


  public Route authenticate = (req, res) -> {

    res.status(401);
    String error = null;
    String description;

    if (isAuthenticatedUser(req)) {

      // TODO
      // res.status(200);
      // Currently ignore authentication request
      description = "Authentication successful";
    } else {
      error = "Unauthorized";
      description = "Authentication failed. Please login and try again.";
    }

    return new Gson().toJsonTree(new ServerResponse(res.status(), error, description));
  };

  public boolean isAuthenticatedUser(Request req) {
    return (req.session().attribute("username") != null);
  }

}
