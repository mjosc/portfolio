import sun.misc.ClassLoaderUtil;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class ReusableUtil {

  public static Path getSystemResourcePath(String filename) throws URISyntaxException {
    URL url = ClassLoader.getSystemResource(filename);
    return Paths.get(url.toURI());
  }

}
