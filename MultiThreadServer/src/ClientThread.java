package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;





import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.RevoluteJoint;


/**
 * One clientThread for one device. Adds and sends balls the balls.
 * 
 */

public class ClientThread extends Thread {
	private int id;
	private PrintWriter out;
	private BufferedReader in;
	private Socket clientSocket = null;
	private final ClientThread[] threads;
	private int maxClientCount;
	private AtomicBoolean isRunning = new AtomicBoolean(true);

	private float posX = 0;
	private float posY = 0;
	private float deltaX = 0;
	private float deltaY = 0;
	private int ballCount;

	private ClientThread[] thread;
	private UpdateLoop updateLoop;
	GlobalCoords globalCoords;

	private volatile static LOCAL_STATE__ internalState;
	private volatile static float time1;
	private volatile AtomicInteger isReady = new AtomicInteger(0);

	private volatile String ip;
	private static DeviceManager deviceManager;
	private OutputStream dout;

	static final int MAX_LIFETIME = 7;
	static final float MAX_POSITION_X = (float) (100 / 2.5);
	
	static final float MAX_POSITION_Y = MAX_POSITION_X;

	public class Ballz {

		float xPos, yPos;
		float xVel, yVel;
		float mass, radius, lifeTime;
		int id;
		int type;
		boolean isMoved;
		boolean shouldBeRemoved;
		boolean removed;

		float lx, ly;
		int parent;

		public Ballz(int parent, int id, float xPos, float yPos, float xVel, float yVel, int type) {

			this.id = id;
			this.parent = parent;
			this.xPos = xPos;
			this.yPos = yPos;
			this.xVel = xVel;
			this.yVel = yVel;
			this.lifeTime = 0;
			this.radius = 0.75f; // cm
			this.isMoved = false;
			this.shouldBeRemoved = false;
			this.removed = false;
			this.type = type;
			
			lx = xPos;
			ly = yPos;

			System.out.println("NEW BALL: " + id);
			System.out.println("Added ballz: " + xPos + ", " + yPos + "    "
					+ xVel + ", " + yVel); // * Math.pow(10, 9) * 2.5

			MultiThreds.getPhysicsWorld().addBall(xPos, yPos, xVel, yVel, id,
					0.03f, radius, 0.8f, 0.3f);

		}

		public boolean shouldUpdate() {
			return this.isMoved;
		}

		public void update(float deltaTime) {
			this.xPos += this.xVel * deltaTime;
			this.yPos += this.yVel * deltaTime;
			this.lifeTime += deltaTime;

			Vec2 position = MultiThreds.getPhysicsWorld().getPositionFromId(
					this.id);
			this.xPos = position.x;
			this.yPos = position.y;
			
			Vec2 velocity = MultiThreds.getPhysicsWorld().getVelocityFromId(this.id);

			if( Math.abs(lx - (position.x - velocity.x * deltaTime)) > 0.01 || 
				Math.abs(ly - (position.y - velocity.y * deltaTime)) > 0.01 ) {
				//System.out.println("OOOOOOOOOOOOOOOOOOO___BALL_SHOUDL-.UPDATE___OOOOOOOOOOOOOOOOOOO");
				this.isMoved = true;
			} else {
				this.isMoved = false;
			}

			lx = xPos;
			ly = yPos;
		}

		public boolean shouldBeRemoved() {
			return shouldBeRemoved;
		}
		
		public void setShouldBeRemoved(final boolean shouldBeRemoved) {
			this.shouldBeRemoved = shouldBeRemoved;
		}
		
		public boolean isDead() {
			if ((this.lifeTime * Math.pow(10, -9)) > MAX_LIFETIME || outOfBounds()) {
				return true;
			}
			return false;
		}

		public boolean outOfBounds() {
			if (Math.abs(this.xPos) < MAX_POSITION_X
					&& Math.abs(this.yPos) < MAX_POSITION_Y) {
				return false;
			}
			return true;
		}

		public void printInfo() {
			System.out.println("Ball: " + this.xPos + ", " + this.yPos + "    "
					+ this.xVel * Math.pow(10, 9) * 2.5 + ", " + this.yVel
					* Math.pow(10, 9) * 2.5);
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

		public void setXVel(float xVel) {
			this.xVel = xVel;
		}

		public void setYVel(float yVel) {
			this.yVel = yVel;
		}
		
		public void setPosX(float xPos) {
			this.xPos = xPos;
		}
		
		public void setPosY(float yPos) {
			this.yPos = yPos;
		}
	}

	public volatile static LinkedBlockingQueue<Ballz> ballz = new LinkedBlockingQueue<Ballz>();

	Hashtable<Integer, Ballz> ownBallz = new Hashtable<Integer, Ballz>();

	
	public static RevoluteJoint targetJoint = null;
	/**
	 * Get the ip of the client
	 * 
	 */
	
	public static RevoluteJoint getTargetJoint() {
		return targetJoint;
	}
	
	public void clearAllOwnBallz() {
		ownBallz.clear();
	}
	
	public String getIp() {
		return this.ip;
	}
	
	public int getIsReady() {
		return isReady.get();
	}
	
	public void setIsReady(final int ready) {
		isReady.set(ready);
	}

	/**
	 * Starts one clientThread
	 * 
	 * @param ip
	 *            The ip adress for the client
	 * @param clientSocket
	 *            The socket on the server
	 * @param threads
	 *            An array with all clientThreads
	 * @param updateLoop
	 *            An update thread (not used at this moment)
	 * @param deviceManager
	 *            Stores device information
	 */

	ClientInfo clientInfo;
	
	public ClientThread(String ip, Socket clientSocket, ClientThread[] threads,
			UpdateLoop updateLoop, DeviceManager deviceManager, final int id) {

		System.out.println("NEW CLIENTTHREAD!");
		this.id = id;
		
		this.deviceManager = deviceManager;
		this.clientSocket = clientSocket;
		
		this.threads = threads;
		this.updateLoop = updateLoop;
		maxClientCount = threads.length;
		ballCount = MultiThreds.getPhysicsWorld().bodies.size();
		this.ip = ip;
		internalState = LOCAL_STATE__.MAPPING_STEP1;
		//System.out.println("Ballz size is: " + ballz.size());
		
		clientInfo = new ClientInfo(ip);
		clientInfo.createWindow();
		isReady.set(0);
	
	}
	
	public ClientThread(String ip, Socket clientSocket, ClientThread[] threads,
			UpdateLoop updateLoop, DeviceManager deviceManager, final int id, final ClientInfo info) {

		System.out.println("NEW CLIENTTHREAD!");
		this.id = id;
		
		this.deviceManager = deviceManager;
		this.clientSocket = clientSocket;
		
		this.threads = threads;
		this.updateLoop = updateLoop;
		maxClientCount = threads.length;
		ballCount = MultiThreds.getPhysicsWorld().bodies.size();
		this.ip = ip;
		internalState = LOCAL_STATE__.MAPPING_STEP1;
		//System.out.println("Ballz size is: " + ballz.size());
		
		//clientInfo = new ClientInfo(ip);
		//clientInfo.createWindow();
		clientInfo = info;
		isReady.set(0);
	
	}
	
	public void reset(DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
		internalState = LOCAL_STATE__.MAPPING_STEP1;
		isReady.set(0);
		clearAllOwnBallz();
		clearBalls();
	}
	
	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}


	public int getIdentification() {
		return this.id;
	}
	
	/**
	 * @deprecated replaced by sendData {@link #sendData(byte[])}
	 */
	public void sendMessage(String message) {
		if (out != null && !out.checkError()) {
			// out.println(message);
			// out.flush();
		}
	}

	/**
	 * Send data to device
	 * 
	 * @param buffer
	 *            Array of bytes to send to device
	 */
	public void sendData(byte[] buffer) {
		if (buffer.length > 0) {
			try {
				ByteBuffer headerBuffer = ByteBuffer.allocate(8);
				headerBuffer.clear();
				headerBuffer.putInt(buffer.length);
				headerBuffer.putFloat(System.nanoTime());

				if (dout != null && headerBuffer != null && !clientSocket.isClosed()) {
					dout.write(headerBuffer.array());
					dout.write(buffer);
					dout.flush();
				}

			} catch (IOException e) {
				try {
					clientSocket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//closed = true;
				//e.printStackTrace();
			}
		}
	}
	
	public ClientInfo getClientInfo() {
		return clientInfo;
	}
	
	public void getIsRunning() {
		this.isRunning.get();
	}
	/**
	 * Starts the thread and reads new data from device
	 */
	public void run() {
		isRunning.set(true);
		int maxClientsCount = this.maxClientCount;
		thread = this.threads;
		
		float delta1 = 0;
		
		try {
			dout = clientSocket.getOutputStream();
			clientSocket.setTcpNoDelay(true);
			
			while( isRunning.get() ) {
				
					try{ 

						byte[] headerBuffer = new byte[8];
						
					//	try{
							//System.out.println("Before read: " + id);
							clientSocket.getInputStream().read(headerBuffer);
				/*		} catch (Exception e){
							clientSocket.close();
							System.out.println("CLOED AND RETURN!!!!!!!!!!!!");
							this.isRunning.set(false);
						}*/
						
						//System.out.println("After read: " + id);
						float reciveTime = System.nanoTime();

						ByteBuffer header = ByteBuffer.wrap(headerBuffer);

						int length = header.getInt();
						length = Math.max(0, length);

						float sendTime = header.getFloat();

						if (length > 0) {
							byte[] bytes = new byte[length];
							clientSocket.getInputStream().read(bytes);
							ByteBuffer buffer = ByteBuffer.wrap(bytes);

							short state = buffer.getShort();
							GLOBAL_STATE__ actualState;

							try {
								actualState = GLOBAL_STATE__.values()[state];
							} catch (Exception e) {
								actualState = GLOBAL_STATE__.REG;
							}

							String ip = clientSocket.getInetAddress()
									.toString();

							
							switch (actualState) {

							case SYNCHRONIZE_DEVICE: {
								clientInfo.addIncomingPackageItem(actualState.name() + "   " + reciveTime + "   " + sendTime);
								
								ByteBuffer sendBuffer = ByteBuffer.allocate(1 * 2 + 2 * 4);
								sendBuffer.clear();

								final short sendState = (short) GLOBAL_STATE__.SYNCHRONIZE_DEVICE.ordinal();
								sendBuffer.putShort(sendState); // State: SYNCHRONZE_DEVICE

								delta1 = reciveTime - sendTime;
								System.out.println("delta1 = " + delta1);
								System.out.println("delta1_2 = " + delta1 * (float)Math.pow(10, -9));
								sendBuffer.putFloat(sendTime); // t1
								sendBuffer.putFloat(reciveTime);
								
								sendData(sendBuffer.array());
								
								clientInfo.addSentPackageItem(GLOBAL_STATE__.values()[sendState] + "   " + sendTime + "   " + reciveTime);
								clientInfo.setClock(delta1);
								
								System.out.println("CLOCK === " + System.nanoTime() * Math.pow(10, -9));
							}
								break;

							case ADD_DEVICE: {
								short type = buffer.getShort();
								int xDPI = buffer.getInt();
								int yDPI = buffer.getInt();
								int deviceResX = buffer.getInt();
								int deviceResY = buffer.getInt();

								// TODO: Add type of device.
								deviceManager.addDevice(ip, type, xDPI, yDPI, deviceResX, deviceResY);
								
								clientInfo.addIncomingPackageItem(actualState.name() + "   " + type + "   " + xDPI + "   " + yDPI + "   " + deviceResX + "   " + deviceResY);
								clientInfo.setType(type);
								clientInfo.setXDPI(xDPI);
								clientInfo.setYDPI(yDPI);
								clientInfo.setResX(deviceResX);
								clientInfo.setResY(deviceResY);
								
								// Dupliciation for main device
								float l_midX = deviceManager.getMidX(ip);
								float l_midY = deviceManager.getMidY(ip);

								float g_midX = deviceManager.localToGlobalX(ip, l_midX, l_midY);
								float g_midY = deviceManager.localToGlobalY(ip, l_midX, l_midY);
								clientInfo.setMidX(g_midX * 2.5f);
								clientInfo.setMidY(g_midY * 2.5f);
								
								if(type == 0 && targetJoint == null) {
									targetJoint = MultiThreds.getPhysicsWorld().addTarget(g_midX, g_midY, 0);
								}
								
								
								
								// Send response 
								ByteBuffer sendBufferAdd = ByteBuffer.allocate(2 + 4);

								final short sendState = (short) GLOBAL_STATE__.ADD_DEVICE.ordinal();
								sendBufferAdd.putShort(sendState); 
								sendBufferAdd.putInt(id);
								System.out.println("Send id: " + id);

								sendData(sendBufferAdd.array());
								clientInfo.addSentPackageItem(GLOBAL_STATE__.values()[sendState] + "   Id: " + id);
							}
								break;

							case MAP_DEVICE: {
								if (deviceManager.needsMapping(ip)) {
									System.out.println("MAPPING_STEP2");

									float time2 = System.nanoTime();
									//float time2 = sendTime + delta1;
									float deltaTime = time2 - time1;

									float x11 = buffer.getFloat();
									float y11 = buffer.getFloat();
									float x22 = buffer.getFloat();
									float y22 = buffer.getFloat();
									float t1 = buffer.getFloat();
									
									clientInfo.addIncomingPackageItem(actualState.name() + "   STEP2   " + x11 + "   " + y11 + "   " + x22 + "   " + y22 + "   " + t1);
									System.out.println("STEP2: " + ip + ", " + x11 + ", " + y11 + ", " + x22 + ", " + y22 + ", " + t1 + ", " + deltaTime);

									deviceManager.devicePointMappingStep2(ip, x11, y11, x22, y22, t1, deltaTime);

									time1 = time2; // F�r multirow
									System.out.println("MAPPING_STEP2 DONE");

									deviceManager.setNeedsMapping(ip, false);

									float rotZ = deviceManager.getRotZ(ip);

									float posX = deviceManager.getPosX(ip);
									float posY = deviceManager.getPosY(ip);

									ByteBuffer sendBuffer = ByteBuffer.allocate(1 * 2 + 5 * 4);
									sendBuffer.clear();

									// Send response - for drawing background
									final short sendState = (short) GLOBAL_STATE__.ADD_MAP.ordinal();
									sendBuffer.putShort(sendState); // State: ADD_MAP

									float l_midX = deviceManager.getMidX(ip);
									float l_midY = deviceManager.getMidY(ip);

									float g_midX = deviceManager.localToGlobalX(ip, l_midX, l_midY);
									float g_midY = deviceManager.localToGlobalY(ip, l_midX, l_midY);

									String ipOfMiddle = deviceManager.getMiddleIp();

									float l_main_midX = deviceManager.getMidX(ipOfMiddle);
									float l_main_midY = deviceManager.getMidY(ipOfMiddle);

									float g_main_midX = deviceManager.localToGlobalX(ipOfMiddle, l_main_midX, l_main_midY);
									float g_main_midY = deviceManager.localToGlobalY(ipOfMiddle, l_main_midX, l_main_midY);
									
									sendBuffer.putFloat(rotZ);
									sendBuffer.putFloat(g_midX);
									sendBuffer.putFloat(g_midY);
									sendBuffer.putFloat(g_main_midX);
									sendBuffer.putFloat(g_main_midY);

									sendData(sendBuffer.array());
									clientInfo.addSentPackageItem(GLOBAL_STATE__.values()[sendState] + "   " + rotZ + "   " + g_midX + "   " + g_midY + "   " + g_main_midX + "   " + g_main_midY);
									clientInfo.setAngle((float) (rotZ * 180 / Math.PI));
									clientInfo.setPosX(posX * 2.5f);
									clientInfo.setPosY(posY * 2.5f);
									clientInfo.setMidX(g_midX * 2.5f);
									clientInfo.setMidY(g_midY * 2.5f);
									
									ownBallz.clear();

								} else {
									System.out.println("MAPPING_STEP1");

									time1 = System.nanoTime();
									//time1 = sendTime + delta1;

									float x1 = buffer.getFloat();
									float y1 = buffer.getFloat();
									float x2 = buffer.getFloat();
									float y2 = buffer.getFloat();
									float t = buffer.getFloat();

									clientInfo.addIncomingPackageItem(actualState.name() + "   STEP1   " + x1 + "   " + y1 + "   " + x2 + "   " + y2 + "   " + t);
									System.out.println("STEP1: " + ip + ", " + x1 + ", " + y1 + ", " + x2 + ", " + y2 + ", " + t);

									deviceManager.devicePointMappingStep1(ip, x1, y1, x2, y2, t);
									
									System.out.println("MAPPING_STEP1 DONE");
									
									//TESTING WITH JUST ONE DEVICE 
									/*
									float l_midX = deviceManager.getMidX(ip);
									float l_midY = deviceManager.getMidY(ip);

									float g_midX = deviceManager.localToGlobalX(ip, l_midX, l_midY);
									float g_midY = deviceManager.localToGlobalY(ip, l_midX, l_midY);

									String ipOfMiddle = deviceManager.getMiddleIp();

									float l_main_midX = deviceManager.getMidX(ipOfMiddle);
									float l_main_midY = deviceManager.getMidY(ipOfMiddle);

									float g_main_midX = deviceManager.localToGlobalX(ipOfMiddle, l_main_midX, l_main_midY);
									float g_main_midY = deviceManager.localToGlobalY(ipOfMiddle, l_main_midX, l_main_midY);
									
									targetJoint = MultiThreds.getPhysicsWorld().addTarget(g_main_midX, g_main_midY, 0.8f);
									*/
								}
							}
								break;

							case RUN_DEVICE: {
								System.out.println("RUN DEVICE: " + System.nanoTime() * Math.pow(10, -9));
								float x1 = buffer.getFloat();
								float y1 = buffer.getFloat();
								float x2 = buffer.getFloat();
								float y2 = buffer.getFloat();
								float t = buffer.getFloat();
								short type = buffer.getShort();

								clientInfo.addIncomingPackageItem(actualState.name() + "   " + x1 + "   " + y1 + "   " + x2 + "   " + y2 + "   " + t);
								
								float xVel = deviceManager.computeVelocityX(ip, x1, y1, x2, y2, t);
								float yVel = deviceManager.computeVelocityY(ip, x1, y1, x2, y2, t);

								float xG = deviceManager.localToGlobalX(ip, x2, y2);
								float yG = deviceManager.localToGlobalY(ip, x2, y2);

								float timeStep = System.nanoTime() - sendTime;

								xG = (float) (xG + xVel * timeStep * Math.pow(10, -9));
								yG = (float) (yG + yVel * timeStep * Math.pow(10, -9));

								ballCount++;
								// MultiThreds.sharedVariables.getInstance().incrementBallCounter();
								synchronized (this) {
									// ballz.add(new
									// Ballz(MultiThreds.sharedVariables.getInstance().getBallCounter(),xG,
									// yG, xVel, yVel));
									Random r = new Random();
									//int type = r.nextInt(3) + 1;
									System.out.println("Type of ball is: " + type);
									ballz.add(new Ballz(getIdentification(), ballCount, xG, yG, xVel, yVel,type));
									for (int j = 0; j < maxClientsCount; j++) {
										if (threads[j] != null) {
											threads[j].ballCount = ballCount;
										}
									}
								}
								System.out.println("Ball count: " + ballCount);
								System.out.println("Ballz size: " + ballz.size());
							}
								break;

							// Set server state
							case SET_STATE: {

								short newState = buffer.getShort();
								
								clientInfo.addIncomingPackageItem(actualState.name() + "   " + newState);
								
								System.out.println("@ SERVER: SET STATE: " + newState);

								try {
									GLOBAL_STATE__ toState = GLOBAL_STATE__.values()[newState];

									switch (toState) {
										case MAP_MAIN: {
											deviceManager.setMappingAtMainDevice();
											deviceManager.setNeedsMapping(ip, true);
										}
										break;
										
										case MAP_ALL: {
											deviceManager.setMappingAtAllDevices();
											deviceManager.setNeedsMapping(ip, true);
										}
									}
								} catch (Exception e) {
									// Do nothing
									System.out.println("ERROR: Invalid state: " + newState);
								}
							}
								break;
								
							case IS_READY: {
								isReady.set(buffer.getInt());;
								
								clientInfo.addIncomingPackageItem(actualState.name() + "   " + isReady);
							}

							case REG:
								break;

							default:
								break;
							}
						}
					} catch (Exception e) {
						System.out.println("FAILED READING ETC");
		                clientSocket.close();
		                isRunning.set(false);
		                break;
					}
			}
			clientSocket.close();
			
			System.out.println("CLOSED!!!!!!!!!!!!!!!!!!!!!!!!");
		} catch (Exception e) {
			System.out.println("Error");
			System.out.println("DISCONNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNECT");
			e.printStackTrace();
		}
	}
	
	public void closeClientSocket(){
		try {
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addBalls() {
		
			for(int i = 0; i < 100; i++) {
				ballCount++;
				ballz.add(new Ballz(-2, ballCount, 0, 0,0, 0,1));
			}
			synchronized (this) {
			for (int j = 0; j < 10; j++) {
				if (threads[j] != null) {
					threads[j].ballCount = ballCount;
				}
			}
		}
		
	}
	
	public void clearBalls() {
		ballz.clear();
		synchronized (this) {
			ballCount = 0;
			for (int j = 0; j < 10; j++) {
				if (threads[j] != null) {
					threads[j].ballCount = ballCount;
				}
			}
		}
	}
	
	public static void removeAndScore(int id){
		Ballz theBall = null;
		for(Ballz ball : ballz){
			if(ball.id == id){
				theBall = ball;
			}
		}
		if(theBall != null) {
			System.out.println("I got touched by ball: " + theBall.id); 
			System.out.println("Score to player: " + theBall.parent);
			deviceManager.score(theBall.parent);
			theBall.removed = true;
			theBall.setXVel(0);
			theBall.setYVel(0);
		}
	}
}// END OF clientThread
