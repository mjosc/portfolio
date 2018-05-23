import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

/* The client-side protocol of mySSL. The main differences between mySSLClient
 * and mySSLServer are the order of the handshake methods and the file paths to
 * the private keys and certificates. All other mySSL methods called from
 * subclasses are invoked directly from mySSL.
 */
public class mySSLClient extends mySSL {

  public mySSLClient(Socket socket, String keyPath, String certPath) throws
          CertificateException, NotSupportedException,
          NoSuchAlgorithmException, NoSuchPaddingException, IOException,
          BadPaddingException, InvalidAuthenticationException,
          IllegalBlockSizeException, SignatureException,
          NoSuchProviderException, InvalidKeyException,
          InvalidKeySpecException {

    super(App.CLNT, socket);
    handshake(keyPath, certPath);
  }

  /* Implements the client-side mySSL handshake protocol. The same methods are
   * used for both the server- and client-side handshakes. However, the
   * required ordering necessitates a unique handshake method for each.
   */
  @Override
  public void handshake(String keyPath, String certPath) throws IOException,
          CertificateException, NoSuchAlgorithmException,
          NoSuchProviderException, InvalidKeyException, SignatureException,
          BadPaddingException, IllegalBlockSizeException,
          InvalidAuthenticationException, InvalidKeySpecException {

    privateKey(keyPath);

    certificateOut(certPath); // initiate handshake
    certificateIn();

    nonceIn();
    nonceOut();

    sharedSecretKey();

    System.out.println("CLIENT: Master Key Established");

    macIn();
    macOut();

    generateKeys();

    System.out.println("CLIENT: Secret Keys Established");
    System.out.println("CLIENT: Handshake Completed");
  }
}

