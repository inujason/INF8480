package ca.polymtl.inf8480.tp1.client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.lang.Math;
import java.util.Map;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.FileInputStream;
import java.security.MessageDigest;

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

	
		String checksum = "";
		try
		{
			File targetFile = new File(filename+".txt");
			if (!targetFile.isFile())
			{
				checksum = "";
			}
			else
			{
				StringBuffer hexString = new StringBuffer();
				try
				{
					String md5 = null;
					FileInputStream fileInputStream = null;
					MessageDigest md = MessageDigest.getInstance("MD5");
					FileInputStream fis = new FileInputStream(filename+".txt");

System.out.println("HASh MAKKING");
			
				
					byte[] dataBytes = new byte[1024];

					int nread = 0;
					while ((nread = fis.read(dataBytes)) != -1) {
					  md.update(dataBytes, 0, nread);
					};
					byte[] mdbytes = md.digest();

					//hexString = new StringBuffer();
					for (int i=0;i<mdbytes.length;i++) {
						String hex=Integer.toHexString(0xff & mdbytes[i]);
			   	     	if(hex.length()==1) hexString.append('0');
			   	     	hexString.append(hex);
					}
					checksum = hexString.toString();	
					System.out.println("HASh MADE:" + hexString.toString());				
				} catch (Exception e) {}
				
			}
			
			System.out.println("GET AVEC CHECKSUM MD5 : " + checksum);
			
			HashMap<String, String> result =  new HashMap(localServerStub.get(filename, checksum));
			for (Map.Entry<String, String> entry : result.entrySet())
			{
				String currenthash = entry.getKey();
				String fileContent = entry.getValue();
				
				System.out.println(currenthash+" "+fileContent);
			}
		}
		catch (RemoteException e)
		{
			System.out.println("Erreur" + e.getMessage());
		}


	}
	private void list()
	{
		try
		{
			HashMap<String, String> result =  new HashMap(localServerStub.list());
			for (Map.Entry<String, String> entry : result.entrySet())
			{
				String currentName = entry.getKey();
				String currentLock = entry.getValue();
				
				System.out.println(currentName+" "+currentLock);
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
			String result = localServerStub.create(filename);

				System.out.println(result);
		}
		catch (RemoteException e)
		{
			System.out.println("Erreur" + e.getMessage());
		}
	}
	private void lock()
	{
		try
		{
			String result = localServerStub.lock("H1a2r3d4y"/*ID*/, filename, "checksumLOL"/*checksum*/);
			System.out.println(result);
		}
		catch (RemoteException e)
		{
			System.out.println("Erreur" + e.getMessage());
		}
	}
	private void push()
	{
		try
		{
			////////////////////////////////////////
			////////////////////////////////////////
			////////////////////////////////////////
			////////////////////////////////////////
			////////////////////////////////////////
			////////////////////////////////////////
			//TODO change le id 
			////////////////////////////////////////
			////////////////////////////////////////
			////////////////////////////////////////
			////////////////////////////////////////
			////////////////////////////////////////
			
			File file = new File(filename + ".txt");
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();
			String content = new String(data, "UTF-8");
			String result = localServerStub.push("H1a2r3d4y" /*ID*/, filename, content);
			System.out.println(result);
		}
		catch (Exception e)
		{
			System.out.println("Erreur" + e.getMessage());
		}
	}
	
	private void syncLocalDirectory()
	{
		try
		{
			//TODO change le content et le id 
			HashMap<String, String> result =  new HashMap(localServerStub.syncLocalDirectory());
			for (Map.Entry<String, String> entry : result.entrySet())
			{
				//Pour chaque fichier on supprime le fichier s'il existe et on le rempli avec le content recu
				String currentName = entry.getKey();
				String currentContent = entry.getValue();
				
				File targetFile = new File(currentName+".txt");
				if(targetFile.isFile())
				{
					targetFile.delete();
					targetFile = new File(currentName+".txt");
				}
				FileWriter fw = new FileWriter(targetFile, false);
				fw.write(currentContent);
				fw.close();
		
			}
			System.out.println("Done yeah!!!!");
		}
		catch (Exception e)
		{
			System.out.println("Erreur" + e.getMessage());
		}
	}
	
	private void CreateClientID()
	{
		
	}

	
}
