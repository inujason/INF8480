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
import java.io.BufferedWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.FileInputStream;
import java.security.MessageDigest;

import java.util.UUID;

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

	
	// On connecte le client au serveur distant avec adresse IP fixe.
	public Client(String distantServerHostname) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		//localServer = new FakeServer();
		
		//localServerStub = loadServerStub("127.0.0.1");

		if (distantServerHostname != null) {
			distantServerStub = loadServerStub(distantServerHostname);
		}
	}

	
	private void run() 
	{	
		// On cree son ID si il nest pas present dans son fichier
		CreateClientID();
			
		if (distantServerStub != null && command != null)
		{
			// Liste des comandes possibles par le client
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


	
	// Fonction qui permet de mettre a jour ou de telecharger un fichier existant du serveur
	private void get()
	{	
		String checksum = "";
		try
		{
			File targetFile = new File(filename+".txt");
			// Si le fichier n'existe pas, on force le checksum a rien pour recevoir une copie du serveur
			if (!targetFile.isFile())
			{
				checksum = "";
			}
			else
			{
				StringBuffer hexString = new StringBuffer();
				// On tente de creer un hash MD5 dont le checksum du fichier s'il existe
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
					checksum = hexString.toString();					
				} catch (Exception e) {}
				
			}
			
			
			//HashMap<String, String> result =  new HashMap(localServerStub.get(filename, checksum));
			// Appel de requete get au serveur distant
			HashMap<String, String> result =  new HashMap(distantServerStub.get(filename, checksum));
			
			// Resultats de la requete
			for (Map.Entry<String, String> entry : result.entrySet())
			{
				String currenthash = entry.getKey();
				String fileContent = entry.getValue();
				
				// Si on recoit 0 le fichier est deja a jour
				if (currenthash.equals("0"))
					System.out.println("Le fichier est deja a jour");
				// le fichier n'existe pas au serveur
				else if (currenthash.equals("NE"))
				{
					System.out.println("Le fichier n'existe pas");
				}
				// La requete a echoue au niveau du serveur
				else if (currenthash.equals("ECHEC"))
				{
					System.out.println("L'operation a echouee");
				}
				// On met a jour le fichier local avec celui du serveur
				else
				{
					try
					{
						if(targetFile.isFile())
						{
							targetFile.delete();
							targetFile = new File(filename+".txt");
						}
						else
						{
							targetFile.createNewFile();
						}	
							FileWriter fw = new FileWriter(targetFile, false);
							fw.write(fileContent);
							fw.close();
							
							System.out.println("Le fichier est synchronise avec celui du server");
							System.out.println("hash:" + currenthash);
							System.out.println("filecontent: " + fileContent);
							
					} catch (Exception e) {}
				}
			}
		}
		catch (RemoteException e)
		{
			System.out.println("Erreur" + e.getMessage());
		}


	}
	
	// Fonction qui permet d'affichier la liste des fichiers et leur etat de verrou provenant du serveur
	private void list()
	{
		try
		{
			//HashMap<String, String> result =  new HashMap(localServerStub.list());
			// Invocation de la methode au serveur
			HashMap<String, String> result =  new HashMap(distantServerStub.list());
			
			// Affichage au console
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
	
	// Fonction qui cree un fichier au serveur
	private void create()
	{
		try
		{
			//String result = localServerStub.create(filename);
			// Invocation de la methode a distance
			String result = distantServerStub.create(filename);

			// Resultat de l'invocation valider par le serveur
			System.out.println(result);
		}
		catch (RemoteException e)
		{
			System.out.println("Erreur" + e.getMessage());
		}
	}
	
	// Fonction lock qui permet de verrouiller un fichier afin de faire des modification locales de maniere synchrone
	// Le client qui le lock pourra donc le modifier et les autres non.
	private void lock()
	{
		try
		{
			// Le Id du client
			String ID = new String(Files.readAllBytes(Paths.get("ClientID.txt")));
			
			// Appel get our synchroniser(MAJ) le fichier avec le server avant de lock
			get();
		
			//String result = localServerStub.lock(ID, filename, "null");
			// Invocation de la methode lock au serveur
			String result = distantServerStub.lock(ID, filename, "null");
			// Affichage du message de retour de l'invocation provenant du serveur.
			System.out.println(result);
		}
		catch (Exception e)
		{
			System.out.println("Erreur" + e.getMessage());
		}
	}
	
	
	// Methode push qui permet d'envoyer le fichier verrouille contenant les modifications apportes
	private void push()
	{
		try
		{
			// Le client ID 
			String ID = new String(Files.readAllBytes(Paths.get("ClientID.txt")));
			
			File file = new File(filename + ".txt");
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();
			String content = new String(data, "UTF-8");
			
			//String result = localServerStub.push(ID, filename, content);
			// L'invocation de la methode push au niveau du serveur
			String result = distantServerStub.push(ID, filename, content);
			// Affichage du resultat de l'invocation
			System.out.println(result);
		}
		catch (Exception e)
		{
			System.out.println("Erreur" + e.getMessage());
		}
	}
	
	
	// Cette fonction permet de telecharger les fichiers dans le serveurs vers notre dossier local.
	// Les fichiers existants sont mis a jour et ceux qui n'existent pas localement sont ajoutes.
	private void syncLocalDirectory()
	{
		try
		{
			//HashMap<String, String> result =  new HashMap(localServerStub.syncLocalDirectory());
			// Invocation de la methode au serveur
			HashMap<String, String> result =  new HashMap(distantServerStub.syncLocalDirectory());
			
			// On met a jour notre dossier avec une reponse venant du serveur contennat la liste des fichiers et leur contenu.
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
	
	// Generation du client ID, si non present, lorsque le client tente de se connecter
	private void CreateClientID()
	{
		File clientIDFile = new File("ClientID.txt");
		// On verifie si le client possede deja un ID
		// Sinon le cree son ID dans un fichier nomme ClientID.txt
		if (!clientIDFile.isFile())
		{
		try
			{
			clientIDFile.createNewFile();
			
			//String result =  localServerStub.CreateClientID();
			String result =  distantServerStub.CreateClientID();
			
				BufferedWriter writer = null;
				writer = new BufferedWriter(new FileWriter(clientIDFile));
		        writer.write(result);
		        writer.close();
		    }
		    catch (Exception e) {e.getMessage();}
		}
		
		
	}

	
}
