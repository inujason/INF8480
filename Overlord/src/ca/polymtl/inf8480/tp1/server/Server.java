package ca.polymtl.inf8480.tp1.server;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.File;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser
import java.nio.charset.Charset;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;

public class Server implements ServerInterface {


	private static JSONArray state;


	public static void main(String[] args) 
	{
		Server server = new Server();
		server.run();
		
		state = new JSONArray();
		
		
	
		try
		{
			
			File listFiles = new File("state.json");
			if (!listFiles.isExist())
			{
				listFiles.createNewFile();
			}
			else
			{
				JSONParser parser = new JSONParser();
				String jsonString = Files.toString(listFiles, Charsets.UTF_8);
				try
				{
					
					state = (JSONArray)parser.parse(jsonString);
					//Object ocj = parser.parse(jsonString);
					//JSONArray jsonArray = (JSONArray)obj;
				}
				catch (ParseException e)
				{
					System.out.println(e.getMessage());
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		
		// TODO : A CONTINUER A PARTIR DICI
		// TESTER si ca cree le JSON
		// TESTER si il lit un JSON existant
		// FINIR OVERLOAD
		// FINIR le TP
		
		for (int i = 0; i < JSONArray.size(); i++)
		{
			
		}
	}

	public Server() {
		super();
	}

	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("server", stub);
			System.out.println("Server ready.");
		} catch (ConnectException e) {
			System.err
					.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}

	/*
	 * Méthode accessible par RMI. Additionne les deux nombres passés en
	 * paramètre.
	 */
	@Override
	public String[] list() throws RemoteException {
		String[] liste = {"SUCCESS", "FIN"};
		return liste;
	}
	
	public int get(int[] tab) throws RemoteException {
		// est ce que on fait laddition?
		return 0;
	}
	public boolean create(String filename) throws RemoteException {
		
		File newFile = new File(filename+".txt");
		
		try
		{			
			newFile.createNewFile();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return (newFile.isFile());
	}
	public int lock(int[] tab) throws RemoteException {
		// est ce que on fait laddition?
		return 0;
	}
	public int push(int[] tab) throws RemoteException {
		// est ce que on fait laddition?
		return 0;
	}
	
	
}
