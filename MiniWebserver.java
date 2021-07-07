
/* 
1.	MIME types tell the browser in which format data will be coming. The data can come in from of HTML, CSS etc. formats
	and this browser how can he handle that type of data

2.	By changing the value of "Content type" to 'text/html' we can return the contents of requested files of type HTML

3. By changing the value of "Content type" to 'text/pl	ain' we can return the contents of requested files of type HTML	

*/

import java.io.*; //Input Output libraries
import java.net.*; //Java networking libraries
import java.util.Scanner;

class ListenWorker extends Thread { // Class definition
	Socket sock; // Class member, socket, local to ListnWorker.

	ListenWorker(Socket s) {
		sock = s;
	}

// Constructor, assign arg s
	// to local sock
	public void run() {
		PrintStream out = null; // Input from the socket
		BufferedReader in = null; // Output to the socket

		String name = null; // created name variable which user will input
		int num1 = 0; // created num1 variable which user will input
		int num2 = 0; // created num2 variable which user will input
		String whole;
		int check = 1;
		try {
			out = new PrintStream(sock.getOutputStream()); // creating output stream variable
			in = new BufferedReader(new InputStreamReader(sock.getInputStream())); // creating input stream variable

			whole = in.readLine();

			if (whole.length() > 50) { // there are 2 packets and i am interested in the packet whose length is greater
										// than 50

				for (int i = 0; i < whole.length(); i++) {
					if (whole.charAt(i) == '&' || whole.charAt(i) == ' ') { // searching for a character because before
																			// it contains the name

						if (check == 2) { // check == 2 because first check is a 'blank' character and it doesn't
											// contain name before it
							name = whole.substring(28, i); // substring the name

						}

						else if (check == 3) {

							for (int j = i; j > 5; j--) { // for loop indicate we can use 4 digit number at max for sum
								if (whole.charAt(j) == '=') {
									num1 = Integer.parseInt(whole.substring(j + 1, i)); // converting the character to
																						// integer and saving it in num1
																						// variable
									break;

								}
							}
						}

						else if (check == 4) {

							for (int j = i; j > 5; j--) { // for loop indicate we can use 4 digit number at max for sum
								if (whole.charAt(j) == '=') {
									num2 = Integer.parseInt(whole.substring(j + 1, i)); // converting the character to
																						// integer and saving it in num2
																						// variable
									break;

								}
							}

						}

						check++;

					}
				}

				// creating a dynamic html

				String HTMLResponse = "<html> <h1> Name is  " + name + "</h1> <p><p> <hr> <p>"; // displaying name
				out.println("HTTP/1.1 200 OK");
				out.println("Connection: close");
				out.println("Content-Length: 1800"); // increased the lenght to 1800.
				out.println("Content-Type: text/html \r\n\r\n");
				out.println(HTMLResponse);
				out.println("sum is " + (num1 + num2)); // outputting the sum of the numbers

				File file = new File("WebAdd.html"); // given WebAdd.html file
				Scanner input = new Scanner(file);

				while (input.hasNextLine()) {
					String code = input.nextLine();

					out.println(code);
				}

				input.close();

				out.println("</html>");
			}

			sock.close(); // close this connection, but not the server;
		} catch (IOException x) {
			System.out.println("Error: Connetion reset. Listening again...");
		}
	}
}

public class MiniWebserver {

	static int i = 0;

	public static void main(String a[]) throws IOException {
		int q_len = 6; /* Number of requests for OpSys to queue */
		int port = 2540; // port no is 2540 of this server
		Socket sock;

		ServerSocket servsock = new ServerSocket(port, q_len);

		System.out.println("Ashay Kargaonkar's WebResponse running at 2540.");
		System.out.println("Point Firefox browser to WebAdd.html");
		while (true) {
			// wait for the next client connection:
			sock = servsock.accept();
			new ListenWorker(sock).start();
		}
	}
}