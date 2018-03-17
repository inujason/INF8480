package ca.polymtl.inf8480.tp1.repartiteur;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Remote;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;
import ca.polymtl.inf8480.tp1.shared.ServerServiceInterface;

public class Repartiteur {
	
	//FakeServer localServer = null; // Pour tester la latence d'un appel de
									// fonction normal.
									
	private ServerInterface localServerStub = null;
	private ServerInterface distantServerStub = null;
	
	private ServerServiceInterface serverServiceRepertoireStub = null;
	
	public static String filename = "";
	
	public static HashMap<String, ServerInterface> listServerStubs = new HashMap<String,ServerInterface>();
	
	public static List<String> listTasks = new ArrayList<String>();
	
	
	public static Integer indexCurrentTask = 0;
	public static Integer sum = 0;
	public static Boolean isFinished = false;
	
	public static List<Integer> sums = new ArrayList<Integer>();
	
	
	public static void main(String[] args) {
		
		String distantHostname = null;
		
		
		if (args.length > 0) {
			//Adresse du serveur
			//distantHostname = args[0];
			filename = args[0];		
		}
		
		Repartiteur repartiteur = new Repartiteur();
		
		//repartiteur.connectToNameService("132.207.12.104");
		repartiteur.connectToNameService("127.0.0.1");
		repartiteur.run();
		
		
		
	}
	
	public Repartiteur()
	{
		super();
	}
	
	// On connecte le Repartiteur au serveur de service de noms distant avec adresse IP fixe.
	public void connectToNameService(String distantServerServiceHostname) {
		

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		if (distantServerServiceHostname != null) {
			serverServiceRepertoireStub = loadServerServiceStub(distantServerServiceHostname);
		}
	}

	private void run() 
	{	
		System.out.println("RUNNING");
		
		
		connectToServers();
		
		
		sendTasks();
		
		

	}

	// Tentative de creer le stub des objets du serveur a l'aide du registre
	private ServerServiceInterface loadServerServiceStub(String hostname) {
		
		ServerServiceInterface stub = null;
	

		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerServiceInterface) registry.lookup("service");
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
	
	// Tentative de creer le stub des objets du serveur a l'aide du registre
	private ServerInterface loadServerStub(String id, String hostname) {
		
		ServerInterface stub = null;
	
		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup(id);
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



	private void sendTasks()
	{
		
		
		
		readFileTask();
		
		
		
		Iterator it = listServerStubs.entrySet().iterator();
		while (it.hasNext())
		{
			ServerInterface server = (ServerInterface)((Map.Entry) it.next()).getValue();
			ServerThread t = new ServerThread(server);
			t.start();
		}
		// TODO : Changement de bool de mode de securite pour les serveurs selon la commande du user!
		
		
		// TODO : THREADS(Donc avec var globales)
		// creer un thread pour chaque serveur
		// chaque thread permet de envoyer des tachers et de recevoir le resultat
		// lors de reception de resultat, recommencer tant que la list des taches nest pas fini.
		
		
		while (!isFinished)
		{
			System.out.println(isFinished);
		}
		System.out.println(isFinished);
		System.out.println("FINAL SUMM : " + sum);
		
	}

	private void readFileTask()
	{
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader(filename));
			for (String taskLine; (taskLine = br.readLine()) != null;)
			{
				listTasks.add(taskLine);
			}
		}
		catch (Exception e)
		{ System.out.println(e.getMessage());}
	}
	
	private void connectToServers()
	{
		try
		{					
			HashMap<String, String> results = serverServiceRepertoireStub.getListServers();
			System.out.println(results);
			
			Iterator it = results.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry server = (Map.Entry) it.next();
				String id = (String) server.getKey();
				String ip = (String) server.getValue();
				ServerInterface stub = loadServerStub(id, ip);
				listServerStubs.put(id, stub);
			}
		}
		catch (Exception e) {e.getMessage();}
		
		System.out.println(listServerStubs.size());
		
	}
	
	private boolean isTasksAccepted(ServerInterface server, int nbOfTasks)
	{
		
		boolean result = false;
		try
		{					
			result =  server.isTasksAccepted(nbOfTasks);
			//int result =  distantServerStub.isTasksAcceptedvoid();
			
		}
		catch (Exception e) {e.getMessage();}		
		return result;
	}
	
	private int sendTasksToServer(ServerInterface server, List<String> listOps)
	{
		
		int result = 0;
		try
		{					
			// TODO: PANNE A GERER
			result =  server.sendTasks(listOps);
			//int result =  distantServerStub.isTasksAcceptedvoid();
		}
		catch (Exception e) {e.getMessage();}		
		
		return result;
	}

	// TODO: Authentification du user pour le serveur a laide du service
	
	
	// ORDRE DIMPORTAANCE DES TACHES
	// THREADS, PANNES, MALICE(Secu ou non-secu),Authentification
	
	
	public class ServerThread extends Thread {
	
	ServerInterface currentServerStub;
	
	public ServerThread(ServerInterface server)
	{
		currentServerStub = server;
	}
	public void run()
	{
		while (!isFinished)
		{
						
			int nbOpsForServer = 10;
			
			int nbTasksLeft = 0;
			//synchronized ((Object) indexCurrentTask) {
			nbTasksLeft = (listTasks.size()) - indexCurrentTask;
			//}
			if (nbTasksLeft < 10)
				nbOpsForServer = nbTasksLeft;			
			
			while (!isTasksAccepted(currentServerStub, nbOpsForServer))
			{
				nbOpsForServer--;
			}
			
			List<String> currentTasks = new ArrayList<String>();
			for ( int i = 0; i < nbOpsForServer; i++)
			{
				currentTasks.add(listTasks.get(indexCurrentTask + i));
				
			}
			//synchronized (indexCurrentTask) {
				indexCurrentTask += nbOpsForServer;
			//}
			//synchronized (sum) {
				sum += sendTasksToServer(currentServerStub, currentTasks);
			//}
			
			System.out.println("Nb tasks: " + nbOpsForServer +" "+sum);
			
			//synchronized (isFinished) {
				if (indexCurrentTask == listTasks.size())
				{
					isFinished = true;
				}
			//}
		}
	}
}
	
}



