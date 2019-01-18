import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	
	private ServerSocketChannel serverSocketChannel;
	private Selector selector;
	private HashMap<String,Room> rooms = new HashMap<String,Room>();
	
	public Server() {
		int port = 8080;
		try {
			selector = Selector.open();
			newChannel(port);
		} catch (Exception e) {
			printErrorMessage(e);
			e.printStackTrace();
			/* Is there a better way to handle an exception at the
			 * instantiation of the server socket channel? */
			System.exit(1);
		}
	}
	
	/* Create a client socket channel only when there is a client
	 * with whom to connect. Run each channel in a new thread. */
	public void run() {
		ExecutorService pool = Executors.newFixedThreadPool(50);
		while (true) {
			try {
				selector.select();
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectionKeys.iterator();
				while(iterator.hasNext()) {
					SelectionKey key = iterator.next();
					if (key.isAcceptable()) {
						iterator.remove();
						// Blocks until a client is accepted.
						SocketChannel socketChannel = serverSocketChannel.accept();
						pool.execute(new Connection(this, socketChannel));
					}
				}
			} catch (Exception e) {
				/* The server should not fail if there is a problem
				 * establishing the client socket. Simply wait for the
				 * next client. */
				printErrorMessage(e);
				e.printStackTrace();
			}
		}
	}
	
	/* Creates a new server socket channel. There will only ever exist
	 * a single server socket channel but this setup is required to
	 * create multiple read/write channels with the client socket. */
	private void newChannel(int port) throws IOException {
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(port));
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	/* Retrieves the Room object of the given name if it already exists.
	 * If not, the Room object is created and the same object is
	 * returned. */
	public Room getRoom(String roomName) {
		if (rooms.containsKey(roomName)) {
			return rooms.get(roomName);
		} else {
			Room newRoom = new Room(roomName);
			rooms.put(roomName, newRoom);
			return newRoom;
		}
	}
	
	/* Prints the exception class and the class wherein the exception
	 * was caught. An accessory to the getMessage method where
	 * seemingly random numbers often appear. Print stack trace if
	 * further information is needed. */
	private void printErrorMessage(Exception e) {
		System.out.println(e.getClass() + " in class Server: " + e.getMessage());
	}
	
}
