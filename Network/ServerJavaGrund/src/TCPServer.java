import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;


public class TCPServer extends Thread{
	public static final int SERVERPORT = 4444;
	private boolean running = false;
	private PrintWriter out;
	private BufferedReader in;
	private String data;
	private float posX = 0;
	private float posY = 0;
	private float speedX = 0;
	private float speedY = 0;
	
	public static void main(String[] args) {
		ServerBoard frame = new ServerBoard();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	public void sendMessage(String message) {
		if(out != null && !out.checkError()) {
			out.println(message);
			out.flush();
		}
	}
	
	@Override
	public void run() {
		running = true;
		try {
			System.out.println("Connection...");
			ServerSocket serverSocket = new ServerSocket(SERVERPORT);
			Socket client = serverSocket.accept();
			System.out.println(client.getInetAddress().toString());
			System.out.println("Receiving...");
			
			try{
				out = 	new PrintWriter(
						new BufferedWriter(
						new OutputStreamWriter(client.getOutputStream())),true);
				in = 	new BufferedReader(
						new InputStreamReader(client.getInputStream()));
				
				while(running) {
					String message = in.readLine();
					if(message != null) {
						System.out.println("Mobile says: " + message);
						data = message;
						String[] parts = data.split(" ");
						posX = Float.parseFloat(parts[0]);
						posY = Float.parseFloat(parts[1]);
						speedX = Float.parseFloat(parts[2]);
						speedY = Float.parseFloat(parts[3]);
					}
					update();
				}
			} catch (Exception e) {
				System.out.println("Error");
				e.printStackTrace();
			} finally {
				client.close();
			}
		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
		}
	}
	
	private void update() {
		sendMessage(posX + " " + posY);
	}
} //END PF TCPSERVER
