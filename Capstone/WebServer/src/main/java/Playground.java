import org.gedcom4j.exception.GedcomParserException;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import sun.misc.ClassLoaderUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class Playground {

  public static void main(String[] args) throws IOException, GedcomParserException, URISyntaxException {

    Path path = ReusableUtil.getSystemResourcePath("TestTree4GenNameOnly.ged");
    GedcomImporter importer = new GedcomImporter(path.toString());





    
  }

}
