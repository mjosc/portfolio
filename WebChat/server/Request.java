import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class Request {

	private String[] status;
	private HashMap<String, String> headers = new HashMap<String, String>();
	private File file;
	private String path;

	public Request(Socket socket) throws IOException {
		Scanner scanner = new Scanner(socket.getInputStream());
		read(scanner);
	}
	
	private void read(Scanner scanner) {
		status = scanner.nextLine().split(" ");
		while (true) {
			String s = scanner.nextLine();
			if (s.equals("")) {
				break;
			}
			String[] data = s.split(": ");
			headers.put(data[0], data[1]);
		}
	}

	/*
	 * This should make it easy to add other status line nuances at a later
	 * point.
	 */
	private boolean isValidStatus() {
		return (status[0].equals("GET") && status[2].equals("HTTP/1.1"));
	}

	/*
	 * Currently ignoring status[1]. See Mozilla documentation for suggested
	 * implementation.
	 */
	public boolean isValidWebSocketRequest() {
		return (isValidStatus() && headers.containsKey("Sec-WebSocket-Key"));
	}

	/*
	 * The url at status[1] is dealt with separately. Files which do not exist
	 * may still be requested within a valid HTTP request.
	 */
	public boolean isValidHTTPRequest() {
		return isValidStatus();
	}

	public long getFileLength() {
		return file.length();
	}

	public boolean isValidPath() {
		path = "Resources/" + status[1];
		file = new File(path);
		return (file.exists());
	}

	public String getPath() {
		return path;
	}

	public HashMap<String, String> getRequestHeaders() {
		return headers;
	}

}
