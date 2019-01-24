package core;


import static core.ExceptionHandlers.handleException;
import static spark.Spark.*;

public class Application {

  // Initialize these dependencies within the initializeDependencies method.
  private static Dao mDbs;
  private static AuthenticationHandlers mAuthHandlers;
  private static ApiHandlers mApiHandlers;

  public static void main(String[] args) {

    initializeDependencies(args);
    staticFileLocation("/public");

    before("/*", (req, res) -> res.type("application/json"));
    CorsFilter.apply(); // Internally calls Spark.before a second time.

    post(Path.LOGIN,          mAuthHandlers.login);
    post(Path.SIGNUP,         mAuthHandlers.addUser);
    get(Path.EXISTING_USER,   mAuthHandlers.isExistingUser);
    get(Path.AUTH,            mAuthHandlers.authenticate);
    post(Path.API,            mApiHandlers.upload);

    redirect.get("/*", Path.INDEX); // Required to enable refresh on client-side via react-router.
    exception(Exception.class, handleException);
  }

  private static void initializeDependencies(String[] args) {
    CommandLine cli = new CommandLine(args);

    mDbs = new OrientDBService(

            cli.get("serverUrl"),
            cli.get("serverUser"),
            cli.get("serverPassword"),
            cli.get("databaseName"),
            cli.get("databaseUser"),
            cli.get("databasePassword")
    );

    mAuthHandlers = new AuthenticationHandlers(mDbs);
    mApiHandlers = new ApiHandlers(mDbs);
  }

}
