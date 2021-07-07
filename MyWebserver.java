
/* 
1.	MIME types tell the browser in which format data will be coming. The data can come in from of HTML, CSS etc. formats
	and this browser how can he handle that type of data

2.	By changing the value of "Content type" to 'text/html' we can return the contents of requested files of type HTML

3. By changing the value of "Content type" to 'text/pl	ain' we can return the contents of requested files of type HTML	

*/

import java.io.*; //Input Output libraries
import java.net.*; //Java networking libraries

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

		String whole;
		File f1;
		File[] strFilesDirs = null;
		try {
			out = new PrintStream(sock.getOutputStream()); // creating output stream variable
			in = new BufferedReader(new InputStreamReader(sock.getInputStream())); // creating input stream variable

			whole = in.readLine();

			if(whole.length() != 0) {
			
			f1 = new File("D:\\College\\5th Quarter\\CSC 435\\Eclipse Files\\Mywebserver\\src");
			
			strFilesDirs = f1.listFiles();

			/*for (int i = 0; i < strFilesDirs.length; i++) {		//checking the list of filenames, this code was given by prof.
				if (strFilesDirs[i].isDirectory())
					System.out.println("Directory: " + strFilesDirs[i]);
				else if (strFilesDirs[i].isFile())
					System.out.println("File: " + strFilesDirs[i]);
			}*/
			}
			
			
				// creating a dynamic html

				
				out.println("HTTP/1.1 200 OK");
				out.println("Connection: close");
				out.println("Content-Length: 1800"); // increased the lenght to 1800.
				out.println("Content-Type: text/html \r\n\r\n");
				
				
				for(int i = 0; i < strFilesDirs.length; i++) {
					
					//<a href="/Ashay/435/">Parent Directory</a> <br>
					//<a href="dog.txt">dog.txt</a> <br>
					
					out.println("<a href = \"" + strFilesDirs[i].getName() + "\">" + editFileName(strFilesDirs[i]) + "</a> <br>"); //surrounding filenames for dynamic HTML page display
					editFileName(strFilesDirs[i]);
				}
				
				out.println("</html>");

			sock.close(); // close this connection, but not the server;
		} catch (IOException x) {
			System.out.println("Error: Connetion reset. Listening again...");
		}
	}
	
	public static String editFileName(File strFilesDirs) {
		
		//String name;
		//System.out.println();
		
		
		
		
		return strFilesDirs.getName() + "";
	}
}




public class MyWebserver {

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