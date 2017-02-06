package serial;

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

public class ServerThread implements Runnable {
	private Thread runner;
	private Socket soc;

	public ServerThread(Socket ss) {
		runner = new Thread(this);
		soc = ss;
		System.out.println("Initializing ServerThread...");
		runner.run();
	}

	@Override
	public void run() {
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
				System.out.println("Terminating ServerThread...");
				soc.close();
				runner.join();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// s.close();
	}
}