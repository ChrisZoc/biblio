package front;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;

import serial.Chunk;

public class FrontEndClientThread implements Runnable {
	Thread runner;
	Socket soc;
	Socket servsoc;
	Chunk toSend;

	public FrontEndClientThread(String[] server, Request peticion) throws ConnectException {
		runner = new Thread(this);
		this.soc = peticion.getSoc();
		this.toSend = peticion.getReq();
		try {
			servsoc = new Socket(server[0], Integer.parseInt(server[1]));
		} catch (ConnectException e) {
			System.out.println("Server at " + server[0] + ":" + server[1] + "is down.");
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Initializing ClientThread...");
		runner.run();
	}

	@Override
	public void run() {
		try {
			ObjectInputStream fromServer = new ObjectInputStream(servsoc.getInputStream());
			ObjectOutput toClient = new ObjectOutputStream(soc.getOutputStream());
			ObjectOutput toServer = new ObjectOutputStream(servsoc.getOutputStream());

			toServer.writeObject(toSend);
			System.out.println("File '" + toSend + "' requested.");
			toServer.flush();

			Chunk d;

			d = (Chunk) fromServer.readObject();
			toClient.writeObject(d);

			toClient.flush();

			soc.close();
			servsoc.close();
			System.out.println("Terminating ClientThread...");
			runner.join();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Error during serialization");
			System.exit(1);
		}
	}

}