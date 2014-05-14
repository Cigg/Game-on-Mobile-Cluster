package src;

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
	
	static PhysicsWorld physicsWorld;
	
	
	private static final int maxClientCount = 10;
	private static final ClientThread[] threads = new ClientThread[maxClientCount];
	private static final int[] scores = new int[maxClientCount];
	private static final UpdateLoop updateLoop = new UpdateLoop(threads, maxClientCount);
	
	volatile static DeviceManager deviceManager;
	
	public static volatile SharedVariables sharedVariables;
	
	public ServerGraphics serverGraphics;
	
	public Thread update;
	public Thread deviceUpdate;
	
	public static PhysicsWorld getPhysicsWorld() {
			return physicsWorld;
	}
	
	
	public MultiThreds() {
		
		int portNumber = 4444;
		// Initiate Physics
		physicsWorld = new PhysicsWorld();
		physicsWorld.create(new Vec2(0.0f, 0.0f));
		
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println(e);
		}
		//updateLoop.start();
		deviceManager = new DeviceManager();
		serverGraphics = new ServerGraphics(deviceManager);
		
		final float tickRate = 128;
		
	
	   
		update = new Thread() {
		    public void run() {
		    	/*
		    	boolean allReady = false;
		    	short nPlayers = 0;
		    	while(!allReady) {
		    		allReady = true;
		    		for(ClientThread thread : threads) {
						if (thread != null) {
							nPlayers ++;
							if(thread.getIsReady() != 1 && !deviceManager.isMiddle(thread.getIp())) {
								allReady = false;
							}
						}
		    		}
		    		if(nPlayers <= 1 || !allReady) {
		    			allReady = false;
		    			nPlayers = 0;
		    			
		    			try {
							Thread.currentThread().sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		    		}
		    	}
		    	*/
		    	
		    	short nPlayers = 0;
		    	boolean isStarted = false;
		    	while(!isStarted) {
		    		nPlayers = 0;
		    		for(ClientThread thread : threads) {
						if (thread != null) {
							if(thread.getIsReady() == 1) {
								nPlayers ++;
								
								if(deviceManager.isMiddle(thread.getIp())) {
									isStarted = true;
									//nPlayers --;
								}
							}
						}
		    		}
		    		
		    		if(nPlayers <= 1) {
		    			isStarted = false;
		    		}
		    		
		    		if(!isStarted) {
			    		try {
							Thread.currentThread().sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		    		}
	    		}
		    	
		    	
				for(ClientThread thread : threads) {
					if (thread != null && thread.getIsReady() == 1) {
						ByteBuffer dataBuffer = ByteBuffer.allocate(2*2 + 0*4);
						dataBuffer.clear();
	    				short sendState = (short) GLOBAL_STATE__.START_GAME.ordinal();
						dataBuffer.putShort(sendState);
						dataBuffer.putShort(nPlayers);	
						
			    		int position = dataBuffer.position();
			    		byte[] sendBytes = new byte[position];
						System.arraycopy( dataBuffer.array(), 0, sendBytes, 0, position);
						
			    		thread.sendData(sendBytes);
			    		thread.clientInfo.addSentPackageItem(GLOBAL_STATE__.values()[sendState] + "   " + nPlayers);
					}
				}
				System.out.println("DONE");
				
				
		    	float timeBegin, timeEnd = 0, timeDelta = 1 / tickRate, timeDelay;
		    	while(true) {
		    		//System.out.println("Update-----------------------------------------------");
		    		//System.out.println("FPS: " + Math.pow(10, 9) / timeDelta);
		    		timeBegin = System.nanoTime();
		    		
		    		// Update physics
		    		physicsWorld.update(timeDelta);
		    		serverGraphics.update();
	    			// Update ballz	
		    		if(threads[0] != null) {
		    			for(ClientThread.Ballz ball : ClientThread.ballz) {
		    				ball.update(timeDelta);
		    				if(ball.shouldBeRemoved()) {
		    					ClientThread.ballz.remove(ball);
		    				} else if(ball.isDead()) {
		    					ball.setShouldBeRemoved(true);
		    				}
		    			}		
		    			
		        		// Send data
		    			for(ClientThread thread : threads) {
		    				if (thread != null) {
		    					// Send messages from other devices
			    				while(deviceManager.hasMessagesToSend(thread.getIp())) {
			    					byte[] data = deviceManager.getNextMessage(thread.getIp());
			    					if(data != null) {
			    						
			    						ByteBuffer test = ByteBuffer.wrap(data);
			    						System.out.println("SEND to " + thread.getIp() + ": " + test.getShort() + "  " + test.getShort());
					    				
			    						thread.sendData(data);
			    						thread.clientInfo.addSentPackageItem("SENDATA   TODO: FIX");
			    					}
			    				}
			    				
			    				// Send middleAngle to middle
			    				if(deviceManager.isMiddle(thread.getIp())) {
			    					ByteBuffer dataBuffer = ByteBuffer.allocate(1*2 + 1*4);
			    					dataBuffer.clear();
			    		    		
			    					dataBuffer.putShort((short) GLOBAL_STATE__.SET_MIDDLE_ANGLE.ordinal());
			    		    		
			    		    		float jointAngle = 0.0f;
				    				if(ClientThread.targetJoint != null){
				    					jointAngle =  ClientThread.targetJoint.getJointAngle();
				    				}
				    				
				    				dataBuffer.putFloat(jointAngle);	
				    				
				    	    		int position = dataBuffer.position();
				    	    		byte[] sendBytes = new byte[position];
				    				System.arraycopy( dataBuffer.array(), 0, sendBytes, 0, position);
			    		    		thread.sendData(sendBytes);
			    				}
			    				
		    				}
		    			}
		    			
		    			
		    			if(deviceManager.shouldUpdateScores()) {
	    					ByteBuffer dataBuffer = ByteBuffer.allocate(2*2 + maxClientCount*4);
	    					dataBuffer.clear();
	    					
	    					dataBuffer.putShort((short) GLOBAL_STATE__.SET_POINTS.ordinal());
	    					dataBuffer.putShort((short) 0);
	    					
	    					short nScores = 0;
	    					for(ClientThread thread : threads) {
			    				if (thread != null) {
			    					dataBuffer.putInt(deviceManager.getScore(thread.getIp()));
			    					nScores ++;
			    				}
	    					}
	    					
	    					final int position = dataBuffer.position();
	    					dataBuffer.position(2);
	    					dataBuffer.putShort(nScores);
	    					
	    					byte[] sendBytes = new byte[position];
	    					System.arraycopy( dataBuffer.array(), 0, sendBytes, 0, position);
	    					
	    					for(ClientThread thread : threads) {
			    				if (thread != null) {
			    					thread.sendData(sendBytes);
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
			    				
			    				short sendState = (short) GLOBAL_STATE__.ADD_BALLS.ordinal();
			    				buffer.putShort(sendState);	// State: Add balls
			    				short nBalls = 0;
			    				buffer.putShort(nBalls);										// nBalls, byte 2 och 3
			    				
			    				
			    				
			    				for(ClientThread.Ballz ball : thread.ballz) {
			    			
			    					if(buffer.limit() - buffer.position() >= 6*4) {
			    						
				    					float xG = ball.getXPos();
					    				float yG = ball.getYPos();
					    				
					    				if(deviceManager.isOnDevice(thread.getIp(), xG, yG, ball.radius)) {
					    					
					    					float xVelG = ball.getXVel();
						    				float yVelG = ball.getYVel();
						    				
						    				float xPosL = deviceManager.globalToLocalX(thread.getIp(), xG, yG);
						    				float yPosL = deviceManager.globalToLocalY(thread.getIp(), xG, yG);
						    				
						    				float xVelL = deviceManager.globalToLocalVelX(thread.getIp(), xVelG, yVelG);
						    				float yVelL = deviceManager.globalToLocalVelY(thread.getIp(), xVelG, yVelG);
						    				
						    				
					    					if( thread.ownBallz.containsKey(ball.id) ) {
					    						
					    						ClientThread.Ballz ball2 = thread.ownBallz.get(ball.id);
					    					
					    						//System.out.println("OOOOOOOOOOOOOOOOOOO___SAMMMME___OOOOOOOOOOOOOOOOOOO");
					    						
					    						Vec2 vella = physicsWorld.getVelocityFromId(ball.id);
					    						
					    						if(ball.shouldUpdate() || ball2.getXVel() != vella.x || ball2.getYVel() != vella.y || ball.shouldBeRemoved()) {  
					    	
					    							ball2.setXVel(vella.x);
					    							ball2.setYVel(vella.y);
					    							
					    							float xVelL2 = deviceManager.globalToLocalVelX(thread.getIp(), ball2.getXVel(), ball2.getYVel());
								    				float yVelL2 = deviceManager.globalToLocalVelY(thread.getIp(), ball2.getXVel(), ball2.getYVel());
								    				
								    				if(ball2.removed){
								    					xPosL = 1337;
								    					yPosL = 1337;
								    				}
								    				
					    							buffer.putInt(ball.id);
					    							if(ball.shouldBeRemoved()) {
					    								buffer.putInt(-1);
					    								thread.ownBallz.remove(ball.id);
					    								System.out.println("Removing ball for mobile");
					    								System.out.println("-1");
					    							} else {
					    								buffer.putInt(ball.parent);
					    							}
								    				buffer.putFloat(xPosL);
								    				buffer.putFloat(yPosL);
								    				
								    				buffer.putFloat(xVelL2);
								    				buffer.putFloat(yVelL2);
				
								    				nBalls ++;
								    				//System.out.println("OOOOOOOOOOOOOOOOOOO___BALL_UPDATE___OOOOOOOOOOOOOOOOOOO");
					    						}

					    					} else {
					    						
					    						thread.ownBallz.put(ball.id, ball);
					    						buffer.putInt(ball.id);
					    						if(ball.shouldBeRemoved()) {
				    								buffer.putInt(-1);
				    								thread.ownBallz.remove(ball.id);
				    								System.out.println("-1");
				    							} else {
				    								buffer.putInt(ball.parent);
				    							}
							    				buffer.putFloat(xPosL);
							    				buffer.putFloat(yPosL);
							    				
							    				buffer.putFloat(xVelL);
							    				buffer.putFloat(yVelL);
			
							    				nBalls ++;
					    					}				
						    				
					    				} else {
					    					thread.ownBallz.remove(ball.id);
					    				}
				    				
			    					} else {
			    						final int position = buffer.position();
			    						buffer.position(2);
					    				buffer.putShort(nBalls);
					    				if(nBalls != 0) {
					    					byte[] sendBytes = new byte[position];
					    					System.arraycopy( buffer.array(), 0, sendBytes, 0, position);
						    				thread.sendData(sendBytes);
						    				thread.clientInfo.addSentPackageItem(GLOBAL_STATE__.values()[sendState] + "   " + nBalls + " Array Size: " + sendBytes.length);
					    				}
					    				nBalls = 0;
					    				buffer.clear();
					    				
					    				buffer.putShort((short)5);			// State: Add balls
					    				buffer.putShort((short)0);			// nBalls, byte 2 och 3
			    					}
			    					
			    				}
			    		
			    				final int position = buffer.position();
			    				buffer.position(2);
			    				buffer.putShort(nBalls);
			    				
			    				
			    				if(nBalls != 0) {
			    					byte[] sendBytes = new byte[position];
			    					System.arraycopy( buffer.array(), 0, sendBytes, 0, position);
				    				thread.sendData(sendBytes);
	
				    				thread.clientInfo.addSentPackageItem(GLOBAL_STATE__.values()[sendState] + "   " + nBalls + " Array Size: " + sendBytes.length);
			    				}
			
			    			}
			    		}
			    				    		
			    		
		    		}
		   
		    		deviceManager.setUpdateScores(false);
		    		
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
		
		deviceUpdate = new Thread(){ 
			 public void run() {
				while(true) {
					try {
						clientSocket = serverSocket.accept();
						System.out.println("ACCEPTED");
						int j = deviceManager.getDeviceThread(clientSocket.getInetAddress().toString());
						if(j >= 0){
							(threads[j] = new ClientThread(clientSocket.getInetAddress().toString(), clientSocket,threads,updateLoop, deviceManager)).start();
						} else {
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
						}
					} catch (IOException e) {
						System.out.println(e);
					}
				}
			}
		};
	}//End of Main
	
	public void stopClientThreads(){
		for(int i = 0; i < maxClientCount; i++) {
			if(threads[i] != null){
				threads[i].clientInfo.closeWindow();
				threads[i].closeClientSocket();
				threads[i].stop();
				threads[i] = null;
			}
		}
	}
	
	public void clearDeviceManeger(){
		deviceManager.devices.clear();
	}
	
	public void clearBalls(){
		if(threads[0] != null){
			threads[0].clearBalls();
		}
		physicsWorld.bodies.clear();
		//physicsWorld.bodies.clear();
		
	}
	
	public void addBalls(){
		if(threads[0] != null){
			threads[0].addBalls();
		}
	}
} // End of MultiThreds
