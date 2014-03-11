import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;


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

	
	private volatile static LOCAL_STATE__ internalState;
	private volatile static float time1;
	
	private String ip;
	private DeviceManager deviceManager;
	private OutputStream dout;
	
	static final int MAX_LIFETIME = 5;
	static final float MAX_POSITION_X = (float) (100 / 2.5);
	static final float MAX_POSITION_Y = MAX_POSITION_X;
	
	public class Ballz {
				
		float xPos, yPos;
		float xVel, yVel;
		float  mass, radious, lifeTime;
		
		public Ballz(float xPos, float yPos, float xVel, float yVel) {
			this.xPos = xPos;
			this.yPos = yPos;
			this.xVel = xVel;
			this.yVel = yVel;
			this.lifeTime = 0;
			System.out.println("Added ballz: " + xPos + ", " + yPos + "    " + xVel + ", " + yVel); // * Math.pow(10, 9) * 2.5
		}
		
		
		public void update(float deltaTime) {
			this.xPos += this.xVel * deltaTime;
			this.yPos += this.yVel * deltaTime;
			this.lifeTime += deltaTime;
			System.out.println("Life: " + this.lifeTime * Math.pow(10, -9));
		}
		
		public boolean isDead() {
			if((this.lifeTime * Math.pow(10, -9)) > MAX_LIFETIME || outOfBounds()) {
				return true;
			}
			return false;
		}
		
		public boolean outOfBounds() {
			if(Math.abs(this.xPos) < MAX_POSITION_X &&  Math.abs(this.yPos) < MAX_POSITION_Y) {
				return false;
			}
			return true;
		}
		
		
		public void printInfo() {
			System.out.println("Ball: " + this.xPos + ", " + this.yPos + "    " + this.xVel* Math.pow(10, 9) * 2.5 + ", " + this.yVel* Math.pow(10, 9) * 2.5);
		}
		
		public float getXPos() {
			return this.xPos;
		}
		
		public float getYPos() {
			return this.yPos;
		}
		
		public float getXVel() {
			return this.xVel;
		}
		
		public float getYVel() {
			return this.yVel;
		}
	}
	
	public volatile static LinkedBlockingQueue<Ballz> ballz = new LinkedBlockingQueue <Ballz>();
	
	public String getIp() {
		return this.ip;
	}
	
	public clientThread(String ip, Socket clientSocket, clientThread[] threads, UpdateLoop updateLoop, DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
		this.clientSocket= clientSocket;
		this.threads = threads;
		this.updateLoop = updateLoop;
		maxClientCount = threads.length;
		ballCount = 0;
		
		this.ip = ip;
		internalState = LOCAL_STATE__.MAPPING_STEP1;
	}
	
	public void sendMessage(String message) {
		if(out != null && !out.checkError()){
			//out.println(message);
			//out.flush();
		}
	}
	
	public void sendData(byte[] buffer) {
		if(buffer.length > 0) {	
			try {
				/*
				Log.d("AppWrite", "Writes: " + buffer.toString());
				byte[] arr = new byte[500];
				ByteBuffer bf = ByteBuffer.wrap(arr);
				bf.putShort((short)16);
				bf.putFloat((float) 17.17);
				bf.putFloat((float) 18.18);
				bf.putFloat((float) 19.19);
				bf.putFloat((float) 2000.20);
				dout.write(bf.array());
				*/
				ByteBuffer bufferLength = ByteBuffer.allocate(4);
				bufferLength.putInt(buffer.length);
				
				dout.write(bufferLength.array());
				dout.write(buffer);
				dout.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void run() {
		running = true;
		int maxClientsCount = this.maxClientCount;
		thread = this.threads;
		
		/*
        for (int j = 0; j < maxClientsCount; j++) {
            if (thread[j] != null) {
            	globalCoords = new GlobalCoords(0,1250,800*j,800*(j+1));
            	System.out.println(globalCoords.minX + " " + globalCoords.maxX + " " + globalCoords.minY + " " + globalCoords.maxY);
            }
          }
        */
		
		try{
			dout = clientSocket.getOutputStream();
			/*
			out = 	new PrintWriter(
					new BufferedWriter(
					new OutputStreamWriter(clientSocket.getOutputStream())),true);
			in = 	new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
			*/

			while(running) {

				for(int i=0; i < maxClientsCount; i++){
					if(thread[i] != null && thread[i] == this){
						
						// TODO: Fixa adaptive length
						byte[] bytesLength = new byte[4];
						clientSocket.getInputStream().read(bytesLength);	
						ByteBuffer bufferLength = ByteBuffer.wrap(bytesLength);
						
						int length = bufferLength.getInt();
					    length = Math.max(0, length);

						if(length > 0) {
							byte[] bytes = new byte[length];
							clientSocket.getInputStream().read(bytes);
							ByteBuffer buffer = ByteBuffer.wrap(bytes);
				
							
							/*
							byte[] bytes = new byte[1024];
							clientSocket.getInputStream().read(bytes);
				     		ByteBuffer buffer = ByteBuffer.wrap(bytes);
				     		*/
							
				     		/*
							System.out.println("+++++++++++++++++++++++++");
							System.out.println("Message: " + buffer.getShort() );
							System.out.println("Message: " + buffer.getFloat() );
							System.out.println("Message: " + buffer.getFloat() );
							System.out.println("Message: " + buffer.getFloat() );
							System.out.println("Message: " + buffer.getFloat() );
							System.out.println("+++++++++++++++++++++++++");
							*/
		
								
							System.out.println("In thread: " + i);
							short state = buffer.getShort();
							GLOBAL_STATE__ actualState;
							
							try {
								actualState = GLOBAL_STATE__.values()[state];
							} catch(Exception e) {
								actualState = GLOBAL_STATE__.REG;
							}
							
			        		String ip = clientSocket.getInetAddress().toString();
	
			        		switch(actualState) {
			        			case ADD_DEVICE:
			        				
			        				int xDPI = buffer.getInt();
			        				int yDPI = buffer.getInt();
			        				int deviceResX = buffer.getInt();
			        				int deviceResY = buffer.getInt();
			        				
			        				deviceManager.addDevice(ip, xDPI, yDPI, deviceResX, deviceResY);
			        			break;
			        			
			        			case MAP_DEVICE:
			        				
					        		switch(internalState) {
						        		case MAPPING_STEP1:
						        			System.out.println("MAPPING_STEP1");
						        			
						        			time1 = System.nanoTime();
						        			
						        			float x1 = buffer.getFloat();
								        	float y1 = buffer.getFloat();	
						        			float x2 = buffer.getFloat();	
						        			float y2 = buffer.getFloat();	
						        			float t  = buffer.getFloat();	
						  
						        			System.out.println("STEP1: " + ip + ", " + x1 + ", " + y1 + ", " + x2 + ", " + y2 + ", " + t);
						        			
						        			deviceManager.devicePointMappingStep1(ip, x1, y1, x2, y2, t);
						        			internalState = LOCAL_STATE__.MAPPING_STEP2;
						        			
						        			System.out.println("MAPPING_STEP1 DONE");
						        		break;
						        		
						        		case MAPPING_STEP2:
						        			System.out.println("MAPPING_STEP2");
						        
						        			float time2 = System.nanoTime();
						        			float deltaTime = time2 - time1;
						        		
						        			float x11 = buffer.getFloat();
								        	float y11 = buffer.getFloat();
						        			float x22 = buffer.getFloat();	
						        			float y22 = buffer.getFloat();	
						        			float t1 = buffer.getFloat();	
						        			
						        			System.out.println("STEP2: " + ip + ", " + x11 + ", " + y11 + ", " + x22 + ", " + y22 + ", " + t1 + ", " + deltaTime);
						        			
						        			deviceManager.devicePointMappingStep2(ip, x11, y11, x22, y22, t1, deltaTime);
						        			internalState = LOCAL_STATE__.MAPPING_STEP1;
						        			
						        			System.out.println("MAPPING_STEP2 DONE");
							        	break;
						        		
						        		default:
						        		break;
					        		}
					        	break;
					        	
					        	
			        			case RUN_DEVICE:
			        				float x1 = buffer.getFloat();
						        	float y1 = buffer.getFloat();	
						        	float x2 = buffer.getFloat();
						        	float y2 = buffer.getFloat();
				        			float t  = buffer.getFloat();	
				        		
				        			//float xVel = deviceManager.computeVelX(ip, x1, x2, t);
				        			//float yVel = deviceManager.computeVelY(ip, y1, y2, t);	
				        			
				        			float xVel = deviceManager.computeVelocityX(ip, x1, y1, x2, y2, t);
				        			float yVel = deviceManager.computeVelocityY(ip, x1, y1, x2, y2, t);
				        			
				        			
				        			System.out.println("xVel = " + xVel * Math.pow(10, 9) * 2.5);
				        			System.out.println("yVel = " + yVel * Math.pow(10, 9) * 2.5);
				        			
				        			
				        			
				        			float xG = deviceManager.localToGlobalX(ip, x2, y2);
				        			float yG = deviceManager.localToGlobalY(ip, x2, y2);			        		
				        			
				        			ballz.add(new Ballz(xG, yG, xVel, yVel));
				        			
			        			break;    			
			        			
			  
	
				        		
			        			case REG:
				        			/*String message = buffer.array().toString();
				        			System.out.println("Message: " + message);
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
									*/
				        		break;
					        		
				        		
								default:
								break;
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
