/*--------------------------------------------------------

1. Ashay Kargaonkar / Date: 09-27-2020

2. Precise command-line compilation examples / instructions:

> javac JokeClientAdmin.java


3. Precise examples / instructions to run this program:

	None

4. List of files needed for running the program.
	
	No files are needed to run the program. This file can run independently. But below are file names that will be required to test.

 1. JokeServer.java
 2. JokeClient.java

5. Notes:

1. 	I am able to change the mode from "Joke" to "Proverb but not able to make server send messages accordingly to client".
2. 	I can randomize the jokes and proverb but have not implemented the non-repeatable cycle.
3. 	Unable to receive messages (as they should) when both the sockets are open simultaneously in JokeServer. You have to press enter from JokeClientAdmin or JokeClient to get the outputs.
	This is happening because i am unable differentiates between sockets' input, from JokeClient and JokeClientAdmin, to code it in JokeServer. 
4. Unable to handle multiple JokeClientAdmin

----------------------------------------------------------*/

import java.io.*;
import java.net.*;

public class JokeClientAdmin {
	public static void main(String args[]) {
		String serverName;
		String type = "Joke";
		if (args.length < 1)
			serverName = "localhost";
		else
			serverName = args[0];

		System.out.println("Ashay Kargaonkar's Joke Client Admin, 1.8.\n");
		System.out.println("Using server: " + serverName + ", Port: 1566");

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); // input from user.
		try {
			String adminResponse;

			do {
				System.out.print("If you want to change from joke to proverb or vice-versa press enter or press quit");
				System.out.flush();
				adminResponse = in.readLine();
				
				if (adminResponse.indexOf("quit") < 0) {
					
					if(adminResponse.equals("")) {		//changing mode from "Joke" to "Proverb" or vice versa
						if(type.equals("Joke")) {
							type = "Proverb";
						}
						
						else if(type.equals("Proverb")) {
							type = "Joke";
						}
					}
					
					changeStatus(adminResponse, serverName, type); // below 'changeStatus' function is called.
				}
			} while (adminResponse.indexOf("quit") < 0);
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

	static void changeStatus(String adminResponse, String serverName, String type) {

		Socket sock; // Socket variable created
		BufferedReader fromServer; // BufferedReader variable created
		PrintStream toServer; // PrintStream variable created
		String textFromServer;

		try {

			sock = new Socket(serverName, 1566);
			fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			toServer = new PrintStream(sock.getOutputStream());

			toServer.println("" + type);
			toServer.flush();			//sending type to server

			sock.close();
		} catch (IOException x) {
			System.out.println("Socket error.");
			x.printStackTrace();
		}
	}
}