/* A wrapper class used in mySSL to store byte arrays.
 */
public class Container<T> {

  private T val;

  public Container() {
    val = null;
  }

  public Container(T val) {
    this.val = val;
  }

  public T getVal() {
    return val;
  }

  public void setVal(T val) {
    this.val = val;
  }

}
