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
import java.util.Collection;

public final class Routes {

  private static final String UPLOAD_DIR_PATH = "src/main/resources/uploads";
  private static final String CLIENT_FILE_INPUT_NAME = "filepond";
  private static final String TEMP_PREFIX = "";
  private static final String TEMP_SUFFIX = "";


  public static Route handleUserUpload = (Request req, Response res) -> {

    Path path = Files.createTempFile(Paths.get("src/main/resources/uploads"), "", "");
    req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

    try (InputStream input = req.raw().getPart("filepond").getInputStream()) {
      Files.copy(input, path, StandardCopyOption.REPLACE_EXISTING);
    }

    Files.delete(path);

    return "Successful upload. Here is the requested data.";

  };
}
