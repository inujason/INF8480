package ca.polymtl.inf8480.tp1.server;


import java.rmi.AccessException;
import java.rmi.NotBoundException;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.util.Timer; import java.util.TimerTask;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;
import ca.polymtl.inf8480.tp1.shared.ServerServiceInterface;

public class ServerService implements ServerServiceInterface {


	public static HashMap<String, String> listServers = new HashMap<String, String>();


	public static void main(String[] args) 
	{
		ServerService server = new ServerService();
	
		server.run();
		
		//Boucle continuellement afin de verfier si les serveurs connu sont toujours actifs 
		while (true)
		{
			String tmp = "";
			try
			{					
				
				Iterator it = listServers.entrySet().iterator();
				while (it.hasNext())
				{
					Map.Entry serverToCheck = (Map.Entry) it.next();
					String id = (String) serverToCheck.getKey();
					tmp = id;
					String ip = (String) serverToCheck.getValue();
					ServerInterface stub = loadServerStub(id, ip);
					try 
					{
						stub.testConnection();
					}
					catch (Exception e) 
					{
						listServers.remove(tmp);
					}
					
					

				}
			}
			catch (Exception e) {e.getMessage();}
			
			System.out.println(listServers);
		}
		
	}
	
	
	private static ServerInterface loadServerStub(String id, String hostname) {
		
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

	public ServerService() {
		super();
	}

	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			ServerServiceInterface stub = (ServerServiceInterface) UnicastRemoteObject
					.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("service", stub);
			System.out.println("Server of repertory service ready");
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
	public HashMap<String, String> getListServers() throws RemoteException
	{
		//retourne la liste des serveurs connu par le service de noms	
		return listServers;
	}
	
	 /*
	 * Méthode accessible par RMI. 
	 * Calcule la liste des taches
	 * Retourne le resultat des calcul
	 */
	@Override
	public void authenticateRepartiteur(/*Repartiteur repartiteur*/) throws RemoteException
	{
		
	}
	
	/*
	 * Méthode accessible par RMI. 
	 * Calcule la liste des taches
	 * Retourne le resultat des calcul
	 */
	@Override
	public void registerServer(String serverID, String hostname) throws RemoteException
	{
		//Ajoute le serveur qui vient de se connecter a la liste 
		listServers.put(serverID, hostname);
	}
	
	@Override
	public void removeServer(String uuid) throws RemoteException
	{
		//Retire le serveur avec le uuid passé en parametre dans la liste des serveurs en service
		listServers.remove(uuid);
		System.out.println("Serveur : " + uuid + " a ete supprimer du service");
	}

	
}
