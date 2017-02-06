package serial;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
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


public class ServerThread   {
	private Thread runner;
	private Socket soc;


	public ServerThread(Socket ss) {
		soc = ss;
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

			Chunk d = null;
			System.out.println("Initializing streams...");
			OutputStream out = soc.getOutputStream();
			InputStream in = soc.getInputStream();
			ObjectOutput toClient = new ObjectOutputStream(out);
			toClient.flush();
			ObjectInput fromClient = new ObjectInputStream(in);

			System.out.println("Streams ready.");
			try {
				System.out.println("Reading InputStream...");
				d = (Chunk) fromClient.readObject();
				System.out.println("Chunk with id " + d.getId() + " received.");
				File sharedFolder = new File("./SharedFolder");

				if (d.getId() == -1) { // list request					
					d.setToSyncList(sharedFolder.list());
					System.out.println("Files in the shared folder:");
					for (String file : d.getToSyncList()) {
						System.out.println("> " + file);
					}
					toClient.writeObject(d);
				} else if (d.getId() == 0){ // download book
					int id = Integer.parseInt(d.getName());
					String filename = sharedFolder.list()[id];
					System.out.println("The book with id '" + id + "' has been requested ");
					
					Path path = Paths.get("./SharedFolder/" + filename);
					byte[] data;
					System.out.println("File ready for transfer.");
					try {
						data = Files.readAllBytes(path);
						d.setInfo(data);
						d.setName(filename);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					toClient.writeObject(d);
					System.out.println(
							"Chunk with file '" + filename + "' has been sent!");
					toClient.flush();
					toClient.close();

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