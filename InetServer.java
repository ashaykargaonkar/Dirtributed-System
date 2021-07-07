import java.io.*; // importing input-output package
import java.net.*; // importing networking package

class Worker extends Thread { // create a new thread which initializes socket.
	Socket sock; // creating object of socket class

	Worker(Socket s) { // constructor of worker class
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
				String name; // creating object of String class.
				name = in.readLine(); // input from client side is stored in 'name' variable
				System.out.println("Looking up " + name); // printing name
				printRemoteAddress(name, out); // below 'printRemoteAddress' function is called
			} catch (IOException x) {
				System.out.println("Server read error"); // if exception occurs, print.
				x.printStackTrace(); // print type of error
			}
			sock.close(); // closing the socket
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}

	static void printRemoteAddress(String name, PrintStream out) {
		try {
			out.println("Looking up " + name + "...");
			InetAddress machine = InetAddress.getByName(name); // by getting ip address of the 'name' variable; it is
																// stored in 'machine' variable
			out.println("Host name : " + machine.getHostName()); // printing host name
			out.println("Host IP : " + toText(machine.getAddress())); // printing Ip address
		} catch (UnknownHostException ex) {
			out.println("Failed in attempt to look up " + name);
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

public class InetServer {

	public static void main(String a[]) throws IOException {
		int q_len = 6; // maximum number of simultaneous connections allowed
		int port = 1565; // this applications uses 1565 port number.
		Socket sock; // creating object of socket class

		ServerSocket servsock = new ServerSocket(port, q_len); // creates server socket for this application.

		System.out.println("Clark Elliott's Inet server 1.8 starting up, listening at port 1565.\n");
		while (true) { // this loops keeps server running
			sock = servsock.accept(); // accepting multiple user's connnection request
			new Worker(sock).start(); // starting worker thread
		}
	}
}