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


import ca.polymtl.inf8480.tp1.shared.ServerInterface;

public class Server implements ServerInterface {


	//private static NodeList nodeList;


	public static void main(String[] args) 
	{
		Server server = new Server();
		server.run();
		

		try
		{
			//Lors de l'execution du serveur, on charge un fichier représentant l'etat du serveur
			File serverStateFile = new File("serverState.xml");
			
			//Dans le cas ou c'est un nouveau serveur on cree un nouveau xml
			//Sinon on charge le xml dans une liste! Les "file" dans le xml represente les fichiers presents sur le serveur
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
				NodeList nodeList = doc.getElementsByTagName("file");
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
	 * Méthode accessible par RMI. Retourne la liste des fichiers present sur le serveur
	 */
	@Override
	public HashMap<String, String> list() throws RemoteException {
		
		File serverStateFile = new File("serverState.xml");
		HashMap<String, String> list = new HashMap<String, String>();
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc;
			
			doc = db.parse(serverStateFile);			
			NodeList nodeList = doc.getElementsByTagName("file");
			
			
			//On parcours les files du xml et on ajoute les attributs "name" et "isLocked" dans la hashMap
			//Si le xml est vide on ajoute "0" "fichiers" dans la hashMap
			
			int nbFichier = nodeList.getLength();
			
			if (nbFichier == 0)
			{
				list.put("0", "fichier");
			}
			else
			{		
				for ( int i=0; i<nbFichier; i++)
					{
						Element currentNode = (Element)nodeList.item(i);
						
						list.put(currentNode.getAttribute("name"), currentNode.getAttribute("isLocked"));
					}
			}	
		}
		catch (Exception e) {}
		return list;
	}
	
	/*
	 * Méthode accessible par RMI. Retourne le contenu d'un fichier si celui ci est present sur le serveur
	 */
	public HashMap<String, String> get(String filename, String checksum) throws RemoteException {
		
		
		HashMap<String, String> checksumAndFileContent = new HashMap<String, String>();
		
		File targetFile = new File(filename+".txt");
		
		//On verifie si le fichier exist dans le serveur
		if (targetFile.isFile())
		{
			try 
			{
				File serverStateFile = new File("serverState.xml");
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document doc;
				
				//on parcours la liste de fichier dans le serveur
				doc = db.parse(serverStateFile);			
				NodeList nodeList = doc.getElementsByTagName("file");
				
				for (int i=0;i<nodeList.getLength();i++)
				{
					Element currentElement = (Element)nodeList.item(i);
					
					
					
					if (currentElement.getAttribute("name").equals(filename))
					{
						//si les checksum sont identiques, on ne fait rien
						if (currentElement.getAttribute("checksum").equals(checksum))
						{			
							checksumAndFileContent.put("0", "");
							return checksumAndFileContent;
						}
						// Sinon le fichier du client doit être mis à fichier a jour 
						else 
						{
						 	String fileContent = new String(Files.readAllBytes(Paths.get(filename+".txt")));
							checksumAndFileContent.put(currentElement.getAttribute("checksum"), fileContent);
							
							return checksumAndFileContent;
						}
					}
				}
			}
			catch (Exception e){}
		}
		else
		{
			checksumAndFileContent.put("NE", "");
			return checksumAndFileContent;
		}
		checksumAndFileContent.put("ECHEC", "");
		return checksumAndFileContent;
		
	}
	
	/*
	 * Méthode accessible par RMI. Crée un fichier sur le serveur
	 */
	public String create(String filename) throws RemoteException {
		
		File newFileServer = new File(filename+".txt");

		//Si un fichier du meme nom existe on ne fait rien sinon on le cree
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
			// on ajoute dans le xml le fichier a ajouter 
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc;
			doc = db.parse(serverStateFile);

			//on cree un nouvel element
			Element root = doc.getDocumentElement();

			Element newFile = doc.createElement("file");
			newFile.setAttribute("name", filename);
			newFile.setAttribute("isLocked", "non verrouillé");
			newFile.setAttribute("lockerID", "");

			//on generer son checksum
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
			// on ajoute au root du xml
			root.appendChild(newFile);

			try
			{
				//on ecrit dans le xml
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
	
	/*
	 * Méthode accessible par RMI. verouille un fichier sur le serveur
	 */
	public String lock(String ID, String filename, String checksum) throws RemoteException {
		
		File targetFile = new File(filename+".txt");
		if (targetFile.isFile())
		{
			try 
			{
				//Si le fichier existe, on le verifie s'il n'est pas verrouillé
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
						if (currentElement.getAttribute("isLocked").equals("verrouillé"))
						{		
							//Si le fichier est verouillé on affiche le ID
							return "Le fichier " 
							+ currentElement.getAttribute("name") 
							+ " est verrouille par le client au ID:" 
							+ currentElement.getAttribute("lockerID");
						}
						else if (currentElement.getAttribute("isLocked").equals("non verrouillé"))
						{
							//on verrouille le fichier  et on modifie le xml
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
	
	/*
	 * Méthode accessible par RMI. Envoie les données d'un fichier sur le serveur
	 */
	public String push(String ID, String filename, String contenu) throws RemoteException {
	
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
			
                        if (currentElement.getAttribute("isLocked").equals("verrouillé"))
                        {
			//on verifie si le fichier a été verrouillé par le bon userID
                            if (currentElement.getAttribute("lockerID").equals(ID))
                            {
                                //on delete le target du serveur, on le recree et on ecrit a l'interieur
                                targetFile.delete();
                                File target = new File(filename+".txt");
                                FileWriter fw = new FileWriter(target, false);
                                fw.write(contenu);
                                fw.close();
                                currentElement.setAttribute("isLocked", "non verrouillé");
                                currentElement.setAttribute("lockerID", "");
                                
				// on ecrit dans le xml
                                TransformerFactory tf = TransformerFactory.newInstance();
				Transformer t = tf.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult output = new StreamResult(new File("serverState.xml"));
				t.transform(source, output);
                                return "Le push a bien ete effectue";
                            }
                            else
                            {
                                 return "Le fichier est verrouille par le client au ID:" + currentElement.getAttribute("lockerID");
                            }
                           
                        }
                        else if (currentElement.getAttribute("isLocked").equals("non verrouillé"))
                        {
                            return "Le fichier n'est pas verrouille, modification impossible";
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
	
	/*
	 * Méthode accessible par RMI. Synchronise le repertoire local avec celui sur le serveur 
	 */
	public HashMap<String, String> syncLocalDirectory() throws RemoteException {
    
		try
		{
			File serverStateFile = new File("serverState.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc;
			
			doc = db.parse(serverStateFile);
			NodeList nodeList = doc.getElementsByTagName("file");
			
			HashMap<String, String> list = new HashMap<String, String>();
			
			for (int i=0;i<nodeList.getLength();i++)
			{
				//Pour chaque elemet du xml on ajoute dans la hashmap son nom et le string du contenu 
				Element currentElement = (Element)nodeList.item(i);
				String nomFile = currentElement.getAttribute("name");
				
				File file = new File(nomFile + ".txt");
				FileInputStream fis = new FileInputStream(file);
				byte[] data = new byte[(int) file.length()];
				fis.read(data);
				fis.close();
				String str = new String(data, "UTF-8");
				list.put(nomFile, str);
				
			}
			return list;
		}
		catch (Exception e){}      
        return null;
	}
	
	public String CreateClientID() throws RemoteException {

		//UUID tmp = new UUID(1L, 1L);

		return UUID.randomUUID().toString();
	}
	
}
