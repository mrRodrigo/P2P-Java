import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
	public Boolean Add(Peer peer) throws RemoteException;

	public void Remove(Peer peer) throws RemoteException;

	public void reciveHeartBeat(Peer peer) throws RemoteException;
}
