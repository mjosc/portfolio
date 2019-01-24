package core;

import org.gedcom4j.exception.GedcomParserException;

import java.io.IOException;
import java.util.Collection;

public interface Dao {

  /**
   * Adds the specified User to the database and returns true if User.username is not already in
   * the database.
   *
   * @param user User whose record is to be added to the database.
   * @return true if User.username is not already in the database.
   */
  boolean addUser(User user);

  /**
   * Returns true if the provided username matches that of any user currently in the database.
   *
   * @param username Username to be queried.
   * @return true if the username already exists.
   */
  boolean isExistingUser(String username);

  /**
   * Returns true if the provided username and password match those stored in the database.
   *
   * @param username Username of the user to be authenticated.
   * @param password Password of the user to be authenticated.
   * @return true if the database contains a matching record to both the provided username and
   * password.
   */
  boolean isAuthenticatedUser(String username, String password);

  /**
   * Returns true if the user's GEDCOM file was successfully imported.
   *
   * @param username Username of the user whose GEDCOM file is to be uploaded.
   * @param path Path to the GEDCOM file in the server's file system.
   * @return true if the file is successfully imported and added to the database.
   */
  boolean addGedcom(String username, String path) throws IOException, GedcomParserException;


  // The following methods are not yet implemented for the current version.

  GedcomPerson getPerson(String user, long id);
  GedcomFamily getFamily(String user);

  Collection<GedcomPerson> getAllPersons(String user);
  Collection<GedcomFamily> getAllFamilies(String user);

  long getPersonCount(String user);
  long getFamilyCount(String user);

  Collection<GedcomPerson> getAllPersons(String user, long id, int generations);
  Collection<GedcomFamily> getAllFamilies(String user, long id, int generations);

  Collection<GedcomPerson> getDirectLine(String uers, long id, int generations);

  void describe(String user);

  void addPerson(String user);
  void addFamily(String user);

}
