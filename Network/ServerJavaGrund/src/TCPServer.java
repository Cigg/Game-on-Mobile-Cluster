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
						sendMessage(message);
					}
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
} //END PF TCPSERVER
