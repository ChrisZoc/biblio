package serial;

import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JSpinner;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class Client extends JFrame {
	private String User;
	private JPanel contentPane;
	private JTextField textField_1;
	private static Socket soc;
	private static int port = 60020;
	String host = "localhost";
	String ruta;
	String lista = "";
	int i = 0;
	String aux = "";
	JTextArea textArea;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client frame = new Client();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Client() {
		User = JOptionPane.showInputDialog("Ingrese el nombre de Usuario");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 410);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnCargar = new JButton("Cargar libro");
		btnCargar.setEnabled(false);
		btnCargar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					soc = new Socket(host, port);
					Chunk d = new Chunk();
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
						JOptionPane.showMessageDialog(null, "Nuevo libro cargado exitosamente!");
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
						textArea.setText(lista);
					} else {
						JOptionPane.showMessageDialog(null, "Error durante la carga.");
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
			}
		});
		btnCargar.setBounds(237, 311, 187, 23);
		contentPane.add(btnCargar);

		JSpinner spinner = new JSpinner();
		spinner.setBounds(105, 280, 97, 20);
		contentPane.add(spinner);

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
		contentPane.add(textArea);

		JScrollPane sp = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		textArea.setBounds(58, 11, 327, 179);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		sp.setBounds(20, 30, 404, 174);
		contentPane.add(sp);

		JLabel lblCdigoDeLibro = new JLabel("C\u00F3digo de Libro");
		lblCdigoDeLibro.setBounds(13, 283, 97, 14);
		contentPane.add(lblCdigoDeLibro);

		JButton btnDescargar = new JButton(" Descargar libro");
		btnDescargar.setEnabled(false);
		btnDescargar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					System.out.println("Connecting...");
					soc = new Socket(host, port);
					System.out.println("Connected.");

					Chunk d = null;
					d = new Chunk();

					System.out.println("Initializing streams...");
					OutputStream out = soc.getOutputStream();
					InputStream in = soc.getInputStream();
					ObjectOutput toServer = new ObjectOutputStream(out);
					toServer.flush();
					ObjectInput fromServer = new ObjectInputStream(in);
					System.out.println("Streams ready.");

					FileOutputStream fos = null;

					d.setName((String.valueOf(spinner.getValue())));
					d.setId(0);
					toServer.writeObject(d);

					toServer.flush();

					System.out.println("Book requested code : " + d.getName());

					d = (Chunk) fromServer.readObject();

					JFileChooser chooser = new JFileChooser();
					chooser.setDialogTitle("Specify a file to save");
					chooser.setName(d.getName());
					chooser.setSelectedFile(new File(d.getName()));
					int retrival = chooser.showSaveDialog(null);
					if (retrival == JFileChooser.APPROVE_OPTION) {
						try {
							fos = new FileOutputStream(chooser.getSelectedFile());
							fos.write(d.getInfo());
							fos.close();
							JOptionPane.showMessageDialog(null, "Libro descargado exitosamente!");
						} catch (Exception ex) {
							ex.printStackTrace();
						}
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
		});
		btnDescargar.setBounds(20, 311, 182, 23);
		contentPane.add(btnDescargar);

		JButton btnDescargarUnLibro = new JButton("Descargar un libro");
		btnDescargarUnLibro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				spinner.setEnabled(true);
				btnDescargar.setEnabled(true);
			}
		});
		btnDescargarUnLibro.setEnabled(false);
		btnDescargarUnLibro.setBounds(20, 249, 182, 23);
		contentPane.add(btnDescargarUnLibro);

		JButton btnCargarUnLibro = new JButton("Escoger un libro para subir");
		btnCargarUnLibro.setEnabled(false);
		btnCargarUnLibro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String userhome = System.getProperty("user.home");
				JFileChooser fc = new JFileChooser(userhome + "\\Desktop");
				fc.setPreferredSize(new Dimension(800, 900));
				int seleccion = fc.showOpenDialog(contentPane);
				if (seleccion == JFileChooser.APPROVE_OPTION) {

					File fichero = fc.getSelectedFile();
					ruta = fichero.getAbsolutePath();
					textField_1.setText(fichero.getName());
					btnCargar.setEnabled(true);
				}
			}
		});
		btnCargarUnLibro.setBounds(237, 249, 187, 23);
		contentPane.add(btnCargarUnLibro);

		textField_1 = new JTextField();
		textField_1.setEnabled(false);
		textField_1.setBounds(237, 280, 187, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);

		JButton btnCargarLibros = new JButton("Cargar Libros");
		btnCargarLibros.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					RequestBooks();
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			private void RequestBooks() throws UnknownHostException, IOException, ClassNotFoundException {
				try {
					System.out.println("Connecting...");
					soc = new Socket(host, port);
					System.out.println("Connected.");
				} catch (ConnectException e) {
					System.out.println("Host at " + host + " is down.");
					e.printStackTrace();
					throw e;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Chunk d = new Chunk();
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
					textArea.setText(lista);
					btnCargarLibros.setEnabled(false);
					btnCargarUnLibro.setEnabled(true);
					btnDescargarUnLibro.setEnabled(true);
					fromServer.close();
					out.flush();
					in.close();
				} else if (d.getId() == 0) {
					JOptionPane.showMessageDialog(null, "El usuario no está registrado ");
				}
			}
		});
		btnCargarLibros.setBounds(165, 215, 114, 23);
		contentPane.add(btnCargarLibros);

	}
}