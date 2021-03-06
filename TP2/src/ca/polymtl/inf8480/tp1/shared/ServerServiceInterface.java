package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

// Interface des methodes qui sont invoque au serveur par le client.
public interface ServerServiceInterface extends Remote {
	
	HashMap<String, String> getListServers() throws RemoteException;
	
	void authenticateRepartiteur(/*Repartiteur repartiteur*/) throws RemoteException;

	void registerServer(String serverID, String hostname) throws RemoteException;
	
	void removeServer(String uuid) throws RemoteException;

}
