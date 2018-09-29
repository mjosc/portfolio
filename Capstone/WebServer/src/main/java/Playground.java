import com.google.gson.Gson;
import org.gedcom4j.exception.GedcomParserException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Collection;

import static spark.Spark.*;

public class Playground {

  public static void main(String[] args) throws IOException, GedcomParserException, URISyntaxException {

    Path path = ReusableUtil.getSystemResourcePath("TestTree4GenNameOnly.ged");
    GedcomImporter importer = new GedcomImporter(path.toString());

    DatabaseService database = importer.getDag();

    get("/:id/", (req, res) -> {
      res.type("application/json");

      int id = Integer.parseInt(req.params(":id"));
      Person person = database.getPerson(id);

      return new Gson().toJson(new ServerResponse(Status.SUCCESS, new Gson().toJsonTree(database.getPerson(id))));
    });

    get("/:id/:k", (req, res) -> {
      res.type("application/json");

      int id = Integer.parseInt(req.params(":id"));
      int k = Integer.parseInt(req.params(":k"));
      Collection<Person> persons = database.getDirectLineAncestors(id, k);

      return new Gson().toJson(new ServerResponse(Status.SUCCESS, new Gson().toJsonTree(database.getDirectLineAncestors(id, k))));
    });
    
  }
}
