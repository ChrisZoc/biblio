package front;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import serial.Chunk;

public class FrontEndServerThread implements Runnable {
	private Thread runner;
	private Socket soc;
	private boolean registered;

	public FrontEndServerThread(Socket ss) {
		runner = new Thread(this);
		soc = ss;
		System.out.println("Initializing ServerThread...");
		runner.run();
	}

	@Override
	public void run() {
		try {
			Chunk d;
			System.out.println("Initializing streams...");
			OutputStream out = soc.getOutputStream();
			InputStream in = soc.getInputStream();
			ObjectOutput toClient = new ObjectOutputStream(out);
			toClient.flush();
			ObjectInput fromClient = new ObjectInputStream(in);
			System.out.println("Streams ready.");
			registered = false;
			try {
				d = (Chunk) fromClient.readObject();
				int c = d.getId();
				System.out.println("Chunk with id " + d.getId() + " received.");

				switch (c) {
				case -1:
					for (String user : FrontEnd.getUsers()) {
						if (d.getUser().equals(user)) {
							registered = true;
							break;
						}
					}
					if (registered) {
						d.setId(-1);
						FrontEnd.add(new Request(soc, d, toClient));
						System.out.println("Request added to queue.");
					} else {
						d.setId(0);
						toClient.writeObject(d);
						System.out.println("User not registered, closing connection...");
						soc.close();
					}
					break;
				case 1:
					for (int i = 0; i < FrontEnd.getServers().size(); i++) {
						FrontEnd.add(new Request(soc, d, toClient));
					}
				default:
					FrontEnd.add(new Request(soc, d, toClient));
					System.out.println("Request added to queue.");
				}
				System.out.println("Terminating ServerThread...");
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