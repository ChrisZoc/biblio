package serial;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.TextArea;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javafx.scene.text.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JFileChooser;
import javax.swing.JSpinner;

public class Client extends JFrame {

	private JPanel contentPane;
	private JTextField textField_1;
	private static Socket soc;
	private static int port=60020;
	String host = "localhost";

	/**
	 * Launch the application.
	 */
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
			}
		});
		btnCargar.setBounds(237, 311, 187, 23);
		contentPane.add(btnCargar);

		JSpinner spinner = new JSpinner();
		spinner.setBounds(105, 280, 97, 20);
		contentPane.add(spinner);

		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
		contentPane.add(textArea);

		JScrollPane sp = new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
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
				OutputStream os;
				try {
					Chunk d = null;
					soc =new Socket(host, 60021);
					FileOutputStream fos = null;
					OutputStream o = soc.getOutputStream();
					ObjectOutput s = new ObjectOutputStream(o);
					d= new Chunk();
					d.setName((String.valueOf(spinner.getValue())));
					d.setId(0);
					s.writeObject(d);
					s.flush();
					s.close();
					System.out.println("Book requested code : "+ d.getName());
					
					ServerSocket receive=new ServerSocket(60022);
					
					InputStream o1 = null;
					ObjectInput s1 = null;
					FileOutputStream fos1 = null;
					soc=receive.accept();
					o1 = soc.getInputStream();
					s1 = new ObjectInputStream(o1);
					d  = (Chunk) s1.readObject();
				    JFileChooser chooser = new JFileChooser();
				    chooser.setDialogTitle("Specify a file to save");
				    chooser.setName(d.getName());
				    chooser.setSelectedFile( new File(d.getName()));
				    int retrival = chooser.showSaveDialog(null);
				    if (retrival == JFileChooser.APPROVE_OPTION) {
				        try {
				        	fos1 = new FileOutputStream(chooser.getSelectedFile());
				            fos1.write(d.getInfo());
				            fos1.close();
				        } catch (Exception ex) {
				            ex.printStackTrace();
				        }
				        receive.close();
				    }
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}finally
				{
					//Closing the socket
					try
					{
						soc.close();
					}
					catch(Exception e1)
					{
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

		JButton btnCargarUnLibro = new JButton("Escojer un libro para subir");
		btnCargarUnLibro.setEnabled(false);
		btnCargarUnLibro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String userhome = System.getProperty("user.home");
				JFileChooser fc=new JFileChooser(userhome +"\\Desktop");
				fc.setPreferredSize(new Dimension(800, 900));
				int seleccion=fc.showOpenDialog(contentPane);
				if(seleccion==JFileChooser.APPROVE_OPTION){

					File fichero=fc.getSelectedFile();
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
				ArrayList<String> List =  new ArrayList<String>();
				soc = new Socket(host,port);	
				String lista="";
				Chunk d = null;
				InputStream o = null;
				ObjectInput s = null;
				FileOutputStream fos = null;
				o = soc.getInputStream();
				s = new ObjectInputStream(o);
				d = (Chunk) s.readObject();
				List = d.getList();
				int i = 0;
				String aux="";
				for(String list:List){
					aux=list+"                                ";

					lista+=aux.substring(0, 25)+"código del libro:" + i + "\n";
					i++;
				}
				textArea.setText(lista);
				btnCargarLibros.setEnabled(false);
				btnCargarUnLibro.setEnabled(true);
				btnDescargarUnLibro.setEnabled(true);


			}
		});
		btnCargarLibros.setBounds(165, 215, 114, 23);
		contentPane.add(btnCargarLibros);










	}
}

