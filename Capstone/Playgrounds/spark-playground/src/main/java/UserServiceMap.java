import java.util.Collection;
import java.util.HashMap;

public class UserServiceMap implements UserService {

  private HashMap<String, User> map;

  public UserServiceMap() {
    map = new HashMap<>();
  }

  @Override
  public void addUser(User user) {
    map.put(user.getId(), user);
  }

  @Override
  public Collection<User> getUsers() {
    return map.values();
  }

  @Override
  public User getUser(String id) {
    return map.get(id);
  }

  @Override
  public User editUser(User forEdit) throws UserException {

    try {
      if (forEdit.getId() == null) {
        throw new UserException("ID must be assigned");
      }

      User toEdit = map.get(forEdit.getId());
      if (toEdit == null) {
        throw new UserException("User not found");
      }
      toEdit.setId(forEdit.getId()); // Already checked for null

      if (forEdit.getFirstName() != null) {
        toEdit.setFirstName(forEdit.getFirstName());
      }

      if (forEdit.getLastName() != null) {
        toEdit.setLastName(forEdit.getLastName());
      }

      return toEdit;

    } catch (Exception e) {
      // TODO: Catch to throw in a single location?
      throw new UserException(e.getMessage());
    }
  }

  @Override
  public void deleteUser(String id) {
    map.remove(id);
  }

  @Override
  public boolean userExists(String id) {
    return map.containsKey(id);
  }

}
