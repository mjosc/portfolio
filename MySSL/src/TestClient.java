import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Scanner;

/* Example usage of the mySSLClient class. Any application using the mySSL
 * protocol must extend either the mySSLClient or the mySSLServer class. This
 * requires a call to super where the socket is passed to the appropriate mySSL
 * layer. After creating the constructor, any class inheriting from one of these
 * two mySSL subclasses has access to the protected read and write methods of
 * the mySSL protocol.
 */
public class TestClient extends mySSLClient {

  /* Provides the file paths to the openSSL-generated private key and self-
   * signed certificate. Defined here as static members to improve readability
   * in the constructor.
   */
  private static String KEY_PATH = "../resources/client/private.der";
  private static String CERT_PATH = "../resources/client/certificate.cert";

  public static void main(String[] args) throws CertificateException,
          NoSuchAlgorithmException, NoSuchPaddingException, IOException,
          NotSupportedException, BadPaddingException,
          InvalidAuthenticationException, IllegalBlockSizeException,
          SignatureException, NoSuchProviderException, InvalidKeyException,
          InvalidKeySpecException, InvalidHeaderException {

    new TestClient(args[0], Integer.parseInt(args[1])).run();
  }

  /* Instantiates this mySSL instance as a client object. Passes the socket to
   * the mySSL layer and completes the client-side handshake protocol.
   */
  public TestClient(String host, int port) throws IOException,
          CertificateException, NotSupportedException,
          NoSuchAlgorithmException, NoSuchPaddingException,
          BadPaddingException, InvalidAuthenticationException,
          IllegalBlockSizeException, SignatureException,
          NoSuchProviderException, InvalidKeyException,
          InvalidKeySpecException {

    super(new Socket(host, port), KEY_PATH, CERT_PATH); // handshake
  }

  /* Example of a client application which implements the mySSL protocol. Here,
   * the client uses the mySSL write to send file requests to the server. The
   * client then decrypts the server's response and subsequent file transfers
   * via a call to mySSL's read method.
   *
   * In this case, the connection between the client and server is stateful and
   * will not terminate until either an exception is thrown or the client
   * chooses to close the connection.
   */
  public void run() throws IOException, NotSupportedException,
          BadPaddingException, InvalidKeyException,
          IllegalBlockSizeException, InvalidHeaderException,
          InvalidAuthenticationException {

    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNextLine()) {

      String filename = scanner.nextLine();
      write(filename.getBytes());
      System.out.println("CLIENT: Requested " + filename);

      byte[] status = read(); // server sends status before file transfer

      if (Arrays.equals(status, ("INVALID_REQUEST").getBytes())) {

        System.out.println("CLIENT: Invalid request. The file does not exist");
        continue; // no file transfer expected

      } else {
        System.out.println("CLIENT: Valid request. Waiting for file transfer");

        byte[] data = read(); // file transfer
        File file = new File("../resources/results/" + filename);

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(data);
        fileOutputStream.flush();

        System.out.println("CLIENT: Received requested file. Saved at " +
                file.getAbsolutePath());
      }
    }
  }

}


