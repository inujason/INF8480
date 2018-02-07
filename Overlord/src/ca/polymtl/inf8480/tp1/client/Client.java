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
	
	public static int[] tabParam;
	
	public static void main(String[] args) {
		String distantHostname = null;
		
		int x = 0;
		
		

		if (args.length > 0) {
			
			x = Integer.parseInt(args[1]);
			
			distantHostname = args[0];
			
			
		}

		tabParam= new int[((int)Math.pow(10,x))/4];
		 
		
		

		Client client = new Client(distantHostname);
		client.run();
	}

	public Client(String distantServerHostname) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		localServer = new FakeServer();
		localServerStub = loadServerStub("127.0.0.1");

		if (distantServerHostname != null) {
			distantServerStub = loadServerStub(distantServerHostname);
		}
	}

	private void run() {
		appelNormal();

		if (localServerStub != null) {
			appelRMILocal();
		}

		if (distantServerStub != null) {
			appelRMIDistant();
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



	private void list()
	{
		
	}
	private void list()
	{
		
	}
	private void list()
	{
		
	}
	private void list()
	{
		
	}
	

	private void appelNormal() {
		long start = System.nanoTime();
		//int result = localServer.execute(4, 7);
		int result = localServer.exec(tabParam);
		long end = System.nanoTime();

		System.out.println("Temps écoulé appel normal: " + (end - start)
				+ " ns");
		System.out.println("Résultat appel normal: " + result);
		
		//System.out.println((end - start));
		
	}

	private void appelRMILocal() {
		try {
			long start = System.nanoTime();
			//int result = localServerStub.execute(4, 7);
			int result = localServerStub.exec(tabParam);
			long end = System.nanoTime();

			System.out.println("Temps écoulé appel RMI local: " + (end - start)
					+ " ns");
			System.out.println("Résultat appel RMI local: " + result);
			
			//System.out.println((end - start));
			
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
	}

	private void appelRMIDistant() {
		try {
			long start = System.nanoTime();
			//int result = distantServerStub.execute(4, 7);
			int result = distantServerStub.exec(tabParam);
			long end = System.nanoTime();

			System.out.println("Temps écoulé appel RMI distant: "
					+ (end - start) + " ns");
			System.out.println("Résultat appel RMI distant: " + result);*/
			
			
			//System.out.println((end - start));
			
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}
	}
}
