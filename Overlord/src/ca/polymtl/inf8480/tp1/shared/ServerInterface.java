package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.HashMap;

// Interface des methodes qui sont invoque au serveur par le client.
public interface ServerInterface extends Remote {
	HashMap<String, String> list() throws RemoteException;
	HashMap<String, String> get(String filename, String checksum) throws RemoteException;
	String create(String filename) throws RemoteException;
	String lock(String ID, String filename, String checksum) throws RemoteException;
	String push(String ID, String filename, String contenu) throws RemoteException;
	String CreateClientID() throws RemoteException;
	HashMap<String, String> syncLocalDirectory() throws RemoteException;
}
