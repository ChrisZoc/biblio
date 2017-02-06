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
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.File;
import java.io.FileReader;
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

public class Client extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;

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
		setBounds(100, 100, 450, 391);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

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
					Socket soc = new Socket("localhost",60020);	
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
					
					
				}
			});
		btnCargarLibros.setBounds(165, 215, 114, 23);
		contentPane.add(btnCargarLibros);
		
		textField = new JTextField();
		textField.setBounds(116, 309, 86, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblCdigoDeLibro = new JLabel("C\u00F3digo de Libro");
		lblCdigoDeLibro.setBounds(20, 312, 86, 14);
		contentPane.add(lblCdigoDeLibro);
		
		JButton btnDescargarUnLibro = new JButton("Descargar un libro");
		btnDescargarUnLibro.setBounds(38, 265, 145, 23);
		contentPane.add(btnDescargarUnLibro);
		
		JButton btnCargarUnLibro = new JButton("Cargar un libro");
		btnCargarUnLibro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String userhome = System.getProperty("user.home");
				JFileChooser fc=new JFileChooser(userhome +"\\Desktop");
				fc.setPreferredSize(new Dimension(800, 900));
				int seleccion=fc.showOpenDialog(contentPane);
				if(seleccion==JFileChooser.APPROVE_OPTION){

					File fichero=fc.getSelectedFile();
					textField_1.setText(fichero.getName());
				}
			}
		});
		btnCargarUnLibro.setBounds(267, 265, 137, 23);
		contentPane.add(btnCargarUnLibro);
		
		textField_1 = new JTextField();
		textField_1.setBounds(267, 309, 137, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);
		
		
		 
	}
	}

