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
	private int id;
	private int ballCount;
	
	private class Ball {
		public int id;
		public float posX;
		public float posY;
		public float speedX;
		public float speedY;
		
		public Ball(int id, float posX, float posY, float speedX, float speedY){
			this.id = id;
			this.posX = posX;
			this.posY = posY;
			this.speedX = speedX;
			this.speedY = speedY;
		}
		
		public void updateBall(float posX, float posY, float speedX, float speedY) {
			this.posX = posX;
			this.posY = posY;
			this.speedX = speedX;
			this.speedY = speedY;
		}
	}
	
	private Ball[] balls;
	
	
	public clientThread(Socket clientSocket, clientThread[] threads) {
		this.clientSocket= clientSocket;
		this.threads = threads;
		maxClientCount = threads.length;
		balls = new Ball[10];
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
								//System.out.println(data);
								String[] parts = data.split(" ");
								id = Integer.parseInt(parts[0]);
								posX = Float.parseFloat(parts[1]);
								posY = Float.parseFloat(parts[2]);
								deltaX = Float.parseFloat(parts[3]);
								deltaY = Float.parseFloat(parts[4]);
								if(ballCount < id){
									balls[ballCount] = new Ball(id,posX,posY,deltaX,deltaY);
									ballCount++;
								}else {
									balls[id-1].updateBall(posX, posY, deltaX, deltaY);
								}
							}
						}
					}
					update();
				}
			}
			clientSocket.close();
		} catch (Exception e) {
			System.out.println("Error");
			e.printStackTrace();
		}
	}
	
	private void update() {
		String data1 = "";
		float x,y;
		for(int i = 0; i < ballCount; i++) {
			System.out.println(balls[i].speedX + " " + balls[i].speedY);
			balls[i].posX += balls[i].speedX*0.5;
			balls[i].posY += balls[i].speedY*0.5;
			data1 += i + " " + balls[i].posX + " " + balls[i].posY + " n ";
		}
		//System.out.println(data1);
		sendMessage(data1);
	}
}//END OF clientThread
