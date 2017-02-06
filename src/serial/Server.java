package serial;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private static int port = 60020;

	public static void main(String[] args) throws IOException {
		startServer();
	}
	
	private static void startServer(){
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
						System.out.println("listening...");
						clientSocket = ser.accept();
						new ServerThread(clientSocket);
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
}
