import java.time.LocalDate;

public class TestIndividual implements PersonConstruct {

  private int id;
  private String name;
  private int age;
  private LocalDate birthDate;
  private LocalDate deathDate;

  public TestIndividual(int id, String name, int age, LocalDate birthDate, LocalDate deathDate) {
    this.id = id;
    this.name = name;
    this.age = age;
    this.birthDate = birthDate;
    this.deathDate = deathDate;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getAge() {
    return age;
  }

  @Override
  public LocalDate getBirthDate() {
    return birthDate;
  }

  @Override
  public LocalDate getDeathDate() {
    return deathDate;
  }
}
