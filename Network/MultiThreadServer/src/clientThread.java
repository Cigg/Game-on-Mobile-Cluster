import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class clientThread extends Thread{
	private String clientName = null;
	private PrintWriter out;
	private BufferedReader in;
	private Socket clientSocket = null;
	private final clientThread[] threads;
	private int maxClientCount;
	private boolean running = false;
	
	private String data;
	private float posX = 0;
	private float posY = 0;
	private float speedX = 0;
	private float speedY = 0;
	
	
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
				if(thread[i] != null){
				if(message != null){
					
					//System.out.println("Mobile says " + i +": "+ message);
					if(!message.isEmpty()){
					data = message;
					String[] parts = data.split(" ");
					posX = Float.parseFloat(parts[0]);
					posY = Float.parseFloat(parts[1]);
					speedX = Float.parseFloat(parts[2]);
					speedY = Float.parseFloat(parts[3]);
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
		System.out.println(posX + " " + posY + " " + speedX + " " + speedY);
		posX += speedX;
		posY += speedY;
		sendMessage(posX + " " + posY + " " + speedX + " " + speedY);
	}
	
	public void seeMessage(){
		System.out.println(posX + " " + posY + " " + speedX + " " + speedY);
	}
	
}//END OF clientThread
