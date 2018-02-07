package ca.polymtl.inf8480.tp1.client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.lang.Math;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;

public class Client {
	
	//FakeServer localServer = null; // Pour tester la latence d'un appel de
									// fonction normal.
									
	private ServerInterface localServerStub = null;
	
	private ServerInterface distantServerStub = null;
	
	public static String command = null;
	public static String filename = null;
	
	public static void main(String[] args) {
		
		String distantHostname = null;
		
		
		if (args.length > 0) {
			
			//Adresse du serveur
			//distantHostname = args[0];
			command = args[0];
			if (args.length == 2)
				filename = args[1];			
		}
		
	
		Client client = new Client("132.207.12.104");
		client.run();
	}

	public Client(String distantServerHostname) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		//localServer = new FakeServer();
		
		localServerStub = loadServerStub("127.0.0.1");

		//if (distantServerHostname != null) {
			//distantServerStub = loadServerStub(distantServerHostname);
		//}
	}

	private void run() {
		
		
		if (/*distantServerStub != null && */command != null)
		{
			switch(command)
			{
				case "list":
					list();
					break;
				case "get":
					break;
				case "create":
					create();
					break;
				case "lock":
					break;
				case "push":
					break;
				default:
					System.out.println("Commande non reconnue");
			}
		}

	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("server");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		return stub;
	}


	private void get()
	{
		
	}
	private void list()
	{
		try
		{
			String[] result = localServerStub.list();
			for (int i = 0; i < result.length; i++)
			{
				 System.out.println(result[i]);
			}
		}
		catch (RemoteException e)
		{
			System.out.println("Erreur" + e.getMessage());
		}
	}
	
	private void create()
	{
		try
		{
			boolean result = localServerStub.create(filename);
			System.out.println(result);
		}
		catch (RemoteException e)
		{
			System.out.println("Erreur" + e.getMessage());
		}
	}
	private void lock()
	{
		
	}
	private void push()
	{
		
	}
	
}
