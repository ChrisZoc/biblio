package serial;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Chunk implements Serializable{
	
	private byte id;
	private byte[] name;
	private byte[] info;
	private ArrayList<String> listOfFiles;
	private String User;
		
	public String getUser() {
		return User;
	}
	public void setUser(String user) {
		User = user;
	}
	public Chunk() {
		super();
		byte[] aux = {0};
		info = aux;
	}
	public int getId() {
		return (int) id;
	}
	public void setId(int id) {
		this.id = (byte) id;
	}
	public String getName() {
		return new String(name, StandardCharsets.UTF_8);
	}
	public void setName(String name) {
		this.name = name.getBytes(StandardCharsets.UTF_8);
	}
	public byte[] getInfo() {
		return info;
	}
	public void setInfo(byte[] info) {
		this.info = info;
	}
	public ArrayList<String> getList() {
		return listOfFiles;
	}
	public void setList(ArrayList<String> toSyncList) {
		this.listOfFiles = toSyncList;
	}
}