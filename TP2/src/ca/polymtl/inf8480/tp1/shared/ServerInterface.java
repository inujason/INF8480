package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

// Interface des methodes qui sont invoque au serveur par le client.
public interface ServerInterface extends Remote {
	
	boolean isTasksAccepted(int u) throws RemoteException;
	int sendTasks(List<String> listOps) throws RemoteException;

}
