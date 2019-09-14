import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;

import java.util.Timer;
import java.util.TimerTask;

public class AppClient {
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

			String text = "";

			Scanner scan = new Scanner(System.in);

			do {

				text = scan.nextLine();

			} while (!text.equalsIgnoreCase("exit"));

			server.Remove(mainPeer);
			System.exit(0);

		} catch (Exception e) {
			System.out.println("Client failed: ");
			e.printStackTrace();
		}

	}

	public static void sendHeartBeat(ServerInterface server, Peer thisClient) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				try {
					server.reciveHeartBeat(thisClient);
					System.out.print(".");

				} catch (Exception e) {
					System.out.print("HeartBeat falhou ao ser enviado");
					e.printStackTrace();
				}
			}
		}, 0, 1000);
	}
}
