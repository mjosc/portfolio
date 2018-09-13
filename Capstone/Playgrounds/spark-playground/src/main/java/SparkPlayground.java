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

  }
}