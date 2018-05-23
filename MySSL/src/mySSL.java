import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/* A secure socket layer platform providing methods for both the handshake and
 * data transfer phases. Designed as an abstract class so as to permit both
 * client and server implementations. Thus, any application which implements the
 * mySSL protocol should inherit from the mySSLClient or mySSLServer subclasses
 * in order to avoid implementing their own handshake.
 */
public abstract class mySSL {

  /*******************************************
   BOTH HANDSHAKE AND DATA TRANSFER PHASES
   *******************************************/

  protected Socket socket;

  private String thisApp;
  private String thatApp;

  private DataInputStream input, socketIn;
  private DataOutputStream output, socketOut;

  private SecretKey clientAuth;
  private SecretKey serverAuth;
  private SecretKey clientCrypt;
  private SecretKey serverCrypt;

  private Mac mac = Mac.getInstance("HMACSHA1");
  private Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");

  private Container<byte[]> byteContainer = new Container<>();

  /********************
   HANDSHAKE PHASE
   ********************/

  private static final int NONCE_LENGTH = 16;

  private KeyFactory kf = KeyFactory.getInstance("RSA");
  private CertificateFactory cf = CertificateFactory.getInstance("X.509");
  private SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
  private KeyGenerator kg = KeyGenerator.getInstance("AES");

  // used to authenticate handshake history via MAC
  private int numBytes = 0;
  private int dataIndex = 0;
  private ArrayList<byte[]> data = new ArrayList<>(Collections.nCopies(5,
          null));

  private PrivateKey privateKey; // this application
  private PublicKey publicKey; // that application
  private byte[] sharedSecretKey;
  private SecretKeySpec keySpec;

  private Cipher RSA = Cipher.getInstance("RSA");

  private byte[] nonceIn, nonceOut;

  /***********************
   DATA TRANSFER PHASE
   ***********************/

  private static final int TYPE_LENGTH = 4;
  private static final int MAC_LENGTH = 20;

  private int seqNum = 0;
  private ByteBuffer buf;

  /* Distinguishes between client and server instances. The mySSLClient and
   * mySSLServer should call the constructor accordingly. Only the socket is
   * passed from the user.
   */
  protected mySSL(App app, Socket socket) throws IOException,
          NoSuchAlgorithmException, CertificateException,
          NoSuchPaddingException, NotSupportedException {

    if (!app.equals(App.CLNT) && (!app.equals(App.SRVR))) {
      throw new NotSupportedException("NotSupportedException: CLNT | SRVR");
    }

    this.socket = socket;
    thisApp = app.equals(App.CLNT) ? "CLNT" : "SRVR";
    thatApp = app.equals(App.CLNT) ? "SRVR" : "CLNT";

    socketIn = new DataInputStream(new BufferedInputStream(socket
            .getInputStream()));
    socketOut = new DataOutputStream(new BufferedOutputStream(socket
            .getOutputStream()));

    input = socketIn;
    output = socketOut;
  }

  /* Fills a Container object with the byte array read from the socket's input
   * stream. Designed for use in conjunction with the writeBytes method.
   */
  private void readBytes(Container<byte[]> byteContainer) throws IOException {
    int len = input.readInt();
    byte[] bytes = new byte[len];
    input.readFully(bytes);

    byteContainer.setVal(bytes);
  }

  /* Writes the specified bytes to the socket's output stream. Designed for use
   * in conjunction with the readBytes method.
   */
  private void writeBytes(byte[] bytes) throws IOException {
    output.writeInt(bytes.length);
    output.write(bytes);
    output.flush();
  }

  /* Helper method for returning a 20 byte MAC of any given byte array.
   */
  protected byte[] mac(byte[] bytes) throws InvalidKeyException {
    mac.init(keySpec);
    return mac.doFinal(bytes);
  }

  /*****************************************************************************
   HANDSHAKE PHASE

   The following methods are for use in the handshake protocol. Protected
   methods are invoked in the mySSLClient and mySSLServer classes.

   ****************************************************************************/

  /* Loads this application's private key from the file system and instantiates
   * the PrivateKey member variable.
   */
  protected void privateKey(String pathname) throws IOException,
          InvalidKeySpecException {

    File file = new File(pathname);

    // reroute this object's input stream to the file system
    input = new DataInputStream(new FileInputStream(file));
    byte[] bytes = new byte[(int) file.length()];
    input.readFully(bytes);

    input = socketIn; // restore input stream to the socket
    privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(bytes));
  }

  /* Loads this application's certificate from the file system and writes it to
   * the socket output stream. If this is the client application, this method
   * also acts as the "hello" to initiate the handshake.
   */
  protected void certificateOut(String pathname) throws IOException,
          CertificateException {

    Certificate certificate = cf.generateCertificate(new BufferedInputStream
            (new FileInputStream(pathname)));

    byte[] cert = certificate.getEncoded();
    numBytes += cert.length;
    data.set(dataIndex++, cert);
    writeBytes(cert);
  }

  /* Reads the certificate data from the corresponding application and verifies
   * the certificate was signed with the correct private key.
   */
  protected void certificateIn() throws IOException, CertificateException,
          NoSuchProviderException, NoSuchAlgorithmException,
          InvalidKeyException, SignatureException {

    readBytes(byteContainer);
    byte[] cert = byteContainer.getVal();
    numBytes += cert.length;
    data.set(dataIndex++, cert);

    Certificate certificate = cf.generateCertificate(new ByteArrayInputStream
            (cert));
    publicKey = certificate.getPublicKey();
    certificate.verify(publicKey);
  }

  /* Reads the nonce from the corresponding application and decrypts it with
   * this application's private key. A BadPaddingException is thrown if the
   * key used to encrypt the data was not this application's public key.
   */
  protected void nonceIn() throws IOException, InvalidKeyException,
          BadPaddingException, IllegalBlockSizeException {

    readBytes(byteContainer);
    byte[] nonce = byteContainer.getVal();

    RSA.init(Cipher.DECRYPT_MODE, privateKey);
    nonceIn = RSA.doFinal(nonce);

    numBytes += nonceIn.length;
    data.set(dataIndex++, nonceIn);
  }

  /* Writes an encrypted nonce to the socket output stream. Makes use of the
   * corresponding application's public key for encryption.
   *
   * The nonce is a randomly generated byte array of NONCE_LENGTH.
   */
  protected void nonceOut() throws InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, IOException {

    nonceOut = new byte[NONCE_LENGTH];
    sr.nextBytes(nonceOut);
    numBytes += nonceOut.length;
    data.set(dataIndex++, nonceOut);

    RSA.init(Cipher.ENCRYPT_MODE, publicKey);
    writeBytes(RSA.doFinal(nonceOut));
  }

  /* Generates the master secret key shared between the client and the server.
   */
  protected void sharedSecretKey() {

    sharedSecretKey = new byte[NONCE_LENGTH];
    for (int i = 0; i < NONCE_LENGTH; i++) {
      sharedSecretKey[i] = (byte) (nonceIn[i] ^ nonceOut[i]);
    }

    // TODO: Find a more natural location for initializing the SecretKeySpec. It
    // cannot be done until after the shared secret has been created but it
    // probably should not be done explicitly inside the mySSLServer or
    // mySSLClient classes.

    keySpec = new SecretKeySpec(sharedSecretKey, "HMACSHA1");
  }

  /* Helper method for combining the handshake history into a single byte array
   * in order to create a MAC.
   */
  private byte[] mergeData() {

    byte[] result = new byte[numBytes];
    int i = 0, j;
    for (byte[] arr : data) { // data = message history
      for (j = 0; j < arr.length; j++) {
        result[i] = arr[j];
        ++i;
      }
    }

    return result;
  }

  /* Reads the corresponding application's MAC of the handshake history and
   * compares it to this application's handshake history. Throws an
   * InvalidAuthenticationException if the two copies do not match.
   */
  protected void macIn() throws IOException, InvalidKeyException,
          InvalidAuthenticationException {

    readBytes(byteContainer);
    byte[] macIn = byteContainer.getVal();

    byte[] sender = thatApp.getBytes();
    numBytes += sender.length;
    data.set(data.size() - 1, sender); // last element in history = sender

    byte[] localHistory = mac(mergeData());

    if (!Arrays.equals(macIn, localHistory)) {
      throw new InvalidAuthenticationException
              ("InvalidAuthenticationException: Incoming MAC must match " +
                      "local copy");
    }
  }

  /* Writes a MAC of this application's handshake history to the socket output
   * stream.
   */
  protected void macOut() throws InvalidKeyException, IOException {

    byte[] sender = thisApp.getBytes();
    numBytes += sender.length;
    data.set(data.size() - 1, sender); // last element in history = sender

    byte[] mac = mac(mergeData());
    writeBytes(mac);
  }

  /* Generates two authentication keys and two encryption/decryption keys. All
   * four of these keys are derived from the master secret and are implemented
   * in the same order for both server and client. Each key is used for both
   * encryption and decryption depending on which application is sending data.
   */
  protected void generateKeys() throws NoSuchAlgorithmException {

    sr = SecureRandom.getInstance("SHA1PRNG");

    // TODO: Without re-initializing the SecureRandom instance, each key on the
    // client-side does not match the same key on the server-side. Why?

    sr.setSeed(sharedSecretKey);
    kg.init(sr);

    clientAuth = kg.generateKey();
    serverAuth = kg.generateKey();
    clientCrypt = kg.generateKey();
    serverCrypt = kg.generateKey();
  }

  /* To be implemented by the mySSLClient and mySSLServer classes. The methods
   * which the handshake will invoke are designed to simply be called in
   * different order.
   *
   * For example, the client application will first initiate the handshake with
   * the certificateOut method. Thus, the server first calls certificateIn and
   * waits for the incoming message.
   *
   * However, the specifics of the implementation are left to the user if the
   * mySSLClient and mySSLServer classes are not used.
   */
  protected abstract void handshake(String keyPath, String certPath) throws
          IOException, CertificateException,
          NoSuchAlgorithmException, NoSuchProviderException,
          InvalidKeyException, SignatureException, BadPaddingException,
          ShortBufferException, IllegalBlockSizeException,
          InvalidAuthenticationException, InvalidKeySpecException;

  /*****************************************************************************
   DATA TRANSFER PHASE

   The following methods are for use in the data transfer protocol. Read and
   write are intended for use by the user and are available to any subclass.

   ****************************************************************************/

  /* Returns the record header contianing the type of data being transferred and
   * the length of the data.
   *
   * -----------------
   * | TYPE | LENGTH |
   * -----------------
   */
  private byte[] generateHeader(Record type, int length) throws
          NotSupportedException {

    assert length < Integer.MAX_VALUE;

    // Q. What is the largest file expected for normal use? What happens in
    // real-world applications if the file length is too large? I assume the
    // program breaks it into segments but what is a realistic max size?

    if (!type.equals(Record.DATA)) {

      // TODO: Add support for HANDSHAKE records.
      throw new NotSupportedException("Supported Records: DATA");
    }

    // TODO: Add FORMAT field for differentiating between files and text.

    byte[] _type = ("DATA").getBytes();
    buf = ByteBuffer.allocate(Integer.BYTES + _type.length);
    buf.putInt(length);
    buf.put(_type);

    return buf.array();
  }


  /* Returns a 20 byte MAC uniquely representing the specified arguments. The
   * key is intended to be the authorization key derived from the shared secret
   * key.
   */
  private byte[] generateMac(byte[] header, byte[] data, byte[] key) throws
          InvalidKeyException {

    assert seqNum < Integer.MAX_VALUE;

    // Q. When should the sequence number reset in the unlikely case that
    // billions of messages are exchanged without closing the connection?

    buf = ByteBuffer.allocate(Integer.BYTES + header.length + data
            .length + key.length);

    buf.putInt(++seqNum);
    buf.put(header);
    buf.put(data);
    buf.put(key);

    return mac(buf.array());
  }

  /* Helper method used to authenticate each message received by comparing the
   * MAC with a locally generated MAC of the same data.
   */
  private void authenticate(byte[] header, byte[] data, byte[] mac) throws
          InvalidKeyException, InvalidAuthenticationException {

    byte[] key = thatApp.equals("CLNT") ? clientAuth.getEncoded() :
            serverAuth.getEncoded();

    buf = ByteBuffer.allocate(Integer.BYTES + header.length + data
            .length + key.length);
    buf.putInt(++seqNum);
    buf.put(header);
    buf.put(data);
    buf.put(key);

    byte[] test = mac(buf.array());
    if (!Arrays.equals(mac, test)) {
      throw new InvalidAuthenticationException("Data Transfer Authentication " +
              "" + "Failed: HMAC");
    }
  }

  /*
   * Writes the specified bytes as encrypted data to this socket's output
   * stream.
   *
   * TODO: The documentation for read and write should be targeted at the user.
   */
  protected void write(byte[] data) throws NotSupportedException,
          InvalidKeyException, IOException, BadPaddingException,
          IllegalBlockSizeException {

    byte[] header = generateHeader(Record.DATA, data.length);
    byte[] key = thisApp.equals("CLNT") ? clientAuth.getEncoded() :
            serverAuth.getEncoded();
    byte[] mac = generateMac(header, data, key);

    buf = ByteBuffer.allocate(data.length + mac.length);
    buf.put(data);
    buf.put(mac);

    byte[] record = buf.array();

    aes.init(Cipher.ENCRYPT_MODE, thisApp.equals("CLNT") ? clientCrypt :
            serverCrypt);

    /* ---------------------------------
     * | HEADER | DATA | MAC | PADDING |
     * --------------------------------- */

    writeBytes(header);
    writeBytes(aes.doFinal(record));
  }

  /* Returns any available bytes from this socket's input stream as decrypted
   * data.
   *
   * TODO: The documentation for read and write should be targeted at the user.
   */
  protected byte[] read() throws IOException, InvalidHeaderException,
          InvalidKeyException, BadPaddingException,
          IllegalBlockSizeException, InvalidAuthenticationException {

    readBytes(byteContainer); // header is not encrypted
    byte[] header = byteContainer.getVal();

    buf = ByteBuffer.wrap(header);
    int length = buf.getInt(); // length of incoming data segment
    byte[] type = new byte[TYPE_LENGTH];
    buf.get(type);

    assert buf.remaining() == 0;

    // only "DATA" records should ever be received in the data transfer phase
    if (!Arrays.equals(type, ("DATA").getBytes())) {
      throw new InvalidHeaderException("Invalid Data Transfer Header");
    }

    aes.init(Cipher.DECRYPT_MODE, thatApp.equals("CLNT") ? clientCrypt :
            serverCrypt);

    readBytes(byteContainer);
    byte[] record = aes.doFinal(byteContainer.getVal());

    buf = ByteBuffer.wrap(record);
    byte[] data = new byte[length];
    byte[] mac = new byte[MAC_LENGTH];
    buf.get(data);
    buf.get(mac);

    assert buf.remaining() == 0;
    authenticate(header, data, mac);

    return data;
  }

}
