package serial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class UserList{

	private ArrayList<String> userList;

	public UserList() {
		super();
		userList = new ArrayList<String>();
		BufferedReader input = null;
		String aux;
		try {
			File inputFile = new File("userlist.txt");
			input = new BufferedReader(new FileReader(inputFile));
			while (input.ready()) {
				aux = input.readLine();
				userList.add(aux);
			}
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "ERROR: Could not find 'userlist.txt'.", "ERROR",
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

	public ArrayList<String> getUserList() {
		return userList;
	}

}