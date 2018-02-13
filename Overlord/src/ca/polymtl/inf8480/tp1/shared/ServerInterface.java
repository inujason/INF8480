package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.HashMap;

public interface ServerInterface extends Remote {
	HashMap<String, String> list() throws RemoteException;
	int get(int[] tab) throws RemoteException;
	boolean create(String filename) throws RemoteException;
	String lock(String ID, String filename, String checksum) throws RemoteException;
	int push(int[] tab) throws RemoteException;
	int CreateClientID(int[] tab) throws RemoteException;
}
