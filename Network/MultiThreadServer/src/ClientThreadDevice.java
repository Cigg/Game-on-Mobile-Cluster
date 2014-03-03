import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientThreadDevice extends Thread{
	private String clientName = null;
	private PrintWriter out;
	private BufferedReader in;
	private Socket clientSocket = null;
	private final ClientThreadDevice[] threads;
	private int maxClientCount;
	private boolean running = false;
	
	private String data;
	private float posX = 0;
	private float posY = 0;
	private float speedX = 0;
	private float speedY = 0;
	
	Server server = new Server();
	
	public ClientThreadDevice(Socket clientSocket, ClientThreadDevice[] threads) {
		this.clientSocket= clientSocket;
		this.threads = threads;
		maxClientCount = threads.length;
	}
	
	public void sendMessage(String message) {
		if(out != null && !out.checkError()){
			out.println(message);
			System.out.println("Message sent: " + message);
			out.flush();
		}
	}
	
	public void run() {
		running = true;
		int maxClientsCount = this.maxClientCount;
		ClientThreadDevice[] thread = this.threads;
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
					server.recieveEvent(i + " " + message);

//					data = message;
//					String[] parts = data.split(" ");
//					posX = Float.parseFloat(parts[0]);
//					posY = Float.parseFloat(parts[1]);
//					speedX = Float.parseFloat(parts[2]);
//					speedY = Float.parseFloat(parts[3]);
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
//		posX += speedX;
//		posY += speedY;
		if(server.deviceManager.devices.size() > 1){
			String msg = server.screenTest();
			//System.out.println(msg);
			sendMessage(msg);
		}
	}
	
	public void seeMessage(){
//		System.out.println(posX + " " + posY + " " + speedX + " " + speedY);
	}
	
}//END OF clientThread
