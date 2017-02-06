package front;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

public class ServerIPList {

	private ArrayList<String[]> iplist;

	public ServerIPList() {
		super();
		iplist = new ArrayList<String[]>();
		BufferedReader input = null;
		String[] server;
		StringTokenizer aux;
		try {
			File inputFile = new File("iplist.txt");
			input = new BufferedReader(new FileReader(inputFile));
			while (input.ready()) {
				server = new String[2];
				aux = new StringTokenizer(input.readLine());
				server[0] = aux.nextToken();
				server[1] = aux.nextToken();
				iplist.add(server);
				System.out.println(server[0]);
				System.out.println(server[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "ERROR: Could not find 'iplist.txt'.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		} finally {
			try {
				if (input != null)
					input.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public ArrayList<String[]> getIplist() {
		return iplist;
	}

}
