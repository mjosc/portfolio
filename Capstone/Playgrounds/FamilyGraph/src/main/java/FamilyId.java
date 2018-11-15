import java.util.Objects;

public class FamilyId {

  private final int a;
  private final int b;

  public FamilyId(int a, int b) {
    this.a = a;
    this.b = b;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) { return true; }
    if (!(o instanceof FamilyId)) { return false; }
    FamilyId p = (FamilyId) o;
    return (a == p.a && b == p.b) || (a == p.b && b == p.a);
  }

  @Override
  public int hashCode() {
    /* TODO
     * Ensure the following statement produces a hash which is compliant with the equals/hashCode contract.
     * Objects.hash appears to be a Java 7 addition and a valid solution but further research is necessary.
     */
    return Objects.hash(a + b);
  }

}
