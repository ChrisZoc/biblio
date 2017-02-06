package front;

import java.net.Socket;

import serial.Chunk;

public class Request {
	private Socket soc;
	private Chunk req;
	
	public Request(Socket soc, Chunk req) {
		super();
		this.soc = soc;
		this.req = req;
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
	
	

}
