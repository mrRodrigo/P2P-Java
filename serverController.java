import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class serverController extends UnicastRemoteObject implements ServerInterface {
	private static final long serialVersionUID = 1L;
	public ArrayList<Peer> clients;
	public HashMap<String, Integer> clientsHeartBeats;

	public serverController() throws RemoteException {
		clients = new ArrayList<Peer>();
		clientsHeartBeats = new HashMap<String, Integer>();
		incrementTimeOut();

	}

	// Incrementa timeout para cada cliente
	private void incrementTimeOut() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {

				try {
					for (String userName : clientsHeartBeats.keySet()) {
						clientsHeartBeats.replace(userName, clientsHeartBeats.get(userName) + 1);

						if (clientsHeartBeats.get(userName) >= 5) {
							Remove(userName, true);
							break;
						}
					}
					System.out.println(clientsHeartBeats);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 0, 1000);
	}

	public Boolean Add(Peer peer) throws RemoteException {

		if (peerExist(peer.getName())) {
			return false;
		}
		// Add no array de clientes
		clients.add(peer);

		// Inicializa contador de timeout para esse cliente
		clientsHeartBeats.put(peer.getName(), 0);

		System.out.println("Client [" + peer.getName() + "] connected with files: " + peer.getFiles());

		return true;
	}

	public void Remove(Peer peer) {
		clients.remove(peer);
		clientsHeartBeats.remove(peer.getName());
		System.out.println("Peer [" + peer.getName() + "] removed");
	}

	public void Remove(String peer, Boolean timeout) {
		Integer removeIndex = null;
		for (Peer peerObj : clients) {
			if (peerObj.getName().equals(peer)) {
				removeIndex = clients.indexOf(peerObj);
			}
		}

		if (removeIndex != null) {
			clients.remove(removeIndex);
			clientsHeartBeats.remove(peer);
			if (timeout) {
				System.out.println("Peer [" + peer + "] removed for timeout");
				return;
			}
			System.out.println("Peer [" + peer + "] removed");
		} else {
			System.out.println("Couldn't remove peer [" + peer + "]");
		}
	}

	public void receiveHeartBeat(Peer peer) throws RemoteException {
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getName().equals(peer.getName())) {
				String clientName = clients.get(i).getName();
				clientsHeartBeats.replace(clientName, 0);
			}
		}
		// System.out.println("Heartbeat from [" + peer.getName() + "]");
	}

	public boolean peerExist(String name) {
		for (int i = 0; i < clients.size(); i++)
			if (clients.get(i).getName().equals(name))
				return true;
		return false;
	}

	public Peer getClientWithFileHash(String hash, Peer client) {
		System.out.println(client.getName() + " need file " + hash);
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).haveFileByHash(hash)) {

				Peer clientHostFile = clients.get(i);
				System.out.println(clientHostFile.getName() + " have file: " + hash);

				return clientHostFile;
			}
		}

		return null;
	}
}
