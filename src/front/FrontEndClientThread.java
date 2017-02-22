package front;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

import serial.Chunk;

public class FrontEndClientThread implements Runnable {
	Thread runner;
	Socket soc;
	Socket servsoc;
	Chunk toSend;
	ObjectOutput toClient;

	public FrontEndClientThread(String[] server, Request peticion) throws Exception {
		runner = new Thread(this);
		this.soc = peticion.getSoc();
		this.toSend = peticion.getReq();
		this.toClient = peticion.getToClient();
		try {
			System.out.println("Connecting to back-end...");
			servsoc = new Socket();
			servsoc.connect(new InetSocketAddress(server[0], Integer.parseInt(server[1])), 1000);
		} catch (Exception e) {
			System.out.println("Server at " + server[0] + ":" + server[1] + " is down.");
			e.printStackTrace();
			throw e;
		}
		System.out.println("Initializing ClientThread...");
		runner.run();
	}

	@Override
	public void run() {
		try {
			ObjectOutput toClient = this.toClient;
			toClient.flush();
			ObjectOutput toServer = new ObjectOutputStream(servsoc.getOutputStream());
			toServer.flush();
			ObjectInputStream fromServer = new ObjectInputStream(servsoc.getInputStream());

			toServer.writeObject(toSend);
			System.out.println("Chunk with id '" + toSend.getId() + "' sent to server.");
			toServer.flush();

			Chunk d;

			d = (Chunk) fromServer.readObject();
			System.out.println("Chunk with id '" + toSend.getId() + "' received from server.");
			
			toClient.writeObject(d);
			System.out.println("Chunk with id '" + toSend.getId() + "' sent to client.");
			toClient.flush();

			soc.close();
			servsoc.close();
			System.out.println("Terminating ClientThread...");
			runner.join();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Error during serialization");
			try {
				runner.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}