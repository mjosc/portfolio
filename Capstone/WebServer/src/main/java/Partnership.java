import java.util.ArrayList;

public class Partnership {

  private static final int MAX_LENGTH = 2;

  private int id; // assigned by the graph
  private CustomPerson[] mPartners = new CustomPerson[MAX_LENGTH]; // pointers to Person objects in graph
  private ArrayList<CustomPerson> mChildren; // don't take up too much space with child-less relationships

  private int nextIndex = 0; // Keep track of where to add the next partner.


  public Partnership(int id) {
    this.id = id;
  }

  public Partnership(CustomPerson a, CustomPerson b) {
    mPartners[0] = a;
    mPartners[1] = b;
  }

  public boolean addPartner(CustomPerson partner) {
    if (mPartners.length == MAX_LENGTH) {
      return false;
    }
    mPartners[nextIndex++] = partner;
    return true;
  }

  public int getId() {
    return id;
  }

}
