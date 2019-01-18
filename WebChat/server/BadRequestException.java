
@SuppressWarnings("serial")
public class BadRequestException extends Exception {
	BadRequestException() {
		super("BadRequestException");
	}
	BadRequestException(String message) {
		super(message);
	}
	BadRequestException(Throwable throwable) {
		super(throwable);
	}
}


/*
Throw this exception in the method/constructor that read's the user's request if it's not a valid request (for example, the first words is not GET, or the last word on the first line is not HTTP/1.1). 

Also throw this error if any other exception is thrown while decoding the request.  To do this, you'll have a try/catch block that catches IOExceptions, and in the catch block, you'll throw a BadRequestException.

Use the telnet command (run telnet localhost 8080 then start typing in the terminal) to verify that you're actually triggering this type of exception with a bad request.  Your server should not crash!  You should add a catch block for BadRequestExceptions in your while(true) loop containing accept().
*/