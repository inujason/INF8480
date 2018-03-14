package ca.polymtl.inf8480.tp1.server;

import java.rmi.ConnectException;
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


import ca.polymtl.inf8480.tp1.shared.ServerInterface;
import ca.polymtl.inf8480.tp1.ops.Operations;

public class Server implements ServerInterface {

	public static Random random;
	static int qMaxCapacity;

	public static void main(String[] args) 
	{
		Server server = new Server();
	
		
		random = new Random();
		
		qMaxCapacity = 3 + random.nextInt()%3;
		server.run();
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
	 * Méthode accessible par RMI. 
	 * Calcule si serveur accepte les taches
	 * Retourne le resultat des calcul
	 */
	 @Override
	public boolean isTasksAccepted(int numberofTasks) throws RemoteException
	{
		int tauxRefus = ((numberofTasks - qMaxCapacity)/(5*qMaxCapacity))*100;
		int r = random.nextInt()%100;
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
		int sum = 0;
		for (String task :
				listOps)
		{
			String[] splits = task.split(" ");
			String operation = splits[0];
			Integer operand = Integer.parseInt(splits[0]);
			int value = 0;
			
			if (operation.equals("pell"))
				value = Operations.pell(operand);
			else if (operation.equals("prime"))
				value = Operations.prime(operand);
			
			
			sum += value%4000;
			
			
		}
		
		System.out.println(sum);
					
		return sum;
	}
	
}
