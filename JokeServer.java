/*--------------------------------------------------------

1. Ashay Kargaonkar / Date: 09-27-2020

2. Precise command-line compilation examples / instructions:

> javac JokeServer.java


3. Precise examples / instructions to run this program:

	None

4. List of files needed for running the program.
	
	No files are needed to run the program. This file can run independently. But below are file names that will be required to test.

 1. JokeClient.java
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
import java.util.Random;

class Worker extends Thread { // create a new thread which initializes socket.
	Socket sock; // creating object of socket class

	Worker(Socket s) {// constructor of worker class
		sock = s; // initializing socket variable
	}

	public void run() {

		PrintStream out = null; // initialize output stream of socket to null
		BufferedReader in = null; // initialize input stream of socket to null
		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream())); // assigning input stream variable to
																					// socket input stream
			out = new PrintStream(sock.getOutputStream()); // assigning output stream variable to socket output stream

			try { // below code can throw exception so try block is used.
				String type; // creating object of String class.
				type = in.readLine(); // input from client side is stored in 'type' variable
				type = "Joke";			//pre-defined the type of "Joke". Unable to implement properly.

				printProbJoke(type, out); // below 'printProbJoke' function is called
			} catch (IOException x) {
				System.out.println("Server read error"); // if exception occurs, print.
				x.printStackTrace(); // print type of error
			}
			sock.close(); // closing the socket
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}

	static Random r = new Random();

	static void printProbJoke(String type, PrintStream out) {
		//initialising Jokes and Proverbs
		
		String JA = "Why did the chicken cross a mobius strip? To get to the same side.";
		String JB = "Proton, electron and neutron walked in a bar and had a drink. Neutron asked 'What's the charge?' Bartender replied: For you? None";
		String JC = "Q. Which is closer, Florida or the moon? A. The moon. You can’t see Florida from here.";
		String JD = "Two guys stole a calendar. They got six months each.";

		String PA = "Nothing can be too beautiful to be true if it's consistent with the laws of nature";
		String PB = "It's the possibility of having a dream come true that makes life interesting.";
		String PC = "There is only one thing that makes a dream impossible to achieve: the fear of failure.";
		String PD = "The simple things are also the most extraordinary things, and only the wise can see them.";

		String[] jokes = { JA, JB, JC, JD };
		String[] proverbs = { PA, PB, PC, PD };		//creating arrays of both Joke and Proverb

		if (type.equals("Joke")) {
			int num = r.nextInt(4);					//printing random Joke
			out.println(jokes[num]);
			out.println();
		}

		else if (type.equals("Proverb")) {
			int num = r.nextInt(4);
			out.println(proverbs[num]);				//printing random Proverb
			out.println();
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
}

//created admin thread

class adminWorker extends Thread {
	Socket sockAdmin; // creating object of socket class

	adminWorker(Socket s) {// constructor of worker class
		sockAdmin = s;

	}

	static String typeAdmin = null;

	public void run() {

		BufferedReader in;

		try {
			in = new BufferedReader(new InputStreamReader(sockAdmin.getInputStream()));

			String fromAdminResponse = in.readLine();

			if (fromAdminResponse.equals("Joke")) {			//changing mode to "Proverb"
				typeAdmin = "Proverb";
			}

			else if (fromAdminResponse.equals("Proverb")) {	//changing mode to "Joke"
				typeAdmin = "Joke";
			}

			System.out.println("mode changed to " + typeAdmin);

			sockAdmin.close();								//closing socket
		}

		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

public class JokeServer {

	public static void main(String a[]) throws IOException {
		int q_len = 6; // maximum number of simultaneous connections allowed
		int portClient = 1565; // this applications uses 1565 port number.
		int portAdmin = 1566; // this applications uses 1565 port number.
		Socket sock; // creating object of socket class
		Socket sockA;

		ServerSocket servsock = new ServerSocket(portClient, q_len); 	// creates server socket for this application. socket = 1565
		ServerSocket sockAdmin = new ServerSocket(portAdmin, q_len); 	// creates server socket for this application. socket = 1565

		System.out.println("Ashay Kargaonkar's Joke server listening at port 1565.\n");
		System.out.println("Ashay Kargaonkar's Joke server listening at port 1566.\n");

		while (true) { // this loops keeps server running
			sock = servsock.accept(); // accepting multiple user's connnection request
			sockA = sockAdmin.accept();
			
			new Worker(sock).start();
			new adminWorker(sockA).start();

		}
	}
}