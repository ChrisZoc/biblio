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
import java.io.RandomAccessFile;
import java.net.ServerSocket;
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
			System.out.println("Terminating ServerThread...");
			listenBook(my);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Error during serialization");
			System.exit(1);
		}
	}

	private void listenBook(ArrayList<String> my) throws ClassNotFoundException {
		ServerSocket ser = null;
		int port=60021;
		try {
			ser = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + 60021 + ".");
			System.exit(1);
		}

		Socket clientSocket = null;
		try {
			while (true) {
				System.out.println("escuchando");
				clientSocket = ser.accept();
				System.out.println("aceptado");
				validate(clientSocket,my);
			}
		} catch (IOException e) {
			System.err.println("Accept failed.");
			System.exit(1);
		}
		try {
			ser.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void validate(Socket clientSocket, ArrayList<String> my) throws IOException, ClassNotFoundException {
		System.out.println("validando");
		Chunk d=null;
		InputStream o1 = null;
		ObjectInput s1 = null;
		FileOutputStream fos = null;
		
		o1 = clientSocket.getInputStream();
		System.out.println("01");
		s1 = new ObjectInputStream(o1);
		System.out.println("s1");
		try {
			d  = (Chunk) s1.readObject();

		} catch (Exception e) {
			e.printStackTrace();
		}

		if(d.getId()==0){
			boolean complete = false;
			Chunk toSend = null;
			Path path = Paths.get(sharedfolder + "/" + my.get(Integer.parseInt(d.getName())));
			byte[] data;
			File archivo = new File(String.valueOf(path));
			do {
				System.out.println("File is being copied, waiting...");
				try {
					complete = isCompletelyWritten(archivo);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (!complete);
			System.out.println("File ready for transfer, sending...");
			try {
				clientSocket=new Socket("localhost",60022);
				data = Files.readAllBytes(path);
				toSend = new Chunk();
				toSend.setInfo(data);
				toSend.setName(my.get(Integer.parseInt(d.getName())));
				toSend.setId(0);
				OutputStream o = clientSocket.getOutputStream();
				ObjectOutput s = new ObjectOutputStream(o);
				s.writeObject(toSend);
				s.flush();
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(d.getId()==1){


		}
	}

	private boolean isCompletelyWritten(File file) throws InterruptedException  {
		RandomAccessFile stream = null;
		try {
			stream = new RandomAccessFile(file, "rw");
			return true;
		} catch (Exception e) {
			Thread.sleep(1000);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					System.out.println("Exception closing file " + file.getName());
				}
			}
		}
		return false;
	}
}