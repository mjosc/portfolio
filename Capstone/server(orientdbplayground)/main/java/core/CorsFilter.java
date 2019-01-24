package core;

import spark.Filter;
import spark.Spark;

import java.util.HashMap;

/**
 * A helper for enabling CORS.
 *
 * This implementation is derived from the following Github Gists:
 * <a href="https://gist.github.com/zikani03/7c82b34fbbc9a6187e9a">CorsFilter</a>
 * <a href="https://gist.github.com/saeidzebardast/e375b7d17be3e0f4dddf">OK Status</a>
 */
public final class CorsFilter {

  private static final HashMap<String, String> corsHeaders = new HashMap<>();

  static {
    corsHeaders.put("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
    corsHeaders.put("Access-Control-Allow-Origin", "*");
    corsHeaders.put("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
    corsHeaders.put("Access-Control-Allow-Credentials", "true");
  }

  public final static void apply() {
    Filter filter = (request, response) -> corsHeaders.forEach((key, value) -> {
      if (request.requestMethod().equals("OPTIONS")) {
        response.body("OK");
      }
      response.header(key, value);
    });
    Spark.before(filter);
  }
}
