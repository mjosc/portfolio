/* See mySSL for usage.
 */
public class InvalidAuthenticationException extends Exception {

  public InvalidAuthenticationException() {
    super("Invalid Authentication");
  }
  public InvalidAuthenticationException(String message) {
    super(message);
  }

}
