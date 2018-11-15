import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gedcom4j.exception.GedcomParserException;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;

import static spark.Spark.*;

public class Playground {

  public static void main(String[] args) throws IOException, GedcomParserException, URISyntaxException {

//    Path path = ReusableUtil.getSystemResourcePath("TestTree4GenNameOnly.ged");
//    Path path = ReusableUtil.getSystemResourcePath("TestTree4GenNameOnlyMissingNode.ged");
//    GedcomImporter importer = new GedcomImporter(path.toString());

//    DatabaseService database = importer.getDag();

    File uploadDir = new File("upload");
    uploadDir.mkdir(); // create the upload directory if it doesn't exist

    staticFiles.externalLocation("upload");

    get("/", (req, res) ->
            "<form action='/upload' method='post' enctype='multipart/form-data'>" // note the enctype
                    + "    <input type='file' name='uploaded_file' accept='.ged'>" // make sure to call getPart using
                    // the same "name" in the post
                    + "    <button>Upload file</button>"
                    + "</form>"
    );

    post("/upload", (req, res) -> {

      Path path = Files.createFile(uploadDir.toPath());
      req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

      try (InputStream input = req.raw().getPart("uploaded_file").getInputStream()) {
        // TODO: convert this to a File in memory without reloading later.
//        Files.copy(input, path, StandardCopyOption.REPLACE_EXISTING);
        File file = new FileInputStream(input);
      }

      return "file uploaded successfully";

    });

    get("/:id/", (req, res) -> {
      res.type("application/json");

      int id = Integer.parseInt(req.params(":id"));
      Person person = database.getPerson(id);

      return new Gson().toJson(new ServerResponse(Status.SUCCESS, new Gson().toJsonTree(person)));
    });

    get("/:id/:k", (req, res) -> {
      res.type("application/json");

      int id = Integer.parseInt(req.params(":id"));
      int k = Integer.parseInt(req.params(":k"));
      Collection<Person> persons = database.getDirectLineAncestors(id, k);

      return new Gson().toJson(
              new ServerResponse(Status.SUCCESS, new Gson().toJsonTree(persons)));
    });

  }
}
