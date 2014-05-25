package src;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.jbox2d.common.Vec2;



/**
 * Main loop for the server.
 *
 */



public class MultiThreds {
	
	private static ServerSocket serverSocket = null;
	private static Socket clientSocket = null;
	
	static PhysicsWorld physicsWorld;
	
	private String SERVER_NAME;
	private volatile int maxClientCount;
	private static int slotsTaken = 0;
	private int gameTimeInSeconds = 60;
	
	private volatile ClientThread[] threads;
	private volatile int[] scores;
	private UpdateLoop updateLoop;
	
	volatile static DeviceManager deviceManager;
	
	public static volatile SharedVariables sharedVariables;
	
	public ServerGraphics serverGraphics;
	
	public Thread update;
	public Thread deviceUpdate;
	

	private short[] colors = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 };
	private float startTime;
	

	public static PhysicsWorld getPhysicsWorld() {
			return physicsWorld;
	}
	
	
	public void startGame(final short nPlayers) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(2*2 + nPlayers*2 + 0*4 + 2 + 2*nPlayers + 2);
		dataBuffer.clear();
		short sendState = (short) GLOBAL_STATE__.START_GAME.ordinal();
		dataBuffer.putShort(sendState);
		dataBuffer.putShort((short)gameTimeInSeconds);
		dataBuffer.putShort(nPlayers);	
		
		int i=0;
		for(ClientThread thread : threads) {
			if(thread != null && thread.getIsReady() == 1 && !deviceManager.isMiddle(thread.getIp()) ) {
				System.out.println("ID: " + (short)thread.getIdentification());
				dataBuffer.putShort((short)thread.getIdentification());
				dataBuffer.putShort(colors[i]);
				i++;
			}
		}
		
		int position = dataBuffer.position();
		byte[] sendBytes = new byte[position];
		System.arraycopy( dataBuffer.array(), 0, sendBytes, 0, position);
	
		for(ClientThread thread : threads) {
			if(thread != null && thread.getIsReady() == 1) {
				thread.sendData(sendBytes);
	    		thread.clientInfo.addSentPackageItem(GLOBAL_STATE__.values()[sendState] + "   " + nPlayers);
			}
		}
	}
	
	
	public MultiThreds(final String serverName, final String maxClient, final String min, final String sec) {
		
		try{
			if(serverName.isEmpty()) {
				SERVER_NAME = "Default Server";
			} else {
				SERVER_NAME = serverName;
			}
		} catch(Exception e) {
			SERVER_NAME = "Default Server";
		}
		
		try{
			maxClientCount = Integer.parseInt(maxClient);
			if(maxClientCount <= 1) {
				maxClientCount = 10;
			}
		} catch(Exception e) {
			maxClientCount = 10;
		}
		
		int tempTime = 0;
		try{
			int minVal = Integer.parseInt(min);
			if(minVal > 0) {
				tempTime += minVal*60;
			}
		} catch(Exception e) {
		}
		
		try{
			int secVal = Integer.parseInt(sec);
			if(secVal > 0) {
				tempTime += secVal;
			}
		} catch(Exception e) {
		}
		
		if(tempTime > 0) {
			gameTimeInSeconds = tempTime;
		}

		System.out.println("Started new server: " + SERVER_NAME + ", " + maxClientCount + ", " + min + "." + sec);
		threads = new ClientThread[maxClientCount];
		scores = new int[maxClientCount];
		updateLoop = new UpdateLoop(threads, maxClientCount);
		
	
		
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
		    	
		    	while(true) {
		    		
		    	System.out.println("NEW GAME______________");
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
									System.out.println("STARTED!!!!!!!!!!!!!!!!!!!!!!!!!!!");
									nPlayers --;
								}
							}
						}
		    		}
		    		
		    		if(nPlayers <= 0) { // Ty main-device är fortfarande spelare.
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
		    	
		    
		   		System.out.println("START GAME______________");
		    	startGame(nPlayers);
		    	
				
				startTime = System.nanoTime();
				final float endTime = startTime + (float)(gameTimeInSeconds * Math.pow(10,9)) + 3.5f * (float) Math.pow(10, 9);
		    	float timeBegin, timeEnd = 0, timeDelta = 1 / tickRate, timeDelay;
		    	while(endTime - System.nanoTime() > 0) {
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
		    					physicsWorld.removeBall(ball.id);
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
	    					ByteBuffer dataBuffer = ByteBuffer.allocate(2*2 + nPlayers*(2+4));
	    					dataBuffer.clear();
	    					
	    					short sendState = (short) GLOBAL_STATE__.SET_POINTS.ordinal();
	    					dataBuffer.putShort(sendState);
	    					dataBuffer.putShort((short) 0);
	    					
	    					short nScores = 0;
	    					for(ClientThread thread : threads) {
			    				if (thread != null && !deviceManager.isMiddle(thread.getIp())) {
			    					dataBuffer.putShort((short) thread.getIdentification());
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
			    					thread.clientInfo.addSentPackageItem(GLOBAL_STATE__.values()[sendState] + "");
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
			    			
			    					if(buffer.limit() - buffer.position() >= 7*4) {
			    						
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
								    				
								    				buffer.putInt(ball.type);
								    				
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
							    				
							    				buffer.putInt(ball.type);
							    				
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
		    	
		    
		    	// Game over:
		    	System.out.println("GAME OVER!!!!!");
		    	
				class Pair implements Comparable{
					final short id;
					final int points;
					
					private Pair(final short first, final int points) {
						this.id = first;
						this.points = points;
	
					}

					public int compareTo(Object p2) {
						if(this.points == ((Pair)(p2)).points) {
							return 0;
						} else if(this.points < ((Pair)(p2)).points) {
							return 1;
						} else {
							return -1;
						}
					}
				}
				
				ArrayList<Pair> finalScores = new ArrayList<Pair>();
				for(ClientThread thread : threads) {
    				if (thread != null && !deviceManager.isMiddle(thread.getIp())) {
    					finalScores.add(new Pair((short)thread.getIdentification(), deviceManager.getScore(thread.getIp())));
    				}
				}
				
				Collections.sort(finalScores);
				
				ByteBuffer dataBuffer = ByteBuffer.allocate(2*2 + nPlayers*(4+2));
				dataBuffer.clear();
				
				dataBuffer.putShort((short) GLOBAL_STATE__.GAME_OVER.ordinal());
				dataBuffer.putShort((short) finalScores.size());
				
				System.out.println("Final Scores: ");
				for(Pair pair : finalScores) {
					System.out.println("Score: " + pair.id + " = " + pair.points);
					dataBuffer.putShort(pair.id);
					dataBuffer.putInt(pair.points);
				}
				
				final int position = dataBuffer.position();
				dataBuffer.position(2);
				dataBuffer.putShort((short) finalScores.size());
				
				byte[] sendBytes = new byte[position];
				System.arraycopy( dataBuffer.array(), 0, sendBytes, 0, position);
				
				for(ClientThread thread : threads) {
    				if (thread != null) {
    					thread.sendData(sendBytes);
    				}
				}	
		    	
				// Clean up
				physicsWorld.bodies.clear();
				//deviceManager = new DeviceManager();
	    			    		
	    		for(ClientThread thread : threads) {
	    			if(thread != null) {
	    				thread.reset(deviceManager);
	    			}
	    		}
	    		
	    		//deviceManager.devices.clear();
	    		deviceManager.setMappingAtMainDevice();
	    		deviceManager.setMappingAtAllDevices();
	    		deviceManager.clearAllScores();
	    		
	 
	    		physicsWorld = new PhysicsWorld();
	    		physicsWorld.create(new Vec2(0.0f, 0.0f));
	    		
	    		//physicsWorld.clearForces();

	    		
				float l_midX = deviceManager.getMidX(deviceManager.getMiddleIp());
				float l_midY = deviceManager.getMidY(deviceManager.getMiddleIp());

				float g_midX = deviceManager.localToGlobalX(deviceManager.getMiddleIp(), l_midX, l_midY);
				float g_midY = deviceManager.localToGlobalY(deviceManager.getMiddleIp(), l_midX, l_midY);
			
				ClientThread.targetJoint = physicsWorld.addTarget(g_midX, g_midY, 0);
				
		    }
		    }
		};
		
		deviceUpdate = new Thread(){ 
			 public void run() {
				 
				 Thread t2 = new Thread(new Runnable() {
					 
					 class Sender implements Runnable {

						 final DatagramPacket packet;
						 MulticastSocket socket;
						 
						 Sender(MulticastSocket socket, final DatagramPacket packet) {
							this.packet = packet; 
							this.socket = socket;
						 }
								 
						public void run() {
							ByteBuffer buffer = ByteBuffer.wrap(packet.getData());
						    
						    final short state = buffer.getShort();
							GLOBAL_STATE__ actualState;

							try {
								actualState = GLOBAL_STATE__.values()[state];
							} catch (Exception e) { 
								actualState = GLOBAL_STATE__.REG;
							}
							
							boolean ok = false;
							if(state == GLOBAL_STATE__.HAND_SHAKE.ordinal()) {
								ok = true;
								char[] key = {'Y', 'O', 'L', 'O'};
								for(int i=0; i<key.length; i++) {
									if(buffer.getChar() != key[i]) {
										ok = false;
										break;
									}
								}
							}
							
							if(ok) {
							   // String received = new String(packet.getData());
							   // System.out.println("RECEIVED: " + received);
								
							    final short sendStatez = (short) GLOBAL_STATE__.HAND_SHAKE.ordinal();
								final char[] serverName =  SERVER_NAME.toCharArray();
								ByteBuffer bufferz = ByteBuffer.allocate(1024);
								bufferz.putShort(sendStatez);
								bufferz.putShort((short) serverName.length);
								
								for(int i=0; i< serverName.length; i++) {
									bufferz.putChar(serverName[i]);
								}
								
								bufferz.putInt(slotsTaken);
								bufferz.putInt(maxClientCount);
				
								try {
									System.out.println("SEND!");
									socket.send(new DatagramPacket(bufferz.array(), bufferz.array().length, packet.getAddress(), packet.getPort()));
								} catch (IOException e) {
									System.out.println("ERROR SENDING DATAGRAM");
									e.printStackTrace();
								}				
							} else {
								System.out.println("NOT OK!");
							}
							 
						}
						 
					 }
					 
						public void run() {
							System.out.println("START BROADCAST");
							
							final int port = 4444;
							final String broadcastIp = "237.0.0.1";
						
							try {
								MulticastSocket socket = new MulticastSocket(port);
							
								InetAddress group = null;
								
								try {
									group = InetAddress.getByName(broadcastIp);
								} catch (UnknownHostException e) {
									System.out.println("ERROR GETTING IPGROUP");
									e.printStackTrace();
								}
								
								try {
									socket.joinGroup(group);
								} catch (IOException e) {
									System.out.println("ERROR JOING IPGROUP");
									e.printStackTrace();
								}
	
								
								boolean isRunning = true;
								while(isRunning) {
									System.out.println("LOOP BROADCAST");
						
									DatagramPacket packet;
									
								    byte[] incomingData = new byte[10];
								    packet = new DatagramPacket(incomingData, incomingData.length);
								    
								    try {
								    	System.out.println("WAITING");
										socket.receive(packet);
										System.out.println("GOT");
									} catch (IOException e) {
										System.out.println("ERROR RECIVING PACKET");
										e.printStackTrace();
									}
								    Thread sender = new Thread(new Sender(socket, packet));
								    sender.start();
									
								}
								
								try {
									socket.leaveGroup(group);
								} catch (IOException e) {
									System.out.println("ERROR LEAVING IPGROUP");
									e.printStackTrace();
								}
								socket.close();
							
							} catch (IOException e) {
								System.out.println("ERROR CREATING MULTICAST SOCKET");
								e.printStackTrace();
							}
							
						}
				 });
				 t2.start();
				 
				while(true) {
					try {
						clientSocket = serverSocket.accept();
						System.out.println("ACCEPTED");
						int j = deviceManager.getDeviceThread(clientSocket.getInetAddress().toString());
						if(j >= 0){
							System.out.println("ALREADY in LIST");
							//threads[j].setClientSocket(clientSocket);
							//System.out.println("DONE ___________ ALREADY in LIST");
							//threads[j].closeClientSocket();
							//threads[j].setClientSocket(clientSocket);
/*
							try {
								Thread.currentThread().sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			*/				
							ClientInfo info = threads[j].getClientInfo();
							(threads[j] = new ClientThread(clientSocket.getInetAddress().toString(), clientSocket,threads,updateLoop, deviceManager, j, info)).start();
						} else {
							int i = 0;
							for(i=0; i<maxClientCount; i++) {
								if(threads[i] == null) {
									slotsTaken++;
									threads[i] = new ClientThread(clientSocket.getInetAddress().toString(), clientSocket,threads,updateLoop, deviceManager, i);
									threads[i].start();
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
				
				//t.join();
			}
		};
		
		update.start();
		deviceUpdate.start();
			
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
