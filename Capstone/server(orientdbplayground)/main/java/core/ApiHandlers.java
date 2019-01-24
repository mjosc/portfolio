package core;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.MultipartConfigElement;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ApiHandlers {

  private Dao dao;

  public ApiHandlers(Dao mDbs) {
    this.dao = mDbs;
  }

  /**
   * Uploads, imports, and subsequently removes from the filesystem the user's GEDCOM file.
   */
  public Route upload = (Request req, Response res) -> {

    // TODO
    // Make sure user is authenticated before uploading the file.

    Path path = Files.createTempFile(Paths.get("src/main/resources/uploads"), "", "");
    req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

    try (InputStream input = req.raw().getPart("filepond").getInputStream()) {
      Files.copy(input, path, StandardCopyOption.REPLACE_EXISTING);
    }

    String username = "testUser";
    boolean importIsSuccessful = dao.addGedcom(username, path.toString());

    Files.delete(path);

    String description = null;
    if (!importIsSuccessful) {
      res.status(400);
      description = "File import unsuccessful.";
    }
    return new Gson().toJson(new ServerResponse(res.status(), description));
  };
}
