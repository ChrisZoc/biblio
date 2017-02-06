package serial;

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

public class ServerThread implements Runnable {
	private Thread runner;
	private Socket soc;
	// private static ArrayList<String> users;

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
			// UserList usuarios = new UserList();
			// users = usuarios.getUserList();
			System.out.println("Streams ready.");
			try {
				System.out.println("Reading InputStream...");
				d = (Chunk) fromClient.readObject();
				System.out.println("Chunk with id " + d.getId() + " received.");
				File sharedFolder = new File(Server.getSharedfolder());

				if (d.getId() == -1) { // list request
					// boolean registered=false;
					// for (String user : users) {
					// if (d.getUser().equals(user)){
					// registered = true;
					// break;
					// }
					// }if(registered){
					// d.setId(1);
					// }else{
					// d.setId(0);
					// }
					ArrayList<String> list = new ArrayList<String>();
					System.out.println("Files in the shared folder:");
					for (String file : sharedFolder.list()) {
						System.out.println(">" + file);
						list.add(file);
					}
					d.setList(list);
					toClient.writeObject(d);
				} else if (d.getId() == 0) { // download book
					int id = Integer.parseInt(d.getName());
					String filename = sharedFolder.list()[id];
					System.out.println("The book with id '" + id + "' has been requested ");
					Path path = Paths.get(Server.getSharedfolder() + "/" + filename);
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
					System.out.println("Chunk with file '" + filename + "' has been sent!");
					toClient.flush();
					toClient.close();
				} else if (d.getId() == 1) {
					guardarCarga(d);
					d = new Chunk();
					d.setId(1);
					ArrayList<String> list = new ArrayList<String>();
					System.out.println("Files in the shared folder:");
					for (String file : sharedFolder.list()) {
						System.out.println(">" + file);
						list.add(file);
					}
					d.setList(list);
					toClient.writeObject(d);
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

	private void guardarCarga(Chunk d) throws IOException {
		System.out.println("Writing new book file called " + d.getName() + "...");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(Server.getSharedfolder() + "/" + d.getName());
			fos.write(d.getInfo());
			fos.close();
			System.out.println("Nuevo libro cargado exitosamente!");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
