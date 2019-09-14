import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.net.*;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class AppClient {
	public static final String CHECK_IF_EXIST_USER = "exist";
	public static final String GET_USER_WITH_HASH = "getFileHash";

	public static void main(String[] args) {
		int result;

		if (args.length <= 2) {
			System.out.println("Usage: java Client <machine>");
			System.exit(1);
		}

		String remoteHostName = args[0];
		String connectLocation = "//" + remoteHostName + "/P2P";

		ServerInterface server = null;
		try {
			System.out.println("Connecting to client at : " + connectLocation);
			server = (ServerInterface) Naming.lookup(connectLocation);

			Peer mainPeer = new Peer(args);

			if (!server.Add(mainPeer)) {
				System.out.println("Nome já existe");
				System.exit(1);
			}

			sendHeartBeat(server, mainPeer);

			new Thread(openSocketServer).start();

			String text = "";

			Scanner scan = new Scanner(System.in);

			do {

				text = scan.nextLine();
				commandController(text, server, mainPeer);

			} while (!text.equalsIgnoreCase("exit"));

			server.Remove(mainPeer);
			System.exit(0);

		} catch (Exception e) {
			System.out.println("Client failed: ");
			e.printStackTrace();
		}

	}

	public static void commandController(String command, ServerInterface server, Peer thisPeer)
			throws RemoteException, IOException {
		String[] commands = command.toLowerCase().split(" ");

		switch (commands[0]) {

		case "test":
			thisPeer.requestFile("pathFile", thisPeer);
			break;

		case GET_USER_WITH_HASH:
			System.out.println(server.getClientWithFileHash(commands[1], thisPeer));
			break;

		case CHECK_IF_EXIST_USER:
			System.out.println(server.peerExist(commands[1]));
			break;
		}
	}

	public static void sendHeartBeat(ServerInterface server, Peer thisClient) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				try {
					server.reciveHeartBeat(thisClient);
					// System.out.print(".");

				} catch (Exception e) {
					System.out.print("HeartBeat falhou ao ser enviado");
					e.printStackTrace();
				}
			}
		}, 0, 1000);
	}

	private static Runnable openSocketServer = new Runnable() {
		public void run() {

			try {
				ServerSocket server = null;

				server = new ServerSocket(4444);
				System.out.println("Socket Server created ");

				Socket client = server.accept();

				DataInputStream dIn = new DataInputStream(client.getInputStream());

				boolean done = false;
				while (!done) {
					byte messageType = dIn.readByte();

					switch (messageType) {
					case 1: // Type A
						System.out.println("Message A: " + dIn.readUTF());
						break;
					case 2: // Type B
						System.out.println("Message B: " + dIn.readUTF());
						break;
					case 3: // Type C
						System.out.println("Message C [1]: " + dIn.readUTF());
						System.out.println("Message C [2]: " + dIn.readUTF());
						break;
					default:
						done = true;
					}
				}

				dIn.close();
			} catch (IOException e) {
				System.out.println("Erro ao criar socket server");
				e.printStackTrace();
			}

		};
	};
}
