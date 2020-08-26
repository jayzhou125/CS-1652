
/**
 * ProxyCache.java - Simple caching proxy
 *
 * $Id: ProxyCache.java,v 1.3 2004/02/16 15:22:00 kangasha Exp $
 *
 */

import java.net.*;
import java.io.*;
import java.util.*;

public class ProxyCache {
	/** Port for the proxy */
	private static int port;
	/** Socket for client connections */
	private static ServerSocket socket;

	static HashMap<String, HttpResponse> hmap;

	/** Create the ProxyCache object and the socket */
	public static void init(int p) {
		port = p;
		try {
			socket = /* Fill in */ new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Error creating socket: " + e);
			System.exit(-1);
		}
		hmap = new HashMap<String, HttpResponse>();
	}

	/** Read command line arguments and start proxy */
	public static void main(String args[]) {
		int myPort = 0;

		try {
			myPort = Integer.parseInt(args[0]);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Need port number as argument");
			System.exit(-1);
		} catch (NumberFormatException e) {
			System.out.println("Please give port number as integer.");
			System.exit(-1);
		}

		init(myPort);

		/**
		 * Main loop. Listen for incoming connections and spawn a new thread for
		 * handling them
		 */
		Socket client = null;

		while (true) {
			try {
				client = /* Fill in */ socket.accept();
				ProxyRunnable pr = new ProxyRunnable(client, hmap);
				Thread thread = new Thread(pr);
				thread.start();
			} catch (IOException e) {
				System.out.println("Error reading request from client: " + e);
				/*
				 * Definitely cannot continue processing this request, so skip to next iteration
				 * of while loop.
				 */
				continue;
			}
		}

	}
}

final class ProxyRunnable implements Runnable {
	private Socket client;
	private HashMap<String, HttpResponse> hmap = new HashMap<String, HttpResponse>();

	ProxyRunnable(Socket client, HashMap<String, HttpResponse> hmap) {
		this.client = client;
		this.hmap = hmap;
	}

	@Override
	public void run() {
		/* Fill in */;
		try {
			handle();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void handle() {
		Socket server = null;
		HttpRequest request = null;
		HttpResponse response = null;

		/*
		 * Process request. If there are any exceptions, then simply return and end this
		 * request. This unfortunately means the client will hang for a while, until it
		 * timeouts.
		 */

		/* Read request */
		try {
			DataInputStream fromClient = /* Fill in */ new DataInputStream(client.getInputStream());
			request = /* Fill in */ new HttpRequest(fromClient);
		} catch (IOException e) {
			System.out.println("Error reading request from client: " + e);
			return;
		}
		/* Fill in */
		/*
		 * Serve the requested object from cache if has been previously cached.
		 */
		String requestInfo = getRequestInfo(request);

		if (hmap.containsKey(requestInfo) && !request.isPost) {
			System.out.println("In Cache");
			response = hmap.get(requestInfo);
			try {
				DataOutputStream toClient = /* Fill in */ new DataOutputStream(client.getOutputStream());
				/* Write response to client. First headers, then body */
				System.out.println("writing headers from cache to client");
				toClient.writeBytes(/* Fill in */ response.toString());
				System.out.println("writing headers finished");
				// System.out.println(response);
				/* Fill in */
				System.out.println("writing body from cache to client");
				toClient.write(response.body, 0, response.length);
				// String body = new String(response.body);
				// System.out.println(body);
				System.out.println("writing body finished");
				System.out.println("sending");
				toClient.flush();
				System.out.println("sending finished");
				client.close();
				System.out.println("write finished.");
				// System.out.println
			} catch (Exception e) {
				System.out.println("requested object is not in cache");
				return;
			}
		} else {
			System.out.println("Not In Cache");
			/* Send request to server */
			try {
				System.out.println("try to send the request to server");
				/* Open socket and write request to socket */
				server = /* Fill in */ new Socket(request.getHost(), request.getPort());
				DataOutputStream toServer = /* Fill in */ new DataOutputStream(server.getOutputStream());
				/* Fill in */
				System.out.println("writing headers to server");
				toServer.writeBytes(request.toString());
				System.out.println("writing headers finished");
				if (request.isPost) {
					/* Fill in */;
					System.out.println("writing body to server");
					// toClient.write(response.body);
					toServer.write(request.body, 0, request.length);
					String body = new String(request.body);
					System.out.println(body);
					System.out.println("writing body finished");
				}
				System.out.println("sending");
				toServer.flush();
				System.out.println("sending finished");
			} catch (UnknownHostException e) {
				System.out.println("Unknown host: " + request.getHost());
				System.out.println(e);
				return;
			} catch (IOException e) {
				System.out.println("Error writing request to server: " + e);
				return;
			}
			/* Read response and forward it to client */
			try {
				System.out.println("try to send the response to client");
				DataInputStream fromServer = /* Fill in */ new DataInputStream(server.getInputStream());
				response = /* Fill in */ new HttpResponse(fromServer);
				DataOutputStream toClient = /* Fill in */ new DataOutputStream(client.getOutputStream());
				/* Write response to client. First headers, then body */
				System.out.println("writing headers to client");
				toClient.writeBytes(/* Fill in */ response.toString());
				System.out.println("writing headers finished");
				/* Fill in */
				System.out.println("writing body to client");
				// toClient.write(response.body);
				toClient.write(response.body, 0, response.length);
				// String body = new String(response.body);
				// System.out.println(body);
				System.out.println("writing body finished");
				System.out.println("sending");
				toClient.flush();
				System.out.println("sending finished");
				client.close();
				server.close();
				/* Insert object into the cache */
				/* Fill in */
				System.out.println("saving the response to cache");
				hmap.put(requestInfo, response);
				// System.out.println(requestInfo);
				System.out.println("the response is now in cache");
			} catch (IOException e) {
				System.out.println("Error writing response to client: " + e);
			}

		}
	}

	/*
	 * This method is to extract information from the http request, etracted info is
	 * in format String, and will be used as the key for the hasmap, hpum stands for
	 * Host,port number, Uri, Method
	 */
	private String getRequestInfo(HttpRequest request) {
		String hpum = null;
		StringBuilder sb = new StringBuilder();
		sb.append(request.getHost());
		sb.append(request.getPort());
		sb.append(request.URI);
		sb.append(request.method);
		hpum = sb.toString();
		// System.out.println(hpum);
		return hpum;
	}
}
