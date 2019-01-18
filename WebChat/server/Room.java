import java.io.IOException;
import java.util.ArrayList;

public class Room {

	private String name;
	private ArrayList<Connection> clients = new ArrayList<Connection>();
	private ArrayList<String> messages = new ArrayList<String>();

	public Room(String name) {
		this.name = name;
	}

	/*
	 * Adds new clients to room and sends all previous messages posted to the
	 * room. Need to remove clients when the socket is closed?
	 */
	public void addClient(Connection client) throws IOException {
		clients.add(client);
		for (String message : messages) {
			client.write(message);
		}
	}

	/*
	 * Removes client from the room when the socket is closed. This might happen
	 * due to a bug/problem or because the user logged out/closed the browser
	 * tab. Is this necessary at every instance where socket.close() is called?
	 */
	public void removeClient(Connection client) {
		// What is the best way to handle this with an ArrayList?
		clients.remove(client);
	}

	/*
	 * Adds new messages to the room and sends the messages to the pipe via
	 * calling the send method of the Connection class. This avoids exposing
	 * access to the pipe unnecessarily.
	 */
	public void add(String message) throws IOException {
		messages.add(message);
		send(message);
	}

	/*
	 * Sends an incoming message to the pipe for each of the clients currently
	 * logged into the room.
	 */
	public void send(String message) throws IOException {
		for (Connection client : clients) {
			client.write(message);
		}
	}

	/*
	 * Returns the name of the Room object. Used in conjunction with the Server
	 * class to check whether a room already exists.
	 */
	public String getRoomName() {
		return name;
	}
}
