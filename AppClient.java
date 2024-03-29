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
	public static final String GET_RESOURCES_LIST = "getResources";
	public static final String GET_FILE_FROM_USER = "getFile";

	public static void main(String[] args) {

		// valida quantidade de argumentos ao rodar o Client
		if (args.length <= 3) {
			System.out.println("Usage: java Client <machine> <client_name> <machine> <files_dir>");
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
				/*
				 * le comando do usuario: -- exist <client_name> -- getFileHash <file_hash> --
				 * getResources <file_1> <file_2> <...> -- getFile <file>
				 */
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
		String[] commands = command.split(" ");

		switch (commands[0]) {

		// solicitar recurso especifico
		// getFile dc6444a370d16433b772d4b7860b110
		case GET_FILE_FROM_USER:
			Peer peerWithFile = server.getClientWithFileHash(commands[1], thisPeer);
			thisPeer.requestFile(commands[1], peerWithFile);
			break;

		// saber quem tem recurso especifico
		// getFileHash dc6444a370d16433b772d4b7860b110
		case GET_USER_WITH_HASH:
			System.out.println("Peer que possui arquivo: " + server.getClientWithFileHash(commands[1], thisPeer).getName());
			break;

		// solicitar lista de recursos
		// getResources
		case GET_RESOURCES_LIST:

			System.out.println("Peer que possui arquivo: " + server.getAllFileHash());

			break;

		// verificar se usuario existe
		// exist <client_name>
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
					server.receiveHeartBeat(thisClient);
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

				boolean done = false;

				// para toda mensagem que este peer receber via socket
				while (!done) {
					Socket client = server.accept(); // quem envio a mensagem
					DataInputStream dIn = new DataInputStream(client.getInputStream());
					byte messageType = dIn.readByte(); // lê o conteúdo da mensagem

					switch (messageType) {
					case 1: // se o conteudo é apenas o byte que representa 1, Recebeu uma solicitação de
									// arquivo
						String fileToSend = dIn.readUTF();
						System.out.println("Arquivo solicitado: " + fileToSend);

						// cria objeto resposta com o cliente solicitante
						try {

							// aqui funciona tudo maravilhosamente bem
							Socket peerServer = new Socket(client.getInetAddress(), 4444);

							DataOutputStream dOut = new DataOutputStream(peerServer.getOutputStream());

							FileInputStream fis = new FileInputStream(fileToSend);
							byte[] buffer = new byte[4096];
							int count;
							while ((count = fis.read(buffer)) > 0) {
								dOut.write(buffer, 0, count);
							}

							fis.close();
							dOut.close();

						} catch (Exception e) {
							System.out.println("Erro ao enviar arquivo ao solicitante");
							e.printStackTrace();
						}
						break;
					default: // se não é 1, então esta recebendo um arquivo
						System.out.println("arquivo recebido");

						DataInputStream dis = new DataInputStream(client.getInputStream());
						FileOutputStream fos = new FileOutputStream("recebido.txt");
						byte[] buffer = new byte[4096];

						int read = 0;
						int totalRead = 0;
						while ((read = dis.read(buffer)) > 0) {
							totalRead += read;
							System.out.println("Read " + totalRead + " bytes.");
							fos.write(buffer, 0, read);
						}

						fos.close();
						dis.close();
						break;
					}
					client.close();
					dIn.close();
				}

			} catch (IOException e) {
				System.out.println("Erro ao criar socket server");
				e.printStackTrace();
			}
		};
	};
}
