package serial;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ServerThread   {
	private Thread runner;
	private Socket soc;
	private String clientIP;
	private static File sharedFolder;
	private static String sharedfolder = "./SharedFolder";
	private static String[] actualFileList;
	private static String[] newFileList;
	private static boolean listRequiresUpdate;

	public ServerThread(Socket ss) {
		soc = ss;
		clientIP = soc.getInetAddress().getHostAddress();
		System.out.println("Initializing ServerThread...");
		sendBooksList(soc);
	}
	
	private void sendBooksList(Socket skt) {
		ArrayList<String> my =  new ArrayList<String>();
		sharedFolder = new File(sharedfolder);
		if (!sharedFolder.exists()) {
			sharedFolder.mkdirs();
			System.out.println("No shared folder detected, creating.....Done!");

		} else {
			System.out.println("Shared folder detected.");
		}
		actualFileList = sharedFolder.list();
		Chunk List = null;
		for (String file : actualFileList) {
			my.add(file);
			System.out.println(file);
		}
		List=new Chunk();
		List.setId(0);
		List.setName("List");
		List.setList(my); 
		try {
			OutputStream o = soc.getOutputStream();
			ObjectOutput s = new ObjectOutputStream(o);
			s.writeObject(List);
			s.flush();
			s.close();
			System.out.println("Terminating ClientThread...");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Error during serialization");
			System.exit(1);
		}
	}
}