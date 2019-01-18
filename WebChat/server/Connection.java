import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Set;

public class Connection implements Runnable {

	private Server server;
	private SocketChannel socketChannel;
	private Socket socket;
	private Room room;
	private Pipe pipe;

	public Connection(Server server, SocketChannel socketChannel) throws IOException {
		this.server = server;
		this.socketChannel = socketChannel;
		socket = socketChannel.socket();
		pipe = Pipe.open();
	}

	/*
	 * Creates a new connection point between the client and the server thread
	 * in which the code is being run (see thread pool in the server class).
	 * Listens for and responds to HTTP requests and web socket requests. When a
	 * web socket request is valid, begins listening for and responding to
	 * client messages.
	 */
	@Override
	public void run() {
		try {
			Request request = new Request(socket);
			Response response = new Response(socket, request);
			if (response.hasCompletedValidHandshake()) {
				Messager messager = new Messager(socket);
				manageChannel(messager);
			}
			socket.close();
			
			//room.removeClient(this);
			
			/*
			 * These catch statments may need to be modified at a later date.
			 * Some exceptions may need to be caught closer to their origins.
			 */
		} catch (IOException e) {
			printErrorMessage(e);
		} catch (NoSuchAlgorithmException e) {
			printErrorMessage(e);
		} catch (Exception e) {
			printErrorMessage(e);
		}
	}

	// --------------------------------------------------------------------------
	// Is there ANY possible way to simplify this function without making it
	// more
	// confusing? Perhaps make it its own class and save the pipe as a member
	// variable there?
	// --------------------------------------------------------------------------

	/*
	 * Creates a message gateway for receiving and sending messages from either
	 * the current socket or other clients. Checks whether the protocol has been
	 * switched to web sockets before permitting messages. Throws an IOException
	 * if there is a problem closing the socket when catching other exceptions.
	 */
	private void manageChannel(Messager messager) throws IOException {
		try {
			joinRoom(messager);
			Selector selector = Selector.open();
			/*
			 * Setup two channels. The socket for messages to this client (as
			 * well as messages originating from this client which will then be
			 * written to the pipe from the Room object). The pipe for messages
			 * sent from the Room object.
			 */
			socketChannel.configureBlocking(false);
			pipe.source().configureBlocking(false);
			socketChannel.register(selector, SelectionKey.OP_READ);
			pipe.source().register(selector, SelectionKey.OP_READ);
			/*
			 * Configures a set of selector keys to which the desired event will
			 * be attached. In this case, listen for whether there is data to
			 * read from the given data stream.
			 */
			while (!(socket.isClosed())) {
				selector.select();
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = keys.iterator();
				while (iterator.hasNext()) {
					SelectionKey key = iterator.next();
					/*
					 * Only do the following if either the socket channel or the
					 * pipe contain data to be read.
					 */
					if (key.isReadable()) {
						iterator.remove();
						/*
						 * Keys must be cancelled before read/write cycles in
						 * order to prevent the selector from notifying the
						 * channel of the same information multiple times.
						 */
						socketChannel.keyFor(selector).cancel();
						socketChannel.configureBlocking(true);
						if (key.channel() == socketChannel) {
							// Reads data from the socket's DataInputStream and
							// writes to the pipe.
							String message = messager.read();
							room.add(message);
						} else {
							/*
							 * Reading from the pipe requires configuration for
							 * both the socket and the pipe.
							 */
							pipe.source().keyFor(selector).cancel();
							pipe.source().configureBlocking(true);
							/*
							 * Reads data from the pipe's ObjectInputStream and
							 * writes to the same to the socket.
							 */
							String message = read();
							messager.send(message);
						}
						// Reconfigure both pipes after read/write is complete.
						pipe.source().configureBlocking(false);
						socketChannel.configureBlocking(false);
						selector.selectNow();
						pipe.source().register(selector, SelectionKey.OP_READ);
						socketChannel.register(selector, SelectionKey.OP_READ);
					} else {
						socket.close();
						throw new ReadableKeyException();
					}
				}
			}
		} catch (Exception e) {
			printErrorMessage(e);
			socket.close();
		}

	}

	public String read() throws IOException, ClassNotFoundException {
		ObjectInputStream input = new ObjectInputStream(Channels.newInputStream(pipe.source()));
		Object object = input.readObject();
		String message = null;
		if (object instanceof Message) {
			message = ((Message) object).getMessage();
		} else {
			socket.close();
		}
		return message;
	}

	/*
	 * Joins the room specified by the client. Currently setup for String input.
	 * Need to change to JSON. he first message received after the handshake is
	 * interpreted as the join message.
	 */
	private void joinRoom(Messager messageManager) throws IOException, JoinRoomException {
		String message = messageManager.read();
		String[] joinMessage = message.split(" ");
		System.out.println("success");
		System.out.println(message);
		if (joinMessage[0].equals("join") && joinMessage.length == 2) {
			joinMessage[1] = joinMessage[1].toLowerCase();
			System.out.println(joinMessage[1]);
			room = server.getRoom(joinMessage[1]);
			room.addClient(this);
		} else {
			socket.close();
			throw new JoinRoomException("Unable to join room. Check join messaage format.");
		}
	}

	/*
	 * Writes messages from the Room objcet to the client socket. Called from
	 * the room object to prevent exposing the Pipe member variable.
	 */
	public void write(String m) throws IOException {
		Message message = new Message(m);
		ObjectOutputStream output = new ObjectOutputStream(Channels.newOutputStream(pipe.sink()));
		output.writeObject(message);
	}

	/*
	 * Prints the exception class and the class wherein the exception was
	 * caught. An accessory to the getMessage method where seemingly random
	 * numbers often appear. Print stack trace if further information is needed.
	 */
	private void printErrorMessage(Exception e) {
		System.out.println(e.getClass() + " in class Server: " + e.getMessage());
	}

}
