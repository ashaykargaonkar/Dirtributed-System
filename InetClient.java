import java.io.*; // importing input-output package
import java.net.*; // importing networking package

public class InetClient {
	public static void main(String args[]) {
		String serverName;
		if (args.length < 1)
			serverName = "localhost"; // default serverName is "localhost"
		else
			serverName = args[0];

		System.out.println("Clark Elliott's Inet Client, 1.8.\n");
		System.out.println("Using server: " + serverName + ", Port: 1565");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); // input of domain name from user.
		try {
			String name;
			do {
				System.out.print("Enter a hostname or an IP address, (quit) to end: ");
				System.out.flush(); // flushes the output stream
				name = in.readLine(); // accepts input of IP address or hostname.
				if (name.indexOf("quit") < 0)
					getRemoteAddress(name, serverName); // below 'getRemoteAddress' function is called.
			} while (name.indexOf("quit") < 0);
			System.out.println("Cancelled by user request.");
		} catch (IOException x) {
			x.printStackTrace();
		}
	}

	static String toText(byte ip[]) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < ip.length; ++i) {
			if (i > 0)
				result.append(".");
			result.append(0xff & ip[i]);
		}
		return result.toString();
	}

	static void getRemoteAddress(String name, String serverName) {
		Socket sock; // Socket variable created
		BufferedReader fromServer; // BufferedReader variable created
		PrintStream toServer; // PrintStream variable created
		String textFromServer;

		try {

			sock = new Socket(serverName, 1565);
			/*
			 * new client socket is created with parameters as serverName and port number
			 * and connection is opened
			 */

			fromServer = new BufferedReader(
					new InputStreamReader(sock.getInputStream())); /*
																	 * using input stream to get output from server
																	 */
			toServer = new PrintStream(sock.getOutputStream()); // send domain name to server

			toServer.println(name); // print domain name
			toServer.flush(); // flushing output stream

			for (int i = 1; i <= 3; i++) { // read maximum of 3 lines from server
				textFromServer = fromServer.readLine(); // reading lines
				if (textFromServer != null)
					System.out.println(textFromServer);
			}
			sock.close(); // closing client socket
		} catch (IOException x) { // catching any exceptions thrown in try block
			System.out.println("Socket error.");
			x.printStackTrace();
		}
	}
}