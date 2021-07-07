import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class publicKeyWorker extends Thread {			//publicKeyServer thread
	Socket keySock;

	publicKeyWorker(Socket s) {
		keySock = s;
	}

	public void run() {

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(keySock.getInputStream()));
			String data = in.readLine();
			System.out.println("data received by keyserver is " + data);	//havent implemented anything here so just print statement

			keySock.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class publicKeyServer implements Runnable {

	Random r = new Random();		//to get random number

	@Override
	public void run() {

			int q_len = 6;	//at a time 6 responses can be handled
			Socket keysock;
			System.out.println("keyServer starting up, listening at port " + Ports.KeyServerPortBase);

			System.out.println();

			try {
				ServerSocket keyServerSocket = new ServerSocket(Ports.KeyServerPortBase, q_len);

				KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");	//generating a key-value pair with RSA public-key cryptosystem
				SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN");
				rng.setSeed(999);
				keyGenerator.initialize(1024, rng);
				KeyPair keyPair = keyGenerator.generateKeyPair();
	
				System.out.println("public key of this process is " +keyPair.getPublic() + "and process ID is " +Ports.KeyServerPortBase % 4710); //printing the public key to the console

				keyServerSocket.close();

				while (true) {
					keysock = keyServerSocket.accept();
					new publicKeyWorker(keysock).start();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}

	}

	class blockchainWorker extends Thread {
		Socket blockChainSock;

		blockchainWorker(Socket s) {
			blockChainSock = s;
		}

		//havent implemented anything here, but here, i have to accept the verified blocks which will be received from unverifiedblockserver and input data to those blockrecords
		//by reading data from the Input file i.e. BlockInput0.txt, BlockInput1.txt, BlockInput2.txt
		//and then save the final blocks back to json format
		public void run() {

			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(blockChainSock.getInputStream()));
				String data = in.readLine();
				System.out.println("data received by blockchainserver is " + data);

				blockChainSock.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class blockchainServer implements Runnable {

		@Override
		public void run() {
			int q_len = 6;
			Socket keysock;
			System.out.println("blockchainServer starting up, listening at port " + Ports.BlockchainServerPortBase);

			System.out.println();

			try {
				ServerSocket blockChainServerSocket = new ServerSocket(Ports.BlockchainServerPortBase, q_len);
				while (true) {
					keysock = blockChainServerSocket.accept();
					new blockchainWorker(keysock).start();
				}

			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

	}

	class unverifiedBlockServer implements Runnable {

		BlockingQueue<BlockRecord> queue;	//priority queue is initialised

		unverifiedBlockServer(BlockingQueue<BlockRecord> queue) {
			this.queue = queue;
		}

		//comparator is used to compare the blockrecords using their timestamps

		public static Comparator<BlockRecord> BlockTSComparator = new Comparator<BlockRecord>() {
			public int compare(BlockRecord b1, BlockRecord b2) {
				String s1 = b1.setTimeStamp();
				String s2 = b2.setTimeStamp();
				if (s1 == s2) {
					return 0;
				}
				if (s1 == null) {
					return -1;
				}
				if (s2 == null) {
					return 1;
				}
				return s1.compareTo(s2);
			}
		};

		@Override
		public void run() {
			int q_len = 6;
			Socket keysock;
			System.out
					.println("unverifiedBlockServer starting up, listening at port " + Ports.UnverifiedBlockServerPortBase);

			System.out.println();

			try {
				ServerSocket unverifiedBlockServerSocket = new ServerSocket(Ports.UnverifiedBlockServerPortBase, q_len);
				while (true) {
					keysock = unverifiedBlockServerSocket.accept();
					new unverifiedBlockWorker(keysock).start();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

	}

	class unverifiedBlockWorker extends Thread {
		Socket unverifiedSock;		//socket for unverifiedBlockWorker is initiated
		BlockRecord BR = new BlockRecord();
		BlockingQueue<BlockRecord> queue;

		unverifiedBlockWorker(Socket s) {
			unverifiedSock = s;
		}

		public void run() {

			try {
				ObjectInputStream unverifiedIn = new ObjectInputStream(unverifiedSock.getInputStream());
				BR = (BlockRecord) unverifiedIn.readObject(); // Reading the UnVerifiedBlock as an object
				System.out.println("Received UVB: " + BR.setTimeStamp() + " " + BR.getData());

				queue.put(BR); // entering the blocks in priority queue so that blocks get arranged according to their timestamps

				if (BR != null)
					writeJSON(BR);		//writing the blockrecord in JSON format using writeJSON

				unverifiedSock.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void writeJSON(BlockRecord bR2) throws IOException {

			FileWriter writer = new FileWriter("BlockchainLedgerSample.json"); 	//creating a new file writer for "BlockchainLedgerSample.json" file
			Gson gson = new GsonBuilder().setPrettyPrinting().create();			//gson library is to convert the blocks in string format to JSon format and write it in file 

			// Convert the Java object to a JSON String:
			String json = gson.toJson(bR2);

			System.out.println("\nJSON String blockRecord is: " + json);

			gson.toJson(bR2, writer);
		}
	}

	class BlockRecord implements Serializable {		//this is the design of block record

		private static final long serialVersionUID = 1L;
		Integer blockID;	//blockID
		String previousHash;	//Hash of previous block
		String currentHash;		//hash of current block
		String data;			//data of that block		
		String TimeStamp;		//timestamp of that block
		UUID uuid;				//UUID of that block
		String RandomSeed;		//RandomSeed
		String puzzle;

		public Integer getBlockID() {
			return blockID;
		}

		public void setBlockID(String blockID) {
			this.data = blockID;
		}

		public String getPreviousHash() {
			return previousHash;
		}

		public String getCurrentHash() {
			return currentHash;
		}

		public String getData() {
			return data;
		}

		public void setData(String DATA) {
			this.data = DATA;
		}

		String setTimeStamp() {
			Date date = new Date();
			String TimeStampString = String.format("%1$s %2$tF.%2$tT", "", date);
			return TimeStampString;
		}

		public UUID getUUID() {
			return uuid;
		} // Later will show how JSON marshals as a string. Compare to BlockID.

		public void setUUID(UUID ud) {
			this.uuid = ud;
		}

	}

	class Ports {											//declaring the base ports value of the 3 servers.
		public static int KeyServerPortBase = 4710;
		public static int UnverifiedBlockServerPortBase = 4820;
		public static int BlockchainServerPortBase = 4930;

		public static int getKeyServerPort(int processID) {		//getting keyserver port of a specific process according to the processID
			return KeyServerPortBase + processID;
		}

		public static int getUnverifiedBlockServerPort(int processID) {		//getting UnverifiedBlockserver port of a specific process according to the processID
			return KeyServerPortBase + processID;
		}

		public static int blockChainServerPort(int processID) {		//getting blockChainServer port of a specific process according to the processID
			return KeyServerPortBase + processID;
		}

	}

	public class Blockchain {

		public static Comparator<BlockRecord> BlockTSComparator = new Comparator<BlockRecord>() {
			@Override
			public int compare(BlockRecord b1, BlockRecord b2) {
				// System.out.println("In comparator");
				String s1 = b1.setTimeStamp();
				String s2 = b2.setTimeStamp();
				if (s1 == s2) {
					return 0;
				}
				if (s1 == null) {
					return -1;
				}
				if (s2 == null) {
					return 1;
				}
				return s1.compareTo(s2);
			}
		};

		static PriorityBlockingQueue<BlockRecord> queue = new PriorityBlockingQueue<>(100, BlockTSComparator);

		public static void main(String[] args) throws Exception {
			int pnum = -1;

			for (String s : args) {		//getting input from console

				pnum = Integer.parseInt(s);
				System.out.println(pnum + " process is running");
				if (pnum != -1) {
					Ports.KeyServerPortBase = Ports.KeyServerPortBase + pnum;		//initialising port number of keyserver using process number
					Ports.BlockchainServerPortBase = Ports.BlockchainServerPortBase + pnum;		//initialising port number of BlockchainServer using process number
					Ports.UnverifiedBlockServerPortBase = Ports.UnverifiedBlockServerPortBase + pnum;		//initialising port number of UnverifiedBlockServer using process number
					new Thread(new publicKeyServer()).start();		//starting the publicKeyServer thread
					new Thread(new blockchainServer()).start();		//starting the blockchainServer thread
					new Thread(new unverifiedBlockServer(queue)).start();		//starting the unverifiedBlockServer thread
					try {
						Thread.sleep(1000);		//making thread sleep to settle all process after they start
					} catch (Exception e) {
					}
				}

				unVerifiedSend(); 	//function creating and submitting unverfied blocks

				readJSON();			//read file which is in JSON format

			}

		}

		public static boolean verifySig(byte[] data, PublicKey key, byte[] sig) throws Exception {
			Signature signer = Signature.getInstance("SHA1withRSA");
			signer.initVerify(key);
			signer.update(data);

			return (signer.verify(sig));
		}

		public static byte[] signData(byte[] data, PrivateKey key) throws Exception {
			Signature signer = Signature.getInstance("SHA1withRSA");
			signer.initSign(key);
			signer.update(data);
			return (signer.sign());
		}

		private static void unVerifiedSend() {

			Socket UVBsock; 	//creaing a socket for unverifiedblockserver
			Random r = new Random();	//getting a random value
			BlockRecord tempRec;	//tempeoroy blockrecord object
			ArrayList<BlockRecord> blocks = new ArrayList<BlockRecord>();	//initialising arraylist of blockrecords

			for (int i = 0; i < 4; i++) {		//creating 4 blockrecords and entering some data in it. This can also be done by reading the json file after verifying them.
				BlockRecord br = new BlockRecord();
				br.blockID = i;				//blockID
				br.RandomSeed = "0000";		//inputting randomseed
				br.data = "data " + i;		//inputting random data	

				if (blocks.isEmpty() == true) {			//if its the first block
					br.previousHash = "first hash ever";
				}

				else {
					br.previousHash = blocks.get(i - 1).currentHash.toString();		//setting the value of previous has value to the currenthash value of the previous node.
				}

				br.puzzle = sha256(br.RandomSeed + br.data + br.previousHash);		//this is puzzle we need to solve
				br.TimeStamp = setTimeStamp();		//setting timestamp of very block
				br.uuid = setUUID();				//setting uuid of very block
				blocks.add(br);						//adding blockrecord to the arraylist

				System.out.println("block created with data: " + blocks.get(i).data); //displaying on console that blocks are created
			}

			Collections.shuffle(blocks); //shuffling the arraylist so that later we can rearrange them according to the increasing timestamp by sending then to 
										// unverified block server
			Iterator<BlockRecord> iterator = blocks.iterator();		//iterator for arraylist of blocks

			ObjectOutputStream toServerOOS;		//ObjectOutputStream created to send the blockrecord as a whole

			while (iterator.hasNext()) {		//going through every block record using this while loop
				try {
					UVBsock = new Socket("localhost", Ports.UnverifiedBlockServerPortBase);
					toServerOOS = new ObjectOutputStream(UVBsock.getOutputStream()); //sending individual blockrecord to the Unverified block server
					Thread.sleep((r.nextInt(9) * 100)); // Sleep up to a second to randominze when sent.
					tempRec = iterator.next();
					// System.out.println("UVB TempRec for P" + i + ": " + tempRec.getTimeStamp() +
					// " " + tempRec.getData());
					toServerOOS.writeObject(tempRec); // Send the unverified block record object
					toServerOOS.flush();
					UVBsock.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		private static void readJSON() {		//this function is used to read the block records from JSON files

			System.out.println("\n=========> In ReadJSON <=========\n");

			Gson gson = new Gson();

			try (Reader reader = new FileReader("blockRecord.json")) { 		//creating new filereader for reading json files

				BlockRecord blockRecordIn = gson.fromJson(reader, BlockRecord.class);	//reading blockrecord from json file	

				// Print the blockRecord:
				System.out.println(blockRecordIn);
				System.out.println("Name is: " + blockRecordIn.blockID.toString());

				String INuid = blockRecordIn.uuid.toString();
				System.out.println("String UUID: " + blockRecordIn.blockID + " Stored-binaryUUID: " + INuid);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private static String setTimeStamp() {		//setTtimeStamp function for block records
			Date date = new Date();
			String TimeStampString = String.format("%1$s %2$tF.%2$tT", "", date);
			return TimeStampString;
		}

		private static UUID setUUID() {			//setting UUID of blockRecord
			UUID BinaryUUID = UUID.randomUUID();
			return BinaryUUID;

		}

		private static Boolean realWork() {		//here we have to hash the 3 parameters and creating some kind of work and if it's successful return true. Ididnt implement it
			return false;
		}

		public static String sha256(String base) {		//get a sha256 string format of any string
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				byte[] hash = digest.digest(base.getBytes("UTF-8"));
				StringBuffer hexString = new StringBuffer();

				for (int i = 0; i < hash.length; i++) {
					String hex = Integer.toHexString(0xff & hash[i]);
					if (hex.length() == 1)
						hexString.append('0');
					hexString.append(hex);
				}

				return hexString.toString();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}
