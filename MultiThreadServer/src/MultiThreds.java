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
		
	   final float tickRate = 1;
		
		Thread update = new Thread() {
		    public void run() {
		    	
		    	float timeBegin, timeEnd, timeDelta = 1 / tickRate, timeDelay;
		    	while(true) {
		    		System.out.println("Update");
		    		timeBegin = System.nanoTime();
		    		
		    		
		    		if(threads[0] != null) {
		    			
		    			// Update ballz
		    			for(clientThread.Ballz ball : threads[0].ballz) {
		    				ball.printInfo();
		    				ball.update(timeDelta);
		    			}
		    			
		    			// Send ballz
		    			for(clientThread.Ballz ball : threads[0].ballz) {
		    			
		    				ByteBuffer buffer;
		    				buffer = ByteBuffer.allocate(1*2 + 4*4);
		    				buffer.clear();
		    				
		    				buffer.putShort((short)3);			// State: Add ball
		    				buffer.putFloat(ball.getXPos());
		    				buffer.putFloat(ball.getYPos());
		    				buffer.putFloat(ball.getXVel());
		    				buffer.putFloat(ball.getYVel());
		    				
		    				threads[0].sendData(buffer.array());
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
						(threads[i] = new clientThread(clientSocket,threads,updateLoop, deviceManager)).start();
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
