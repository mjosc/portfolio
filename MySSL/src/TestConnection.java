import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

/* Example usage of the mySSLServer class. Implements Runnable as a means to
 * execute from a thread pool. Each instance represents a single connection
 * between server and client.
 *
 * Any application using the mySSL protocol must extend either the mySSLClient
 * or the mySSLServer class. This requires a call to super where the socket
 * is passed ot the appropriate mySSL layer. After creating the constructor, any
 * class inheriting from one of these two mySSL subclasses has access to the
 * protected read and write methods of the mySSL protocol.
 */
public class TestConnection extends mySSLServer implements Runnable {

  /* Provides the file paths to the openSSL-generated private key and self-
   * signed certificate. Defined here as static members to improve readability
   * in the constructor.
   */
  private static String KEY_PATH = "../resources/server/private.der";
  private static String CERT_PATH = "../resources/server/certificate.cert";

  /* Passes the socket to the mySSLServer layer and completes the mySSL
   * handshake protocol. Simply creating an instance of any object which extends
   * either the mySSLServer or mySSLClient classes and provides a valid socket
   * to the super constructor will result in the handshake protocol.
   */
  public TestConnection(Socket socket) throws CertificateException,
          NotSupportedException, NoSuchAlgorithmException,
          NoSuchPaddingException, IOException, BadPaddingException,
          InvalidAuthenticationException, IllegalBlockSizeException,
          ShortBufferException, SignatureException, NoSuchProviderException,
          InvalidKeyException, InvalidKeySpecException {

    super(socket, KEY_PATH, CERT_PATH); // handshake
  }

  /* Example implementation of the Runnable interface. In this example, the
   * server receives a request for a specific file. If the file exists, it is
   * loaded into memory and sent across the secure socket. This connection is
   * stateful and will not terminate until either an exception is thrown or the
   * client closes the connection.
   */
  @Override
  public void run() {

    try {
      while (!socket.isClosed()) { // stateful connection

        String filename = new String(read());
        File file = new File("../resources/tests/" + filename);

        System.out.println("SERVER: Received request for file at " + file
                .getAbsolutePath());

        if (!file.exists()) {

          write(("INVALID_REQUEST").getBytes());
          System.out.println("SERVER: " + filename + " does not exist. " +
                  "Awaiting additional requests");
        } else {
          write(("VALID_REQUEST").getBytes());

          FileInputStream fileInputStream = new FileInputStream(file);
          byte[] data = new byte[(int) file.length()];
          fileInputStream.read(data);

          write(data);

          System.out.println("SERVER: " + filename + " written to client");
        }
      }

    } catch (Exception e) {
      System.out.println("SERVER: " + e.getMessage());
    }

    // socket is implicitly closed when this method returns
    System.out.println("SERVER: Connection closed");
  }

}
