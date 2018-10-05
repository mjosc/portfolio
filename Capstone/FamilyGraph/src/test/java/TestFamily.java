public class TestFamily implements FamilyConstruct {

  private int id;

  public TestFamily(int id) {
    this.id = id;
  }

  @Override
  public int getId() {
    return id;
  }
}
