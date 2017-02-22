package serial;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ConsoleClient {

	private static Socket soc;
	private static int port = 60020;
	String lista = "";
	String aux = "";

	public static void main(String[] args) {
		for (String s: args) {
            System.out.println(s);
        }

		String User = args[0];
		String host = args[1];
		String peticion = args[2];
		String lista = "";
		String aux = "";
		int i = 0;
		Chunk d = null;

		try {
			// pedir lista
			try {
				System.out.println("Connecting...");
				soc = new Socket(host, port);
				System.out.println("Connected.");
			} catch (ConnectException e) {
				System.out.println("Host at " + host + " is down.");
				e.printStackTrace();
				System.exit(1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			d = new Chunk();
			d.setId(-1);
			d.setName("list");
			d.setUser(User);
			System.out.println("Initializing streams...");
			OutputStream out = soc.getOutputStream();
			InputStream in = soc.getInputStream();
			ObjectOutput toServer = new ObjectOutputStream(out);
			toServer.flush();
			ObjectInput fromServer = new ObjectInputStream(in);
			System.out.println("Streams ready.");

			toServer.writeObject(d);
			System.out.println("List request sent.");

			d = (Chunk) fromServer.readObject();
			System.out.println("List received.");
			if (d.getId() == -1) {
				ArrayList<String> list = d.getList();
				System.out.println("Loading books...");
				if (list.size() != 0) {
					for (String book : list) {
						aux = "Código:" + i + "                                ";
						lista += aux.substring(0, 15) + book + "\n";
						i++;
					}
				} else {
					lista = "No hay libros disponibles";
				}
				System.out.println(lista);
			} else if (d.getId() == 0) {
				System.out.println("El usuario no está registrado ");
				soc.close();
				System.exit(0);
			}
		} catch (IOException | ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			// Closing the socket
			try {
				soc.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		if (peticion.equals("cargar")) {
			try {
				String ruta = args[3];

				soc = new Socket(host, port);
				d = new Chunk();
				System.out.println("Initializing streams...");
				OutputStream out = soc.getOutputStream();
				InputStream in = soc.getInputStream();
				ObjectOutput toServer = new ObjectOutputStream(out);
				toServer.flush();
				ObjectInput fromServer = new ObjectInputStream(in);
				System.out.println("Streams ready.");
				Path path = Paths.get(ruta);
				byte[] data;
				data = Files.readAllBytes(path);
				d.setInfo(data);
				d.setName(path.getFileName().toString());
				d.setId(1);
				System.out.println("File ready for transfer...");
				toServer.writeObject(d);
				toServer.flush();
				System.out.println("Chunk with file '" + path.getFileName().toString() + "' has been sent!");
				try {
					d = (Chunk) fromServer.readObject();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (d.getId() == 1) {
					System.out.println("Nuevo libro cargado exitosamente!");
					ArrayList<String> list = d.getList();
					System.out.println("Loading books...");
					if (list.size() != 0) {
						for (String book : list) {
							aux = "Código:" + i + "                                ";
							lista += aux.substring(0, 15) + book + "\n";
							i++;
						}
					} else {
						lista = "No hay libros disponibles";
					}
					System.out.println(lista);
				} else {
					System.out.println("Error durante la carga.");
				}

				toServer.flush();
				toServer.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				// Closing the socket
				try {
					soc.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} else if (peticion.equals("descargar")) {
			// descarga
			try {

				System.out.println("Connecting...");
				soc = new Socket(host, port);
				System.out.println("Connected.");

				d = new Chunk();

				System.out.println("Initializing streams...");
				OutputStream out = soc.getOutputStream();
				InputStream in = soc.getInputStream();
				ObjectOutput toServer = new ObjectOutputStream(out);
				toServer.flush();
				ObjectInput fromServer = new ObjectInputStream(in);
				System.out.println("Streams ready.");

				FileOutputStream fos = null;

				int random = (int) Math.random() * i;

				d.setName("" + random);
				d.setId(0);
				toServer.writeObject(d);

				toServer.flush();

				System.out.println("Book requested code : " + d.getName());

				d = (Chunk) fromServer.readObject();

				try {
					fos = new FileOutputStream(new File(d.getName()));
					fos.write(d.getInfo());
					fos.close();
					System.out.println("Libro descargado exitosamente!");
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				// Closing the socket
				try {
					soc.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}

	}
}
