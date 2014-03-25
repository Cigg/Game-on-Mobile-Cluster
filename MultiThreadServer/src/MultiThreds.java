import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;


public class MultiThreds {
	private static ServerSocket serverSocket = null;
	private static Socket clientSocket = null;
	
	private static final int maxClientCount = 10;
	private static final clientThread[] threads = new clientThread[maxClientCount];
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
		
	   final float tickRate = 500;
		
		Thread update = new Thread() {
		    public void run() {
		    	
		    	float timeBegin, timeEnd, timeDelta = 1 / tickRate, timeDelay;
		    	while(true) {
		    		//System.out.println("Update-----------------------------------------------");
		    		timeBegin = System.nanoTime();
		    		
	    			
	    			// Update ballz	
		    		if(threads[0] != null) {
		    			for(clientThread.Ballz ball : threads[0].ballz) {
		    				//ball.printInfo();
		    				ball.update(timeDelta);
		    				if(ball.isDead()) {
		    					threads[0].ballz.remove(ball);
		    				}
		    			}		    			
		    			
			    		// Send ballz
			    		for(clientThread thread : threads) {
			    			if (thread != null) {
			    				
			    				byte[] arr = new byte[1024];
			    				ByteBuffer buffer;
			    				buffer = ByteBuffer.wrap(arr);
			    				buffer.clear();
			    				
			    				buffer.putShort((short)5);			// State: Add balls
			    				buffer.putShort((short)0);			// nBalls, byte 2 och 3
			    				
			    				short nBalls = 0;
			    				
			    				for(clientThread.Ballz ball : thread.ballz) {
			    			
			    					if(buffer.capacity() < 16) {
			    						break;
			    					}
			    					
			    					float xG = ball.getXPos();
				    				float yG = ball.getYPos();
				    				
				    				
				    				if(deviceManager.isOnDevice(thread.getIp(), xG, yG)) {
				    					float xVelG = ball.getXVel();
					    				float yVelG = ball.getYVel();
					    				
					    				int xPosL = deviceManager.globalToLocalX(thread.getIp(), xG, yG);
					    				int yPosL = deviceManager.globalToLocalY(thread.getIp(), xG, yG);
					    				
					    				float xVelL = deviceManager.globalToLocalVelX(thread.getIp(), xVelG, yVelG);
					    				float yVelL = deviceManager.globalToLocalVelY(thread.getIp(), xVelG, yVelG);
					    				
					    				//System.out.println(xVelL + " " + yVelL);
					    				
					    				buffer.putInt(ball.id);
					    				buffer.putInt(xPosL);
					    				buffer.putInt(yPosL);
					    				buffer.putFloat((float) (xVelL*Math.pow(10, 9)));
					    				buffer.putFloat((float) -(yVelL*Math.pow(10, 9)));
					    				
					    				nBalls ++;
				    					
				    				}
				    				
				    				
				    				
				    				/*
			    					if(deviceManager.isOnDevice(thread.getIp(), xG, yG)) {
					    				ByteBuffer buffer;
					    				buffer = ByteBuffer.allocate(1*2 + 4*4);
					    				buffer.clear();
					    				
					    				buffer.putShort((short)3);			// State: Add ball
					    				
					    				
					    				float xVelG = ball.getXVel();
					    				float yVelG = ball.getYVel();
					    				
					    				int xPosL = deviceManager.globalToLocalX(thread.getIp(), xG, yG);
					    				int yPosL = deviceManager.globalToLocalY(thread.getIp(), xG, yG);
					    				
					    				float xVelL = deviceManager.globalToLocalVelX(thread.getIp(), xVelG, yVelG);
					    				float yVelL = deviceManager.globalToLocalVelY(thread.getIp(), xVelG, yVelG);
					    				
					    				buffer.putInt(xPosL);
					    				buffer.putInt(yPosL);
					    				buffer.putFloat(xVelL);
					    				buffer.putFloat(yVelL);
					    				
					    				thread.sendData(buffer.array());
			    					}
			    					*/
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
						(threads[i] = new clientThread(clientSocket.getInetAddress().toString(), clientSocket,threads,updateLoop, deviceManager)).start();
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
} // End of MultiThreds
