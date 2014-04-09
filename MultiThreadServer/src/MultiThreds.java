import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.jbox2d.common.Vec2;


/**
 * Main loop for the server.
 *
 */

public class MultiThreds {
	private static ServerSocket serverSocket = null;
	private static Socket clientSocket = null;
	
	public static PhysicsWorld physicsWorld;
	
	private static int ballCount = 0;
	private static final int maxClientCount = 10;
	private static final ClientThread[] threads = new ClientThread[maxClientCount];
	private static final UpdateLoop updateLoop = new UpdateLoop(threads, maxClientCount);
	
	volatile static DeviceManager deviceManager;
	
	public static void main(String args[]) {
		int portNumber = 4444;
		if(args.length < 1) {
			System.out.println("Now using portnumber=" + portNumber);
		}else {
			portNumber = Integer.valueOf(args[0]).intValue();
		}
		
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println(e);
		}
		
		//updateLoop.start();
		deviceManager = new DeviceManager();
		
		final float tickRate = 20;
   
	   // Initiate Physics
	   physicsWorld = new PhysicsWorld();
	   physicsWorld.create(new Vec2(0.0f, 0.0f)); //no gravity
		
	   Thread update = new Thread() {
		    public void run() {
		    	
		    	float timeBegin, timeEnd = 0, timeDelta = 1 / tickRate, timeDelay;
		    	while(true) {
		    		timeBegin = System.nanoTime();
		    		
		    		// Update physics
		    		physicsWorld.update(timeDelta);
	    			
	    			// Update ballz	
		    		if(threads[0] != null) {
		    			for(Ball ball : threads[0].balls) {
		    				//ball.printInfo();
		    				ball.update(timeDelta);
		    				if(ball.isDead()) {
		    					threads[0].balls.remove(ball);
		    				}
		    			}		
		    			
		        		// Send data
		    			for(ClientThread thread : threads) {
		    				if (thread != null) {
			    				while(deviceManager.hasMessagesToSend(thread.getIp())) {
			    					byte[] data = deviceManager.getNextMessage(thread.getIp());
			    					if(data != null) {
			    						
			    						ByteBuffer test = ByteBuffer.wrap(data);
			    						//System.out.println("SEND to " + thread.getIp() + ": " + test.getShort() + "  " + test.getShort());
			    						
			    						thread.sendData(data);
			    					}
			    				}
		    				}
		    			}
		    			
			    		// Send ballz
			    		for(ClientThread thread : threads) {
			    			if (thread != null) {
			    				
			    				byte[] arr = new byte[1024];
			    				ByteBuffer buffer;
			    				buffer = ByteBuffer.wrap(arr);
			    				buffer.clear();
			    				
			    				buffer.putShort((short) GLOBAL_STATE__.ADD_BALLS.ordinal());	// State: Add balls
			    				buffer.putShort((short) 0);										// nBalls, byte 2 och 3
			    				
			    				short nBalls = 0;
			    				
			    				for(Ball ball : thread.balls) {
			    			
			    					if(buffer.limit() - buffer.position() >= 5*4) {
			    					
				    					float xG = ball.getXPos();
					    				float yG = ball.getYPos();
					    				
					    				if(deviceManager.isOnDevice(thread.getIp(), xG, yG)) {
					    					float xVelG = ball.getXVel();
						    				float yVelG = ball.getYVel();
						    				
						    				float xPosL = deviceManager.globalToLocalX(thread.getIp(), xG, yG);
						    				float yPosL = deviceManager.globalToLocalY(thread.getIp(), xG, yG);
						    				
						    				float xVelL = deviceManager.globalToLocalVelX(thread.getIp(), xVelG, yVelG);
						    				float yVelL = deviceManager.globalToLocalVelY(thread.getIp(), xVelG, yVelG);
						    				
						    				buffer.putInt(ball.id);
						    				buffer.putFloat(xPosL);
						    				buffer.putFloat(yPosL);
						    				
						    				buffer.putFloat(xVelL);
						    				buffer.putFloat(yVelL);
						    				
						    				//buffer.putFloat((float) (xVelL*Math.pow(10, 9)));
						    				//buffer.putFloat((float) -(yVelL*Math.pow(10, 9)));
						    				
						    				nBalls ++;
					    					
					    				}
				    				
			    					} else {
			    						buffer.position(2);
					    				buffer.putShort(nBalls);
					    				thread.sendData(buffer.array());
					    				
					    				nBalls = 0;
					    				buffer.clear();
					    				
					    				buffer.putShort((short)5);			// State: Add balls
					    				buffer.putShort((short)0);			// nBalls, byte 2 och 3
			    					}
			    					
			    				}
			    				
			    				buffer.position(2);
			    				buffer.putShort(nBalls);
			    				thread.sendData(buffer.array());
			
			    			}
			    		}
			    				    		
			    		
		    		}
		    		
		    		timeDelay = Math.max(0, (float)((1 / tickRate)*Math.pow(10, 9)) - (System.nanoTime() - timeBegin));
		 
		    		try {
						Thread.sleep( (long) (timeDelay * Math.pow(10, -6)));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		timeEnd = System.nanoTime();
		    		timeDelta = timeEnd - timeBegin;
		    		//System.out.println(timeDelta*Math.pow(10, -9));
		    	}
		    }
		};
		
		update.start();
		
		while(true) {
			try {
				clientSocket = serverSocket.accept();
				int i = 0;
				for(i=0; i<maxClientCount; i++) {
					if(threads[i] == null) {
						(threads[i] = new ClientThread(clientSocket.getInetAddress().toString(), clientSocket,threads,updateLoop, deviceManager)).start();
						break;
					}
				}
				if(i == maxClientCount) {
					PrintStream os = new PrintStream(clientSocket.getOutputStream());
					os.println("Server too busy. Try later");
					os.close();
					clientSocket.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}//End of Main
	
	public static int getBallCount() {
		return ballCount;
	}
	
	public static void incrementBallCount() {
		ballCount++;
	}
	
} // End of MultiThreds
