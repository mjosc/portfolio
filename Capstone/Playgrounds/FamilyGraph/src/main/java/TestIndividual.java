public class TestIndividual implements IndividualConstruct {

  private int id;
  private String name;

  public TestIndividual(int id, String name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }
}
