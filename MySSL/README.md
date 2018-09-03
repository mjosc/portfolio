# MySSL

A variation of the secure socket layer. Implements a two-way authentication handshake and provides access to read and write methods for applications requiring network encryption. Inheritance of either the MySSLClient or the MySSLServer class provides a simple API.

Implements RSA and AES encryption algorithms for handshake authentication and secure data transfer, respectively.

The current version requires both a private key and self-signed certificate. These are generated using OpenSSL and saved to the local filesystem. See *Getting Started* for details.

**WARNING: All security algorithms are provided through the java.security and java.crypto libraries. However, this implementation is purely academic and not intended for commercial applications. There are absolutely no security guarantees.**

## Getting Started

### API Documentation

MySSL provides an extremely simple API. The MySSLClient and MySSLServer classes are designed to be inherited. They require only a valid TCP connection (just pass a Socket instance to the constructor) and the paths to the OpenSSL-generated private key and OpenSSL-generated self-signed certificate.

```java
public class TestClient extends mySSLClient {

  private static String KEY_PATH = "../resources/client/private.der"
  private static String CERT_PATH = "../resrouces/client/certificate.cert"

  public TestClient(String host, int port) {

    super(new Socket(host, port), KEY_PATH, CERT_PATH);
  }
}
```

With the appropriate arguments passed to the constructor, any subclass of either the MySSLClient or the MySSLServer parent class will automatically complete a two-way authentication handshake when an instance of that object is created. The user need only decide how to invoke the MySSL *write* and *read* methods.

```java
public static void main(String[] args) {

	/* Authentication handshake completed here! No need to do anything except
	 * ensure the server is running beforehand.
	 */
	TestClient testClient = new TestClient(args[0], Integer.parseInt(args[1]));

	/*
	 * Encrypt/decrypt any byte array with a call to MySSL.write() or MySSL.read().
	 * Remember, TestClient inherits from MySSLClient.
	 */

	byte[] message = ("This message will be encrypted").getBytes();
	testClient.write(message);

	byte[] serverResponse = testClient.read();
}
```

### A Quick Guide to OpenSSL

This project currently uses OpenSSL to create a private key and self-signed certificate. Each client or server instance requires its own unique key and certificate. Be sure to follow these steps for both client and server.

##### Step 1: Generate private and public keys
```
$ openssl genrsa -out private.pem 2048
$ openssl rsa -in private.pem -pubout > public.pem
```

##### Step 2: Generate java-readable versions of each key
```
$ openssl rsa -in private.pem -pubout -outform DER -out public.der
$ openssl pkcs8 -topk8 -inform PEM -outform DER -in private.pem -out private.der -nocrypt
```

##### Step 3: Create a certificate signature request and subsequently sign the certificate with the private key
```
$ openssl req -new -key private.pem -out request.csr
$ openssl x509 -req -days 365 -in request.csr -signkey private.pem -sha1 -out signed.cert
```

## Demonstration

### Setup

This project was written and tested on macOS High Sierra 10.13.4.

```
$ javac *.java
```

There are three provided test classes: (1) TestClient, (2) TestServer, and (3) TestConnection. The former two represent the entry points to two separate applications. TestClient runs as a client application which requests files from the TestServer application. TestServer returns either the requested file or notifies the client the file does not exist.

TestConnection is a simple implementation of the java Runnable interface. A new instance is created per thread within the TestServer application.

These two applications use the private keys and certificates as described in *Getting Started*. To run the demonstration, either use those located in the provided *resources* directory or create a new key and certificate.

The default path for file I/O is *"../resources/results/" + filename* and *"../resources/tests/" + filename* for the client- and server-side applications, respectively. These will need to be changed if not using the provided *resources* directory.

##### Using the default paths

Simply clone this project or copy the resources directory and ensure it is placed in the same directory as *src*. Make sure the compiled .class files are within a sibling directory of *resources*. Whether that is within the *src* directory or a *build* directory is not important.

##### Modifying the test code for non-default paths

```java

public class TestClient extends MySSLClient {

  private static String KEY_PATH = "new_path_to_private_key";
  private static String CERT_PATH = "new_path_to_certificate";

  ...

  File file = new File("new_path_to_file");

  ...
}

public class TestConnection extends MySSLServer implements Runnable {

  private static String KEY_PATH = "new_path_to_private_key";
  private static String CERT_PATH = "new_path_to_certificate";

  ...

  File file = new File("new_path_to_file");

  ...
}
```

### Execution

As described, there are two separate test applications: TestClient and TestServer. It will be easiest to dicern the output if the TestServer is run in its own terminal window (rather than as a background process).

##### Step 1: Start the server with the port number

```
$ java TestServer 8080
```

##### Step 2: Start the client with the host and port number

```
$ java TestClient 8080
```

##### Expected output:

```
$ java TestServer 8080
SERVER: Master Key Established
SERVER: Secret Keys Established
SERVER: Handshake Completed
```

```
$ java TestClient localhost 8080
CLIENT: Master Key Established
CLIENT: Secret Keys Established
CLIENT: Handshake Completed
```

##### Step 3: Request a file

```
$ java TestClient localhost 8080
...
test.pdf
```

##### Expected Output - File Exists

```
$ java TestServer 8080
...
SERVER: Received request for file at /some_path/test.pdf
SERVER: test.pdf written to client
```

```
$ java TestClient localhost 8080
...
test.pdf
CLIENT: Requested test.pdf
CLIENT: Valid request. Waiting for file transfer
CLIENT: Received requested file. Saved at /some_path/test.pdf
```

##### Expected Output - File Does Not Exist

```
$ java TestServer 8080
...
SERVER: Received request for file at /some_path/some_file
SERVER: some_file does not exist. Awaiting additional requests
```

```
$ java TestClient localhost 8080
...
some_file
CLIENT: Requested some_file
CLIENT: Invalid request. The file does not exist
```

##### Step 4: Exit

The client-server connection remains open until either an exception is thrown or the connection is closed manually with Ctrl + C.

### Author

Matt Josse

### Additional Information

This project was initially implemented as an assignment for CS6014: Networking and Network Security. No template code was provided.

After completing the necessary requirements, the project was re-written as if it were its own library as described above. The objective developed from simply sending an encrypted 50kb file post-handshake protocol to providing a platform upon which any networking application could be built using a simple API to encrypt its traffic.
