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
	
	private float posX = 0;
	private float posY = 0;
	private float deltaX = 0;
	private float deltaY = 0;
	private int ballCount;
	
	private clientThread[] thread;
	private UpdateLoop updateLoop;
	GlobalCoords globalCoords;

	
	public clientThread(Socket clientSocket, clientThread[] threads, UpdateLoop updateLoop) {
		this.clientSocket= clientSocket;
		this.threads = threads;
		this.updateLoop = updateLoop;
		maxClientCount = threads.length;
		ballCount = 0;
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
		thread = this.threads;
		
        for (int j = 0; j < maxClientsCount; j++) {
            if (thread[j] != null) {
            	globalCoords = new GlobalCoords(0,1250,800*j,800*(j+1));
            	System.out.println(globalCoords.minX + " " + globalCoords.maxX + " " + globalCoords.minY + " " + globalCoords.maxY);
            }
          }
		
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
								System.out.println();
								System.out.println(message);
								String[] parts = message.split(" ");
								posX = Float.parseFloat(parts[0]);
								posY = Float.parseFloat(parts[1]);
								deltaX = Float.parseFloat(parts[2]);
								deltaY = Float.parseFloat(parts[3]);
								toGlobal();
								ballCount++;
						        synchronized (this) {
						        	if(updateLoop != null) {
						        		updateLoop.addBall(new Ball(i,ballCount,posX,posY,deltaX,deltaY));
						        	}
						            for (int j = 0; j < maxClientsCount; j++) {
						              if (threads[j] != null) {
						                threads[j].ballCount = ballCount;
						              }
						            }
						        }
							}
						}
					}
				}
			} 
			clientSocket.close();
		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
		}
	}
	
	private void toGlobal(){
		posX += globalCoords.minX;
		posY += globalCoords.minY;
	}
}//END OF clientThread