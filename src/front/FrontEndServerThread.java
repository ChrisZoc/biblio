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
				for (String user : FrontEnd.getUsers()) {
					if (d.getName() == user){
						registered = true;
						break;
					}
				}
				if(!registered){
					d.setId(-1); //usuario no registrado
					toClient.writeObject(d);
					fromClient.close();
					toClient.close();
				} else {
					d.setId(1); //usuario registrado
					toClient.writeObject(d);
					d = (Chunk) fromClient.readObject();
					FrontEnd.add(new Request(soc, d));
				}
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
