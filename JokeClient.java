/*--------------------------------------------------------

1. Ashay Kargaonkar / Date: 09-27-2020

2. Precise command-line compilation examples / instructions:

> javac JokeClient.java


3. Precise examples / instructions to run this program:

	None

4. List of files needed for running the program.
	
	No files are needed to run the program. This file can run independently. But below are file names that will be required to test.

 1. JokeServer.java
 2. JokeClientAdmin.java

5. Notes:

1. 	I am able to change the mode from "Joke" to "Proverb but not able to make server send messages accordingly to client".
2. 	I can randomize the jokes and proverb but have not implemented the non-repeatable cycle.
3. 	Unable to receive messages (as they should) when both the sockets are open simultaneously in JokeServer. You have to press enter from JokeClientAdmin or JokeClient to get the outputs.
	This is happening because i am unable differentiates between sockets' input, from JokeClient and JokeClientAdmin, to code it in JokeServer. 
4. Unable to handle multiple JokeClientAdmin

----------------------------------------------------------*/

import java.io.*; // importing input-output package
import java.net.*; // importing networking package

public class JokeClient {
	public static void main(String args[]) {
		String serverName;
		if (args.length < 1)
			serverName = "localhost"; // default serverName is "localhost"
		else
			serverName = args[0];

		System.out.println("Ashay Kargaonkar's Joke Client, 1.8.\n");
		System.out.println("Using server: " + serverName + ", Port: 1565");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); // input of domain name from user.
		try {
			String response;
			do {
				System.out.print("If want joke or proverb press enter, or type quit");
				System.out.println();
				System.out.flush(); // flushes the output stream
				response = in.readLine(); // accepts "" from client
				if (response.indexOf("quit") < 0) {

					requestJokeServer(response, serverName); // below 'requestJokeServer' function is called.
				}
			} while (response.indexOf("quit") < 0);
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

	static void requestJokeServer(String responseFromClient, String serverName) {
		Socket sock; // Socket variable created
		BufferedReader fromServer; // BufferedReader variable created
		PrintStream toServer; // PrintStream variable created
		String textFromServer;

		try {

			sock = new Socket(serverName, 1565);

			fromServer = new BufferedReader(
					new InputStreamReader(sock.getInputStream())); /*
																	 * using input stream to get output from JokeServer
																	 */

			toServer = new PrintStream(sock.getOutputStream()); // send domain name to server

			toServer.println(responseFromClient); // print response from client name
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