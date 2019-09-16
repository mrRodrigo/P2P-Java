import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {

	public Boolean Add(Peer peer) throws RemoteException;

	public void Remove(Peer peer) throws RemoteException;

	public void receiveHeartBeat(Peer peer) throws RemoteException;

	public boolean peerExist(String name) throws RemoteException;

	public Peer getClientWithFileHash(String hash, Peer client) throws RemoteException;
}
