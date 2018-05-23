import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* Provides a testing platform for the mySSL protocol. Implemented similar to a
 * basic multithreaded web server. Each thread executes an instance of a
 * Runnable object which also extends the mySSLServer class.
 */
public class TestServer {

  public static void main(String[] args) {
    new TestServer().run(Integer.parseInt(args[0]));
  }

  /* Listens for network requests and attempts to establish a connection with
   * each incoming client.
   *
   * Any exception thrown while attempting to instantiate a ServerSocket object
   * results in failure to boot the server and the system will exit. However,
   * exceptions thrown from within either the Socket or the Runnable instance
   * simply result in the connection being closed.
   */
  public void run(int port) {

    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(port);
    } catch (Exception e) {
      System.err.println("ERROR: " + e.getMessage());
      System.exit(1);
    }

    Socket clientSocket = null;
    ExecutorService threadPool = Executors.newFixedThreadPool(100);
    while (true) {
      try {
        clientSocket = serverSocket.accept();
        threadPool.execute(new TestConnection(clientSocket));
      } catch (Exception e) {
        System.err.println("LOG: " + e.getMessage());
      }
    }
  }

}
