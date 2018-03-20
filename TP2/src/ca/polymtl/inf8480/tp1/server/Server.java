package ca.polymtl.inf8480.tp1.server;

import java.rmi.ConnectException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
/*
import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.FileInputStream;

import java.security.MessageDigest;

import java.io.BufferedReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.nio.charset.StandardCharsets;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
* */
import java.util.Random;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;

import java.net.InetAddress;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;
import ca.polymtl.inf8480.tp1.shared.ServerServiceInterface;
import ca.polymtl.inf8480.tp1.ops.Operations;

public class Server implements ServerInterface {

	private static ServerServiceInterface serverServiceRepertoireStub = null;







	private final static String id = UUID.randomUUID().toString();



	private static boolean isSecure = true;
	private static int tauxMalice = 0;








	public static Random random;
	static int qMaxCapacity;
	

	public static void main(String[] args) 
	{
		System.out.println("ARGRS " + args.length);
		
		if (args.length > 0) {	

			tauxMalice = Integer.parseInt(args[0]);
				
			
		}
		
		
		
		Server server = new Server();
		
		random = new Random();
		
		qMaxCapacity = 3 + Math.abs(random.nextInt()%3);
		
		System.out.println("Service avec taux de malice de: " + tauxMalice);
		System.out.println("Capacite de taches du serveur: " + qMaxCapacity);
		
		server.run();
		registerToServerService();
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
			registry.rebind(id, stub);
		} catch (ConnectException e) {
			System.err
					.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}
 

	private static void registerToServerService()
	{
		
		serverServiceRepertoireStub = null;
	

		try {
			//Registry registry = LocateRegistry.getRegistry("132.207.12.104");
			Registry registry = LocateRegistry.getRegistry("127.0.0.1");
			serverServiceRepertoireStub = (ServerServiceInterface) registry.lookup("service");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		try {
		//System.out.println(InetAddress.getLocalHost().getHostAddress());
		serverServiceRepertoireStub.registerServer(id, InetAddress.getLocalHost().getHostAddress());
		} catch(Exception e ) 
		{
			System.out.println(e.getMessage());
		}	
		
		
	}


	/*
	 * Méthode accessible par RMI. 
	 * Calcule si serveur accepte les taches
	 * Retourne le resultat des calcul
	 */
	 @Override
	public boolean isTasksAccepted(int numberofTasks) throws RemoteException
	{
		if (numberofTasks <= qMaxCapacity)
			return true;
		int tauxRefus = ((numberofTasks - qMaxCapacity)/(5*qMaxCapacity))*100;
		int r = Math.abs(random.nextInt()%100);
		if (r < tauxRefus)
			return false;
		else
			return true;
	}
	
	 /*
	 * Méthode accessible par RMI. 
	 * Calcule la liste des taches
	 * Retourne le resultat des calcul
	 */
	@Override
	public int sendTasks(List<String> listOps) throws RemoteException
	{
	
		
		
		if (isSecure == true)
		{
			
			return sendSecureTasks(listOps);
		}
		else
		{
			// System.out.println("tauxMalice: " + tauxMalice);
			if (tauxMalice > 0)
			{
				System.out.println("SECURITY " + isSecure + " tauxMalice: " + tauxMalice);
				int r = Math.abs(random.nextInt()%100);
				// System.out.println("r: " + r + " TauxMalice:" + tauxMalice);
				if (r < tauxMalice)
				{
					return random.nextInt();
				}
				else
					return sendSecureTasks(listOps);
			}
			else
				return sendSecureTasks(listOps);
		}
	}
	
	
	public int sendSecureTasks(List<String> listOps) throws RemoteException
	{
		int sum = 0;
		//Operations operationDOER = new Operations();
		
		for (String task :
				listOps)
		{
			String[] splits = task.split(" ");
			String op = splits[0];
			int operand = Integer.parseInt(splits[1]);
			int value = 0;
			
			if (op.equals("pell"))
			{
				value = pell(operand);
				//System.out.println("VALUE " + value);
			}
			if (op.equals("prime"))
			{
				value = prime(operand);
				//System.out.println("VALUE " + value);
			}
			
			
			sum += value %4000;
			//System.out.println(value);
			//System.out.println("sum " + sum);
		}
		
		//System.out.println("Final " + sum);
					
		return sum;
	}
	
	
	@Override
	public void changeBehaviours(boolean security) throws RemoteException
	{
		
		isSecure = security;
	}
	
	
	
	public static int pell(int x) {
		if (x == 0)
			return 0;
		if (x == 1)
			return 1;
		return 2 * pell(x - 1) + pell(x - 2);
	}
	
	public static int prime(int x) {
		int highestPrime = 0;
		
		for (int i = 1; i <= x; ++i)
		{
			if (isPrime(i) && x % i == 0 && i > highestPrime)
				highestPrime = i;
		}
		
		return highestPrime;
	}
	
	private static boolean isPrime(int x) {
		if (x <= 1)
			return false;

		for (int i = 2; i < x; ++i)
		{
			if (x % i == 0)
				return false;
		}
		
		return true;		
	}
	
	@Override
	public void testConnection() throws RemoteException
	{
		
	}
	
	
}
