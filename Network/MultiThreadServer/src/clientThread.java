import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class clientThread extends Thread{
	private PrintWriter out;
	private BufferedReader in;
	private Socket clientSocket = null;
	private final clientThread[] threads;
	private int maxClientCount;
	private boolean running = false;
	
	private String data;
	private float posX = 0;
	private float posY = 0;
	private float deltaX = 0;
	private float deltaY = 0;
	
	
	public clientThread(Socket clientSocket, clientThread[] threads) {
		this.clientSocket= clientSocket;
		this.threads = threads;
		maxClientCount = threads.length;
	}
	
	public void sendMessage(String message) {
		if(out != null && !out.checkError()){
			out.println(message);
			out.flush();
		}
	}
	
	public void run() {
		running = true;
		int maxClientsCount = this.maxClientCount;
		clientThread[] thread = this.threads;
		try{
			out = 	new PrintWriter(
					new BufferedWriter(
					new OutputStreamWriter(clientSocket.getOutputStream())),true);
			in = 	new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
			
			while(running) {
				String message = in.readLine();
				for(int i=0; i < maxClientsCount; i++){
					if(thread[i] != null && thread[i] == this){
						if(message != null){
							if(!message.isEmpty()){
								data = message;
								String[] parts = data.split(" ");
								posX = Float.parseFloat(parts[0]);
								posY = Float.parseFloat(parts[1]);
								deltaX = Float.parseFloat(parts[2]);
								deltaY = Float.parseFloat(parts[3]);
							}
						}
					}
				}
				update();
			}
			clientSocket.close();
		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
		}
	}
	
	private void update() {
		System.out.println(posX + " " + posY + " " + deltaX + " " + deltaY);
		posX += deltaX;
		posY += deltaY;
		sendMessage(posX + " " + posY + " " + deltaX + " " + deltaY);
	}
}//END OF clientThread
