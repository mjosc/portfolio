import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

public class Response {
	
	private PrintWriter writer;
	private Request reqHandler;
	private OutputStream output;
	private boolean handshakeCompleted = false;
	
	public Response(Socket socket, Request reqHandler) throws IOException, NoSuchAlgorithmException {
		writer = new PrintWriter(socket.getOutputStream());
		output = socket.getOutputStream();
		this.reqHandler = reqHandler;
		respond();
	}
	
	public void respond() throws IOException, NoSuchAlgorithmException {
		if (reqHandler.isValidWebSocketRequest()) {
			sendHeaders(getHandshakeHeaders());
			handshakeCompleted = true;
		} else if (reqHandler.isValidHTTPRequest()) {
			FileInputStream input;
			if (reqHandler.isValidPath()) {
				input = new FileInputStream(reqHandler.getPath());
				sendHeaders(getValidPathHeaders());
				sendData(input);
			} else {
				input = new FileInputStream("Resources/404.html");
				sendHeaders(get404Headers());
				sendData(input);
			}
		} else {
			// Create an exception if there is no 404 html. What other kinds of exceptions could occur here? 
		}
	}
	
	private void sendHeaders(ArrayList<String> headers) {
		// don't forget to add the \r\n
		for (String header: headers) {
			writer.print(header + "\r\n");
		}
		writer.print("\r\n");
		writer.flush();
	}
	
	private void sendData(FileInputStream input) throws IOException {
		int bytes = 0;
		byte[] buffer = new byte[1024];
		do {
			bytes = input.read(buffer);
			if (bytes < 1) {
				break;
			}
			output.write(buffer, 0, bytes);
			writer.flush();
		} while (bytes > 0);
		input.close();
	}
	
	private ArrayList<String> getValidPathHeaders() {
		ArrayList<String> headers = new ArrayList<String>();
		headers.add("HTTP/1.1 200 OK");
		headers.add("Content-length: " + reqHandler.getFileLength());
		return headers;
	}
	
	private ArrayList<String> get404Headers() {
		ArrayList<String> headers = new ArrayList<String>();
		headers.add("HTTP/1.1 404 ERROR");
		return headers;
	}
	
	private ArrayList<String> getHandshakeHeaders() throws NoSuchAlgorithmException {
		ArrayList<String> headers = new ArrayList<String>();
		headers.add("HTTP/1.1 101 Switching Protocols");
		headers.add("Upgrade: websocket");
		headers.add("Connection: Upgrade");
		headers.add("Sec-WebSocket-Accept: " + getAcceptKey());
		return headers;
	}
	
	/* This method should only be called once it has been confirmed that
	 * the key exists.
	 */
	private String getAcceptKey() throws NoSuchAlgorithmException {
		HashMap<String,String> requestHeaders = reqHandler.getRequestHeaders();
		String magicString = requestHeaders.get("Sec-WebSocket-Key") + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
		MessageDigest digestor = MessageDigest.getInstance("SHA-1");
		byte[] stringInBytes = magicString.getBytes();
		byte[] hashed = digestor.digest(stringInBytes);
		String base64String = Base64.getEncoder().encodeToString(hashed);
		return base64String;
	}
	
	public boolean hasCompletedValidHandshake() {
		if (handshakeCompleted == true) {
			return true;
		}
		return false;
	}
	
}



























