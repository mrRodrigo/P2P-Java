import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Timer;
import java.util.TimerTask;

public class AppServer {
	public static void main(String[] args) throws RemoteException {
		if (args.length != 1) {
			System.out.println("Usage: java Server <machine>");
			System.exit(1);
		}

		try {
			System.setProperty("java.rmi.server.hostname", args[0]);
			LocateRegistry.createRegistry(1099);
			System.out.println("java RMI registry created.");

		} catch (RemoteException e) {
			System.out.println("java RMI registry already exists.");
		}

		try {
			Naming.rebind("P2P", new serverController());
			System.out.println("Addition Server is ready.");
		} catch (Exception e) {
			System.out.println("Addition Serverfailed: " + e);
		}
	}

}
