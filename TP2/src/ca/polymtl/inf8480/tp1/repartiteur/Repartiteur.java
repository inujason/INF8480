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
		readFileTask();
		
		//if (distantServerStub != null && command != null)
		{
			// Liste des comandes possibles par le client
			/*
			switch(command)
			{
				case "list":
					list();
					break;
				case "get":
					get();
					break;
				case "create":
					create();
					break;
				case "lock":
					lock();
					break;
				case "push":
					push();
					break;
				case "syncLocalDirectory":
					syncLocalDirectory();
					break;
				default:
					System.out.println("Commande non reconnue");
			}
			*/
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
	
	private boolean isTasksAcceptedvoid()
	{
		/*
		try
		{					
			int result =  localServerStub.isTasksAcceptedvoid();
			//int result =  distantServerStub.isTasksAcceptedvoid();
		}
		    catch (Exception e) {e.getMessage();}		
		*/
		return false;	
	}

	
}
