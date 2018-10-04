import java.util.Objects;

public class PartnershipKey {

  private final int a;
  private final int b;

  public PartnershipKey(int a, int b) {
    this.a = a;
    this.b = b;
  }

  @Override
  public boolean equals(Object o) {

    if (o == this) { return true; }
    if (!(o instanceof PartnershipKey)) { return false; }
    PartnershipKey p = (PartnershipKey) o;
    return (a == p.a && b == p.b) || (a == p.b && b == p.a);
  }

  @Override
  public int hashCode() {
    return Objects.hash(a + b); // TODO: Okay to add these two?
  }

}
