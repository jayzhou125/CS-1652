import java.io.*;
import java.net.*;
import java.util.*;

public final class WebServer {
	// Set the port number.
	private final static int port = 6789;

	public static void main(String[] args) throws Exception {
		// Establish the listen socket.
		/* Fill in */
		ServerSocket serverSocket = new ServerSocket(port);

		// Process HTTP service requests in an infinite loop.
		while (true) {
			// Listen for a TCP connection request.
			/* Fill in */
			Socket socket = serverSocket.accept();

			// Construct an object to process the HTTP request message.
			HttpRequestRunnable request = new HttpRequestRunnable( /* Fill in */ socket);

			// Create a new thread to process the request.
			Thread thread = new Thread(request);

			// Start the thread.
			thread.start();
		}
	}
}

final class HttpRequestRunnable implements Runnable {
	final static String CRLF = "\r\n";

	private Socket clientSocket;

	HttpRequestRunnable(Socket socket) {
		clientSocket = socket;
	}

	@Override

	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void processRequest() throws Exception {
		// Get a reference to the socket's input and output streams.
		InputStream is = /* Fill in */ clientSocket.getInputStream();
		DataOutputStream os = /* Fill in */ new DataOutputStream(clientSocket.getOutputStream());

		// Set up input stream filters.
		/* Fill in */
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = /* Fill in */ new BufferedReader(isr);

		// Get the request line of the HTTP request message.
		String requestLine = /* Fill in */ br.readLine();

		// Display the request line.
		System.out.println();
		System.out.println(requestLine);

		// Get and display the header lines.
		String headerLine = null;
		while ((headerLine = br.readLine()).length() != 0) {
			System.out.println(headerLine);
		}

		// Extract the filename from the request line.
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken(); // skip over the method, which should be "GET"
		String fileName = tokens.nextToken();

		// Prepend a "." so that file request is within the current directory.
		fileName = "." + fileName;
		// Open the requested file.
		FileInputStream fis = null;
		boolean fileExists = true;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			fileExists = false;
		}

		// Construct the response message.
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		if (fileExists) {
			statusLine = /* Fill in */ "HTTP/1.1 200 OK" + CRLF;
			contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
		} else {
			statusLine = /* Fill in */ "HTTP/1.1 404 Not Found" + CRLF;
			contentTypeLine = /* Fill in */ "Content-type: " + "text/html" + CRLF;
			entityBody = "<HTML>" + "<HEAD><TITLE>Not Found</TITLE></HEAD>" + "<BODY>Not Found</BODY></HTML>";
		}

		// Send the status line.
		os.writeBytes(statusLine);

		// Send the content type line.
		os.writeBytes(/* Fill in */ contentTypeLine);

		// Send a blank line to indicate the end of the header lines.
		os.writeBytes(CRLF);

		// Send the entity body.
		if (fileExists) {
			sendBytes(fis, os);
			fis.close();
		} else {
			os.writeBytes(/* Fill in */ entityBody);
		}

		// Close streams and socket.
		os.close();
		br.close();
		clientSocket.close();

	}

	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
		// Construct a 1K buffer to hold bytes on their way to the socket.
		byte[] buffer = new byte[1024];
		int bytes = 0;

		// Copy requested file into the socket's output stream.
		while ((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
	}

	private static String contentType(String fileName) {
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		}
		if (fileName.endsWith(".css")) {
			return "text/css";
		}
		if (fileName.endsWith(".js")) {
			return "text/javascript";
		}
		if (fileName.endsWith(".gif")) {
			return "image/gif";
		}
		if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".jfif")
				|| fileName.endsWith(".pjpeg") || fileName.endsWith("pjp")) {
			return "image/jpeg";
		}
		if (fileName.endsWith(".apng")) {
			return "image/apng";
		}
		if (fileName.endsWith(".bmp")) {
			return "image/bmp";
		}
		if (fileName.endsWith(".ico") || fileName.endsWith(".cur")) {
			return "image/x-icon";
		}
		if (fileName.endsWith(".png")) {
			return "image/png";
		}
		if (fileName.endsWith(".svg")) {
			return "image/svg+xml";
		}
		if (fileName.endsWith(".tif") || fileName.endsWith("tiff")) {
			return "image/tiff";
		}
		if (fileName.endsWith(".webp")) {
			return "image/webp";
		}
		if (fileName.endsWith(".pdf")) {
			return "application/pdf";
		}
		return "application/octet-stream";
	}

}
