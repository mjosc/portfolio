package core;

import com.orientechnologies.orient.core.db.OrientDB;

import java.util.*;

/**
 * Utilities for OrientDB server and database maintenance.
 */
public class OrientDBUtils {

  /**
   * Removes all databases on the specified server except for those provided as arguments.
   *
   * @param server OrientDB server instance on which to perform the operation.
   * @param databaseNames Names of all databases to remain on the server (all others will be cleared).
   */
  public static void cleanServer(OrientDB server, String ...databaseNames) {
    System.out.println("Cleaning OrientDB server...");

    List<String> list = server.list();
    HashSet<String> set = new HashSet<>(Arrays.asList(databaseNames));
    for (String db : list) {
      if (!set.contains(db) && server.exists(db)) {
        server.drop(db);
      }
    }

    System.out.println("OrientDB server maintenance complete.");
    // See https://orientdb.com/docs/2.2.x/NET-Server-Databases.html for additional implementation ideas.
  }
}
