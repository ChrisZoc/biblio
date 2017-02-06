package serial;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private static int port = 60021;
	private static String sharedfolder = "./Libros";	
	
	public static String getSharedfolder() {
		return sharedfolder;
	}

	public static void setSharedfolder(String sharedfolder) {
		Server.sharedfolder = sharedfolder;
	}

	public static void main(String[] args) throws IOException {
		File sharedFolder = new File(sharedfolder);
		if (!sharedFolder.exists()) {
			sharedFolder.mkdirs();
			System.out.println("No shared folder detected, creating.....Done!");
		} else {
			System.out.println("Shared folder detected.");
		}
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
