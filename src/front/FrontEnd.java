package front;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class FrontEnd {

	private static int port = 60020;
	private static ArrayList<Request> cola;
	private static ArrayList<String[]> servers;
	private static ArrayList<String> users;

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		FrontEnd.port = port;
	}

	public static void add(Request peticion) {
		cola.add(peticion);
	}

	public static Request next() {
		return cola.remove(0);
	}		

	public static ArrayList<String> getUsers() {
		return users;
	}

	public static void main(String args[]) throws IOException {
		cola = new ArrayList<Request>();
		ServerIPList lista = new ServerIPList();
		servers = lista.getIplist();
		UserList usuarios = new UserList();
		users = usuarios.getUserList();
		startServer();
		startClient();
	}

	private static void startServer() throws IOException {
		(new Thread() {

			@Override
			public void run() {
				ServerSocket ser = null;
				try {
					ser = new ServerSocket(port);
				} catch (IOException e) {
					System.err.println("Could not listen on port: " + port + ".");
					System.exit(1);
				}

				Socket clientSocket = null;
				try {
					while (true) {
						clientSocket = ser.accept();
						new FrontEndServerThread(clientSocket);
					}
				} catch (IOException e) {
					System.err.println("Accept failed.");
					System.exit(1);
				}

				try {
					ser.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	private static void startClient() throws IOException {
		(new Thread() {			
			@Override
			public void run() {
				int numServers = servers.size();
				int counter = 0;
				try {
					while(true){
						if(!cola.isEmpty()){
							new FrontEndClientThread(servers.get(counter%numServers), cola.remove(0));
							counter++;
						}
					}
				}catch (IOException e){
					
				}
				
			}			
		}).start();
	}

}
