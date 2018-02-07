package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
	String[] list() throws RemoteException;
	int get(int[] tab) throws RemoteException;
	boolean create(String filename) throws RemoteException;
	int lock(int[] tab) throws RemoteException;
	int push(int[] tab) throws RemoteException;
}
