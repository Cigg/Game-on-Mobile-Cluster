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

import org.jbox2d.common.Vec2;


/**
 * One clientThread for one device.
 * Adds and sends balls the balls.
 *
 */

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
	
	private clientThread[] thread;
	private UpdateLoop updateLoop;
	GlobalCoords globalCoords;

	
	private volatile static LOCAL_STATE__ internalState;
	private volatile static float time1;
	
	private String ip;
	private DeviceManager deviceManager;
	private OutputStream dout;
	
	static final int MAX_LIFETIME = 15;
	static final float MAX_POSITION_X = (float) (100 / 2.5);
	static final float MAX_POSITION_Y = MAX_POSITION_X;
	
	public class Ballz {
				
		float xPos, yPos;
		float xVel, yVel;
		float  mass, radious, lifeTime;
		int id;
		
		public Ballz(int id,float xPos, float yPos, float xVel, float yVel) {
			MultiThreds.incrementBallCount();
			this.id = id++;
			this.xPos = xPos;
			this.yPos = yPos;
			this.xVel = xVel;
			this.yVel = yVel;
			this.lifeTime = 0;
			
			// old
			// addBall(float x, float y, Object data, float density, float radius, float bounce, float friction)
			
			MultiThreds.physicsWorld.addBall(xPos, yPos, xVel, yVel,  this.id, 0.03f, 0.2f, 0.8f, 0.3f);
			
			System.out.println("Added ballz: " + xPos + ", " + yPos + "    " + xVel + ", " + yVel); // * Math.pow(10, 9) * 2.5
		}
		
		public void update(float deltaTime) {
			// Get pos from PhysicsWorld. Physics handles position and velocity
			Vec2 position = MultiThreds.physicsWorld.getPositionFromId(id);
			this.xPos = position.x;
			this.yPos = position.y;
			
			// Old way
			//this.xPos += this.xVel * deltaTime;
			//this.yPos += this.yVel * deltaTime;
			
			this.lifeTime += deltaTime;
			//System.out.println("Life: " + this.lifeTime * Math.pow(10, -9));
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
	
	/**
	 * Get the ip of the client
	 *
	 */
	public String getIp() {
		return this.ip;
	}
	
	/**
	 * Starts one clientThread
	 * 
	 * @param ip			 	The ip adress for the client
	 * @param clientSocket	 	The socket on the server
	 * @param threads			An array with all clientThreads
	 * @param updateLoop		An update thread (not used at this moment)
	 * @param deviceManager		Stores device information
	 */
	public clientThread(String ip, Socket clientSocket, clientThread[] threads, UpdateLoop updateLoop, DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
		this.clientSocket = clientSocket;
		this.threads = threads;
		this.updateLoop = updateLoop;
		maxClientCount = threads.length;
		
		this.ip = ip;
		internalState = LOCAL_STATE__.MAPPING_STEP1;
	}
	
	/**
	 * @deprecated replaced by sendData
	 * {@link #sendData(byte[])}
	 */
	public void sendMessage(String message) {
		if(out != null && !out.checkError()){
			//out.println(message);
			//out.flush();
		}
	}
	
	
	/**
	 * Send data to device
	 * @param buffer Array of bytes to send to device 
	 */
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
				ByteBuffer headerBuffer = ByteBuffer.allocate(8);
				headerBuffer.clear();
				headerBuffer.putInt(buffer.length);
				headerBuffer.putFloat(System.nanoTime());
				
				if(dout != null && headerBuffer != null) {
					dout.write(headerBuffer.array());
					dout.write(buffer);
					dout.flush();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Starts the thread and reads new data from device 
	 */
	public void run() {
		running = true;
		int maxClientsCount = this.maxClientCount;
		thread = this.threads;
		
		/*
        for (int j = 0; j < maxClientsCount; j++) {
            if (thread[j] != null) {
            	globalCoords = new GlobalCoords(800*j,800*(j+1),0,1250);
            	System.out.println(globalCoords.minX + " " + globalCoords.maxX + " " + globalCoords.minY + " " + globalCoords.maxY);
            }
          }
        */
		
		try{
			dout = clientSocket.getOutputStream();
			clientSocket.setTcpNoDelay(true);
			/*
			out = 	new PrintWriter(
					new BufferedWriter(
					new OutputStreamWriter(clientSocket.getOutputStream())),true);
			in = 	new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
			*/

			while(running) {

				// Onödig loop?
				for(int i=0; i < maxClientsCount; i++){
					if(thread[i] != null && thread[i] == this){
						
						byte[] headerBuffer = new byte[8];
						clientSocket.getInputStream().read(headerBuffer);	
						float reciveTime = System.nanoTime();
						
						ByteBuffer header = ByteBuffer.wrap(headerBuffer);
						
						int length = header.getInt();
					    length = Math.max(0, length);
					    
					    float sendTime = header.getFloat();					    
					    
					    if(length > 0) {
							byte[] bytes = new byte[length];
							clientSocket.getInputStream().read(bytes);
							ByteBuffer buffer = ByteBuffer.wrap(bytes);
				
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
			        		
			        		case SYNCHRONIZE_DEVICE:
			        		{		        			
			        			
			        			ByteBuffer sendBuffer = ByteBuffer.allocate(1*2 + 2*4);
			        			sendBuffer.clear();
			    				
			        			sendBuffer.putShort((short) GLOBAL_STATE__.SYNCHRONIZE_DEVICE.ordinal()); 	// State: SYNCHRONZE_DEVICE
			    				
			        			float delta1 = reciveTime - sendTime;
			        			System.out.println("delta1 = " + delta1);
			        			sendBuffer.putFloat(sendTime);												// t1
			        			sendBuffer.putFloat(reciveTime);
			        				
			    				
			    				sendData(sendBuffer.array());
			    				System.out.println("CLOCK === " + System.nanoTime() * Math.pow(10, -9));
			        		}
			        		break;
			        		
		        			case ADD_DEVICE:
		        			{
		        				short type = buffer.getShort();
		        				int xDPI = buffer.getInt();
		        				int yDPI = buffer.getInt();
		        				int deviceResX = buffer.getInt();
		        				int deviceResY = buffer.getInt();
		        				
		        				// TODO: Add type of device.
		        				deviceManager.addDevice(ip, type, xDPI, yDPI, deviceResX, deviceResY);
		        			}
		        			break;
		        			
		        			case MAP_DEVICE:
		        			{
				        		if(deviceManager.needsMapping(ip)) {
				        			System.out.println("MAPPING_STEP2");
				        			
				        			float time2 = System.nanoTime();
				        			float deltaTime = time2 - time1;
				        		
				        			float x11 = buffer.getFloat();
						        	float y11 = buffer.getFloat();
				        			float x22 = buffer.getFloat();	
				        			float y22 = buffer.getFloat();	
				        			float t1 = buffer.getFloat();	
				        			
				        		//	float time2 = buffer.getFloat();
				        		//	float deltaTime = time2 - time1;
				        			
				        			System.out.println("STEP2: " + ip + ", " + x11 + ", " + y11 + ", " + x22 + ", " + y22 + ", " + t1 + ", " + deltaTime);
				        			
				        			deviceManager.devicePointMappingStep2(ip, x11, y11, x22, y22, t1, deltaTime);
				        		
				        			time1 = time2; // För multirow
				        			System.out.println("MAPPING_STEP2 DONE");
				        			
				        			deviceManager.setNeedsMapping(ip, false);
				        		} else {
				        			System.out.println("MAPPING_STEP1");
				        			
				        			time1 = System.nanoTime();
				        			
				        			float x1 = buffer.getFloat();
						        	float y1 = buffer.getFloat();	
				        			float x2 = buffer.getFloat();	
				        			float y2 = buffer.getFloat();	
				        			float t  = buffer.getFloat();	
				        		//	time1 = buffer.getFloat() + t;
				  
				        			System.out.println("STEP1: " + ip + ", " + x1 + ", " + y1 + ", " + x2 + ", " + y2 + ", " + t);
				        			
				        			deviceManager.devicePointMappingStep1(ip, x1, y1, x2, y2, t);				        			
				        			System.out.println("MAPPING_STEP1 DONE");
				        		}
		        			}
				        	break;
				        	
				        	
		        			case RUN_DEVICE:
		        			{
		        				System.out.println("RUN DEVICE: " + System.nanoTime() * Math.pow(10, -9));
		        				float x1 = buffer.getFloat();
					        	float y1 = buffer.getFloat();	
					        	float x2 = buffer.getFloat();
					        	float y2 = buffer.getFloat();
			        			float t  = buffer.getFloat();	

			        			//float xVel = deviceManager.computeVelX(ip, x1, x2, t);
			        			//float yVel = deviceManager.computeVelY(ip, y1, y2, t);	
			        			
			        			float xVel = deviceManager.computeVelocityX(ip, x1, y1, x2, y2, t);
			        			float yVel = deviceManager.computeVelocityY(ip, x1, y1, x2, y2, t);
			        			
			        			
			        		
			        			System.out.println("GLOBAL xVel = " + xVel);
			        			System.out.println("GLOBAL yVel = " + yVel);

			        			float xG = deviceManager.localToGlobalX(ip, x2, y2);
			        			float yG = deviceManager.localToGlobalY(ip, x2, y2);	
			        			
			        			 synchronized (this) {
			        				 ballz.add(new Ballz(MultiThreds.getBallCount(),xG, yG, xVel, yVel));
			        				 
			        				 // Is this needed?
//			        				 for (int j = 0; j < maxClientsCount; j++) {
//							              if (threads[j] != null) {
//							                threads[j].ballCount = ballCount;
//							              }
//							            }
							        }
		        			}
		        			break;  
		        			
		        
		        			// Set server state
		        			case SET_STATE:
		        			{
		        				
		        				short newState = buffer.getShort();
		        				System.out.println("@ SERVER: SET STATE: " + newState);
		        				
		        				try {
		        					GLOBAL_STATE__ toState = GLOBAL_STATE__.values()[newState];
		        					
		        					switch(toState) {
		        						case MAP_MAIN:
		        						{
		        							//deviceManager.setMappingAtMainDevice();
		        							deviceManager.setMappingAtAllDevices();
		        							deviceManager.setNeedsMapping(ip, true);
		        						}
		        						break;
		        					}
								} catch(Exception e) {
									// Do nothing
									System.out.println("ERROR: Invalid state: " + newState);
								}
		        			}
		        			break;

		        			case REG:
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
	

}//END OF clientThread
