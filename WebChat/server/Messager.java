import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Messager {

	/*
	 * Individual bytes are required as private members only when echoing. If
	 * there are additional features and exception handling added at a later
	 * point, differing fin, opcode, and other values may be required, rather
	 * than simply passing the input values to the output stream.
	 */

	private DataInputStream input;
	private DataOutputStream output;

	public Messager(Socket socket) throws IOException {
		input = new DataInputStream(socket.getInputStream());
		output = new DataOutputStream(socket.getOutputStream());
	}

	/* Reads the initial data from the client, verifies its format, and returns
	 * the portion of the message containing the textual data.
	 */
	public String read() throws IOException {
		byte byte1 = input.readByte();
		byte byte2 = input.readByte();
		String payload = null;
		if (isValidFin(byte1) && isValidOpcode(byte1) && isValidMaskCode(byte2)) {
			payload = decode(getLength(byte2), getMaskKey());
		} else {
			// Throw exception
			// Handle a logout message here? See isValidFin().
		}
		return payload;
	}

	/*
	 * Writes the data back to the client(s). Notice some input values are
	 * passed directly through to the output stream while others are not. The
	 * message.length attribute is used in place of the getLength method because
	 * the message has already been read (no new byte[] dependent upon message
	 * length for instantiation) and there is no need to store the length as a
	 * private member variable.
	 */

	public void send(String m) throws IOException {
		String username = m.substring(0, m.indexOf(" "));
		String message = m.substring(m.indexOf(" ") + 1);
		String jsonString = "{\"user\" : \"" + username + "\", \"message\" : \"" + message + "\"}";
		System.out.println(message);
		byte[] payload = jsonString.getBytes();
		byte[] headers = null;
		if (payload.length < 126) {
			headers = new byte[2];
			headers[0] = -127;
			headers[1] = (byte) payload.length;
		} else if (payload.length < 5000) {
			/*
			 * The message length is arbitrarily set. The max value is approx.
			 * 65000 characters. bytes[1] is not equivalent to byte2 due to the
			 * requirement for an outgoing mask bit value of 0.
			 */
			headers = new byte[4];
			headers[0] = -127;
			headers[1] = 126;
			headers[2] = (byte) (payload.length >>> 8);
			headers[3] = (byte) (payload.length);
		} else {
			// Throw TBD exception.
		}
		output.write(headers);
		output.write(payload);
	}

	/*
	 * Valid messages from the client are masked. After the byte(s) containing
	 * the length of the message, there are four subsequent bytes containing a
	 * masking key.
	 */
	private String decode(int length, byte[] maskKey) throws IOException {
		byte[] message = new byte[length];
		input.readFully(message);
		for (int i = 0; i < length; i++) {
			// The key repeats every four characters.
			message[i] ^= maskKey[i % 4];
		}
		String m = new String(message);
		return m;
	}

	/*
	 * This method must be called AFTER reading the message length and BEFORE
	 * reading the message. These four bytes are contained in-between. If not
	 * using the readFully method, it may be possible to simply start with an
	 * array of bytes for the entire input and the order of the method calls
	 * would not matter.
	 */
	private byte[] getMaskKey() throws IOException {
		byte[] maskKey = new byte[4];
		input.readFully(maskKey);
		return maskKey;
	}

	/*
	 * Returns the length of the message in number of bytes. If the length is
	 * too large for a single byte, use the 16 bit integer of the subsequent two
	 * bytes interpreted as Big-Endian. Messages larger than approx. 65,000
	 * characters will throw an exception.
	 */
	private int getLength(byte b) throws IOException {
		int length = (byte) (b & 0x7F);
		if (length == 126) {
			length = input.readUnsignedShort();
		} else {
			// Throw InvalidMessageLength exception.
		}
		return length;
	}

	/*
	 * For all purposes of this application the fin bit should be 1. Only small
	 * packages of text data are intended to sent/received. When a fin bit value
	 * of 0b8 is received, the socket should close (Javascript should send this
	 * value when "logout" is selected.
	 */
	private boolean isValidFin(byte b) {
		byte fin = (byte) ((b >>> 4) & 0xF);
		return fin == 8;
	}

	/*
	 * The opcode for the current specifications is 0001. There may arise a need
	 * to support additional codes at a later time.
	 */
	private boolean isValidOpcode(byte b) {
		byte opcode = (byte) (b & 0xF);
		return opcode == 1;
	}

	/*
	 * All incoming messages should have a mask code bit value of 1. This should
	 * not change even with the addition of new features.
	 */
	private boolean isValidMaskCode(byte b) {
		byte maskCode = (byte) ((b >>> 7) & 0x1);
		return maskCode == 1;
	}

}
