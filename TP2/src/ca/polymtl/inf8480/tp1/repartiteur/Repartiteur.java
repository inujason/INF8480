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
	
	private static ServerServiceInterface serverServiceRepertoireStub = null;
	
	public static String filename = "";
	
	public static HashMap<String, ServerInterface> listServerStubs = new HashMap<String,ServerInterface>();
	
	public static List<String> listTasks = new ArrayList<String>();
	
	HashMap<String, String> listIDIP = new HashMap<String, String>();
	
	
	public static Integer indexCurrentTask = 0;
	public static Integer sum = 0;
	public static boolean isFinished = false;
	
	public static List<Integer> sums = new ArrayList<Integer>();
	
	public static boolean isSecure = false;
	
	public static void main(String[] args) {
		
		checkServerState();
		
		String distantHostname = null;
		
		if (args.length > 0) {
			//Adresse du serveur
			//distantHostname = args[0];
			filename = args[0];		
			if (args.length == 2 && args[1].equals("-s"))
			{
				isSecure = true;
				System.out.println("Tasks are handled in a SECURE environment");
			}
			else
			{
				isSecure = false;
				System.out.println("Tasks are handled in a INSECURE environment");
			}
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
		
		checkServerState();
		
		System.out.println("RUNNING");
		
		connectToServers();
		
		changeServersBehaviour();
		
		//try{wait(1000000);}catch(Exception e){}
		
		// Function temporaire sans parralelisme
		sendTasksSequentially();
		// Function avec parralelisme avec threads
		//sendTasks();
		
		

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


	private void changeServersBehaviour()
	{
		Iterator it = listServerStubs.entrySet().iterator();
		while (it.hasNext())
		{
			ServerInterface currentServer = (ServerInterface)((Map.Entry) it.next()).getValue();
			try
			{	
				// PANNE?
				System.out.println("isSecure: " + isSecure);
				currentServer.changeBehaviours(isSecure);
			}
			catch (Exception e) {e.getMessage();}	
		}
		
	}

	private void sendTasksSequentially()
	{
		readFileTask();
		
		
				
		if (!isSecure && listServerStubs.size() >= 2)
		{
			sendInsecurely();
		}
		else
		{
			
			sendSecurely();
		}
	}

	private void sendInsecurely()
	{
		checkServerState();
		
		Iterator it = listServerStubs.entrySet().iterator();
		while (it.hasNext() && (indexCurrentTask != listTasks.size()))
		{
			
			checkServerState();
			
			if(!it.hasNext())
			{
				break;
			}

			Map.Entry map1 = (Map.Entry) it.next();
			ServerInterface firstServer = (ServerInterface) map1.getValue();
			String id1 = map1.getKey().toString();
						
			if(!it.hasNext())
			{
				break;
			}
			
			Map.Entry map2 = (Map.Entry) it.next();
			ServerInterface secondServer = (ServerInterface) map2.getValue();
			String id2 = map2.getKey().toString();
			
			int nbOpsForServer = 10;
			
			int nbTasksLeft = 0;
			nbTasksLeft = (listTasks.size()) - indexCurrentTask;

			if (nbTasksLeft < 10)
				nbOpsForServer = nbTasksLeft;			
			
			
			
			while (!isTasksAccepted(firstServer, nbOpsForServer, id1))
			{
				nbOpsForServer--;
			}
			
			List<String> currentTasks = new ArrayList<String>();
			for ( int i = 0; i < nbOpsForServer; i++)
			{
				currentTasks.add(listTasks.get(indexCurrentTask + i));
				
			}
				indexCurrentTask += nbOpsForServer;
				
				
			int result1 = 0;
			int result2 = 1;
			
	
			result1 = sendTasksToServer(firstServer, currentTasks, id1);
			result2 = sendTasksToServer(secondServer, currentTasks, id2);	
			System.out.println(result1+ " " + result2);
			
			if (result1 == result2)
			{
				sum += result1;
				System.out.println("Nb tasks: " + nbOpsForServer +" "+sum);
			}
			else
			{
				System.out.println("Donnees non coherentes ");
			}
			
			if (indexCurrentTask == listTasks.size())
			{
				isFinished = true;
			}
			
			if (!it.hasNext())
				it = listServerStubs.entrySet().iterator();
		}
	}

	private void sendSecurely()
	{
				
		checkServerState();
		
		Iterator it = listServerStubs.entrySet().iterator();
		while (it.hasNext() && (indexCurrentTask != listTasks.size()))
		{
			
			if (!it.hasNext())
			{
				break;
			}
			
			Map.Entry map1 = (Map.Entry) it.next();
			
			ServerInterface currentServer = (ServerInterface)map1.getValue();
			String id1 = map1.getKey().toString();
			
						
			int nbOpsForServer = 10;
			
			int nbTasksLeft = 0;
			nbTasksLeft = (listTasks.size()) - indexCurrentTask;

			if (nbTasksLeft < 10)
				nbOpsForServer = nbTasksLeft;			
			
			
			
			while (!isTasksAccepted(currentServer, nbOpsForServer, id1))
			{
				
				nbOpsForServer--;
			}
			
			List<String> currentTasks = new ArrayList<String>();
			for ( int i = 0; i < nbOpsForServer; i++)
			{
				currentTasks.add(listTasks.get(indexCurrentTask + i));
				
			}
			
				indexCurrentTask += nbOpsForServer;
				sum += sendTasksToServer(currentServer, currentTasks, id1);
			
			System.out.println("Nb tasks: " + nbOpsForServer +" "+sum);
			
				if (indexCurrentTask == listTasks.size())
				{
					isFinished = true;
				}
			
			if (!it.hasNext())
				it = listServerStubs.entrySet().iterator();
		}
	}


	private static void checkServerState()
	{
		Iterator it = listServerStubs.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry map1 = (Map.Entry) it.next();
			ServerInterface serverToTest = (ServerInterface) map1.getValue();
			String currentKey = map1.getKey().toString();
			try
			{	// Une appel RMI arbitraire
				serverToTest.isTasksAccepted(0);
			}
			catch (Exception e) 
			{
				listServerStubs.remove(currentKey);
				try
				{
				serverServiceRepertoireStub.removeServer(currentKey);
				}
				catch (Exception excep) {}
				break;
			}	
		}
		
	}

	private void sendTasks()
	{
		
		
		
		readFileTask();
		

		Iterator it = listServerStubs.entrySet().iterator();
		/*
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
			//System.out.println(isFinished);
		}
		System.out.println(isFinished);
		System.out.println("FINAL SUMM : " + sum);
		*/
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
		checkServerState();
		try
		{					
			listIDIP = serverServiceRepertoireStub.getListServers();
			System.out.println(listIDIP);
			
			Iterator it = listIDIP.entrySet().iterator();
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
	
	private boolean isTasksAccepted(ServerInterface server, int nbOfTasks, String uuid)
	{
		
		boolean result = false;
		try
		{					
			
			result =  server.isTasksAccepted(nbOfTasks);
			//int result =  distantServerStub.isTasksAcceptedvoid();
			
		}
		catch (Exception e) 
		{
			e.getMessage();
			//retirer des listes 
			//retirer du service
			System.out.println("LE BATARD EST DOWN 2 " + uuid);		
		}
		return result;
	}
	
	private int sendTasksToServer(ServerInterface server, List<String> listOps, String uuid)
	{
		
		int result = 0;
		try
		{					
			// TODO: PANNE A GERER
			
			
			ServerInterface test = loadServerStub(uuid, listIDIP.get(uuid));
			if (test == null)
					System.out.println("LE BATARD EST DOWN 2 " + uuid);
			result =  test.sendTasks(listOps);
			//int result =  distantServerStub.isTasksAcceptedvoid();
		}
		catch (Exception e) 
		{
			System.out.println("Le serveur est down: " + uuid);
		}		
		
		return result;
	}




	// ORDRE DIMPORTAANCE DES TACHES
	// PANNES, THREADS, Authentification
	
	
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
						
			synchronized ((Object) indexCurrentTask)
			 {
				synchronized ((Object) sum) 
				{
					synchronized ((Object) isFinished)
					 {
						
						int nbOpsForServer = 10;
			
						int nbTasksLeft = 0;
						//synchronized ((Object) indexCurrentTask) {
						nbTasksLeft = (listTasks.size()) - indexCurrentTask;
						//}
						if (nbTasksLeft < 10)
							nbOpsForServer = nbTasksLeft;			
						
						while (!isTasksAccepted(currentServerStub, nbOpsForServer, " "))
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
							sum += sendTasksToServer(currentServerStub, currentTasks, "  ");
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
	}
}
	
}



