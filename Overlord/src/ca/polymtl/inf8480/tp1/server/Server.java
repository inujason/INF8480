package ca.polymtl.inf8480.tp1.server;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.FileInputStream;
import java.security.MessageDigest;

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


import ca.polymtl.inf8480.tp1.shared.ServerInterface;

public class Server implements ServerInterface {


	private static NodeList nodeList;


	public static void main(String[] args) 
	{
		Server server = new Server();
		server.run();
		

		try
		{
			File serverStateFile = new File("serverState.xml");
			

			if (!serverStateFile.isFile())
			{
			
				
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				
				// Set the root element
				Document doc = db.newDocument();
				Element root = doc.createElement("root");
				doc.appendChild(root);
				
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer t = tf.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult output = new StreamResult(new File("serverState.xml"));
				
				//listFiles.createNewFile();

				// Saving..
				t.transform(source, output);
			}
			else
			{			
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc;
				
				doc = db.parse(serverStateFile);			
				nodeList = doc.getElementsByTagName("file");
				/*
				for ( int i=0; i<nodeList.getLength(); i++)
				{
					Element currentNode = (Element)nodeList.item(i);
					
					System.out.println("File " + i + " : "+ currentNode.getAttribute("name") + " " + currentNode.getAttribute("isLocked"));
				}*/
				
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		
	}

	public Server() {
		super();
	}

	private void run() {
		//if (System.getSecurityManager() == null) {
			//System.setSecurityManager(new SecurityManager());
		//}

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
	public HashMap<String, String> list() throws RemoteException {
		
		
		HashMap<String, String> list = new HashMap<String, String>();
		
		int nbFichier = nodeList.getLength();
		
		if (nbFichier == 0)
		{
			list.put("0", "fichiers");
		}
		else
		{		
			for ( int i=0; i<nbFichier; i++)
				{
					Element currentNode = (Element)nodeList.item(i);
					
					list.put(currentNode.getAttribute("name"), currentNode.getAttribute("isLocked"));
				}
		}	
		return list;
	}
	
	public HashMap<String, String> get(String filename, String checksum) throws RemoteException {
		
		
		HashMap<String, String> checksumAndFileContent = new HashMap<String, String>();
		
		File targetFile = new File(filename+".txt");
		
		if (targetFile.isFile())
		{
			try 
			{
				File serverStateFile = new File("serverState.xml");
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc;
				
				
				doc = db.parse(serverStateFile);			
				NodeList nodeList = doc.getElementsByTagName("file");
				
				for (int i=0;i<nodeList.getLength();i++)
				{
					Element currentElement = (Element)nodeList.item(i);
					
					
					
					if (currentElement.getAttribute("name").equals(filename))
					{
						/*
						// Si client na pas le fichier
						if (checksum.equals("0")
						{
							String fileContent = readFile(filename+".txt", Charset.UTF_8);
							checksumAndFileContent.put(currentElement.getAttribute(checksum, fileContent);
							
							return checksumAndFileContent;
						}
						// si le client a le fichier deja a jour
						else*/ if (currentElement.getAttribute("checksum").equals(checksum))
						{			
							checksumAndFileContent.put("0", "");
							return checksumAndFileContent;
						}
						// Si le client doit le fichier a jour 
						else 
						{
						 	String fileContent = new String(Files.readAllBytes(Paths.get(filename+".txt")));
							//String fileContent = readFile(filename+".txt", StandardCharsets.UTF_8);
							checksumAndFileContent.put(currentElement.getAttribute("checksum"), fileContent);
							
							return checksumAndFileContent;
						}
					}
				}
				//return "Le fichier n'est pas dans serverState";
			}
			catch (Exception e){}
		}
		else
		{
			checksumAndFileContent.put("NE", "");
			return checksumAndFileContent;
			//return ("Le fichier n'existe pas");
		}
		checksumAndFileContent.put("NE", "");
		return checksumAndFileContent;
		//return "L'operation a echoue";
		
	}
	
	public String create(String filename) throws RemoteException {
		
		File newFileServer = new File(filename+".txt");

		if(!newFileServer.isFile())
 		{
 			try
 			{
 			newFileServer.createNewFile();
 			} catch (Exception e) {e.getMessage();}
 		}
 		else
 		{
 			return (filename + " est deja existant");
 		}
 		

		File serverStateFile = new File("serverState.xml");
		
		
		
		try
		{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc;
		doc = db.parse(serverStateFile);
			
			
		Element root = doc.getDocumentElement();
		
		Element newFile = doc.createElement("file");
		newFile.setAttribute("name", filename);
		newFile.setAttribute("isLocked", "non verrouillé");
		newFile.setAttribute("lockerID", "");
		
		
		StringBuffer hexString = new StringBuffer();
		try
		{
			String md5 = null;
		    FileInputStream fileInputStream = null;
		    MessageDigest md = MessageDigest.getInstance("MD5");
		    FileInputStream fis = new FileInputStream(filename+".txt");


		
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
			System.out.println("HASH MAKING!");
			
		}catch (Exception e) {e.getMessage();}
		
		
		
        System.out.println("CREATE SERVER MD5 : " + hexString.toString());
		
		newFile.setAttribute("checksum", hexString.toString());

		root.appendChild(newFile);
		
		try
		{
		
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = tf.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult output = new StreamResult(new File("serverState.xml"));
		t.transform(source, output);
		
					
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		} catch (Exception e) {}

		return "Le fichier est cree";
	}
	
	public String lock(String ID, String filename, String checksum) throws RemoteException {
		
		File targetFile = new File(filename+".txt");
		System.out.println("111111111111111111111");
		if (targetFile.isFile())
		{
			try 
			{
				File serverStateFile = new File("serverState.xml");
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc;
				
				System.out.println("222222222222222222");
				
				doc = db.parse(serverStateFile);			
				NodeList nodeList = doc.getElementsByTagName("file");
				
				for (int i=0;i<nodeList.getLength();i++)
				{
					System.out.println("333333333333333333");
					Element currentElement = (Element)nodeList.item(i);
					
					System.out.println(currentElement.getAttribute("name"));
					System.out.println(filename);				
					if (currentElement.getAttribute("name").equals(filename))
					{
						System.out.println("getAtt============filename???");
						if (currentElement.getAttribute("isLocked").equals("verrouillé"))
						{						
							System.out.println("4444444444444444444");
							return "Le fichier est verrouille par le client au ID:" + currentElement.getAttribute("lockerID");
						}
						else if (currentElement.getAttribute("isLocked").equals("non verrouillé"))
						{
							System.out.println("555555555555555555");
							// Et le checksum dans tout ca?
							currentElement.setAttribute("isLocked", "verrouillé");
							currentElement.setAttribute("lockerID", ID);
							
							TransformerFactory tf = TransformerFactory.newInstance();
							Transformer t = tf.newTransformer();
							DOMSource source = new DOMSource(doc);
							StreamResult output = new StreamResult(new File("serverState.xml"));
							t.transform(source, output);
							return "Le fichier est verrouille";
						}
					}
				}
				return "Le fichier n'est pas dans serverState";
			}
			catch (Exception e){}
		}
		else
		{
			return ("Le fichier n'existe pas");
		}
		return "L'operation a echoue";
	}
	
	public int push(int[] tab) throws RemoteException {
		// est ce que on fait laddition?
		return 0;
	}
	
	public int CreateClientID(int[] tab) throws RemoteException {
		// est ce que on fait laddition?
		return 0;
	}
	
}
