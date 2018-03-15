package ca.polymtl.inf8480.tp1.repartiteur;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.List;
import java.util.ArrayList;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;

public class Repartiteur {
	
	//FakeServer localServer = null; // Pour tester la latence d'un appel de
									// fonction normal.
									
	private ServerInterface localServerStub = null;
	private ServerInterface distantServerStub = null;
	
	public static String filename = "";
	
	public static List<String> listTasks = new ArrayList<String>();
	
	public static void main(String[] args) {
		
		String distantHostname = null;
		
		
		if (args.length > 0) {
			//Adresse du serveur
			//distantHostname = args[0];
			filename = args[0];		
		}
		
		Repartiteur repartiteur = new Repartiteur("132.207.12.104");
		repartiteur.run();
	}

	
	// On connecte le Repartiteur au serveur distant avec adresse IP fixe.
	public Repartiteur(String distantServerHostname) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		//localServer = new FakeServer();
		
		
		// CHANGER POUR SOIT LOCAL OU DISTANT EN COMMENTANT 
		localServerStub = loadServerStub("127.0.0.1");

		if (distantServerHostname != null) {
			//distantServerStub = loadServerStub(distantServerHostname);
		}
	}

	
	private void run() 
	{	
		//if (distantServerStub != null && command != null)
		
		int indexCurrentTask = 0;
		int sum = 0;
		boolean isFinished = false;
		
		readFileTask();
		
		// Prendre les prochains tasks
		int nbOpsForServer = 10;
		
		
		// TODO : Changement de bool de mode de securite pour les serveurs selon la commande du user!
		
		
		while (!isFinished)
		{
			
			// TODO : THREADS(Donc avec var globales)
			int nbTasksLeft = (listTasks.size()) - indexCurrentTask;
			if (nbTasksLeft < 10)
				nbOpsForServer = nbTasksLeft;
			else
				nbOpsForServer = 10;
			
			
			while (!isTasksAccepted(localServerStub, nbOpsForServer))
			{
				nbOpsForServer--;
			}
			
			List<String> currentTasks = new ArrayList<String>();
			for ( int i = 0; i < nbOpsForServer; i++)
			{
				currentTasks.add(listTasks.get(indexCurrentTask + i));
				
			}
			indexCurrentTask += nbOpsForServer;
			sum += sendTasks(localServerStub, currentTasks);
			
			System.out.println("Nb tasks: " + nbOpsForServer +" "+sum);
			
			if (indexCurrentTask == listTasks.size())
			{
				isFinished = true;
			}
		}
		
		
		
		

	}

	// Tentative de creer le stub des objets du serveur a l'aide du registre
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
		//System.out.println(listTasks);
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
	
	private int sendTasks(ServerInterface server, List<String> listOps)
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

	
}
