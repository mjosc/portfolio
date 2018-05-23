import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

/* The server-side protocol of mySSL. The main differences between mySSLServer
 * and mySSLClient are the order of the handshake methods and the file paths to
 * the private keys and certificates. All other mySSL methods called from
 * subclasses are invoked directly from mySSL.
 */
public class mySSLServer extends mySSL {

  /* Instantiates this mySSL instance as a server object. Passes the socket to
   * the mySSL layer and completes the server-side handshake protocol.
   */
  public mySSLServer(Socket socket, String keyPath, String certPath) throws
          CertificateException, NotSupportedException,
          NoSuchAlgorithmException, NoSuchPaddingException, IOException,
          BadPaddingException, InvalidAuthenticationException,
          IllegalBlockSizeException, ShortBufferException,
          SignatureException, NoSuchProviderException, InvalidKeyException,
          InvalidKeySpecException {

    super(App.SRVR, socket);
    handshake(keyPath, certPath);
  }

  /* Implements the server-side mySSL handshake protocol. The same methods are
   * used for both the client- and server-side handshakes. However, the
   * required ordering necessitates a unique handshake method for each.
   */
  @Override
  public void handshake(String keyPath, String certPath) throws IOException,
          CertificateException, NoSuchAlgorithmException,
          NoSuchProviderException, InvalidKeyException, SignatureException,
          BadPaddingException, IllegalBlockSizeException,
          InvalidAuthenticationException, InvalidKeySpecException {

    privateKey(keyPath);

    certificateIn(); // wait for client to initiate handshake
    certificateOut(certPath);

    nonceOut();
    nonceIn();

    sharedSecretKey();

    System.out.println("SERVER: Master Key Established");

    macOut();
    macIn();

    generateKeys();

    System.out.println("SERVER: Secret Keys Established");
    System.out.println("SERVER: Handshake Completed");
  }
}
