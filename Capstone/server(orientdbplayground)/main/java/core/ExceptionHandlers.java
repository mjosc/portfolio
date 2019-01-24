package core;

import com.google.gson.Gson;
import spark.ExceptionHandler;

/**
 * A simple helper class for handling Exceptions which have been elevated to the top level of the
 * application. Returns a 404 to the client with the exception message.
 *
 * This is a raw implementation and a catch-all under the current version.
 */
public class ExceptionHandlers {

  public static ExceptionHandler handleException = (exc, req, res) -> {
    exc.printStackTrace();
    int status = 404;
    res.status(status);
    res.body(new Gson().toJson(new ServerResponse(status, exc.getMessage())));
  };

}
