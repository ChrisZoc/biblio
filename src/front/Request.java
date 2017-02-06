package front;

import java.io.ObjectOutput;
import java.net.Socket;

import serial.Chunk;

public class Request {
	private Socket soc;
	private Chunk req;
	private ObjectOutput toClient;
	
	public Request(Socket soc, Chunk req, ObjectOutput toClient) {
		super();
		this.soc = soc;
		this.req = req;
		this.toClient = toClient;
	}

	public Socket getSoc() {
		return soc;
	}

	public void setSoc(Socket soc) {
		this.soc = soc;
	}

	public Chunk getReq() {
		return req;
	}

	public void setReq(Chunk req) {
		this.req = req;
	}

	public ObjectOutput getToClient() {
		return toClient;
	}

	public void setToClient(ObjectOutput toClient) {
		this.toClient = toClient;
	}	

}