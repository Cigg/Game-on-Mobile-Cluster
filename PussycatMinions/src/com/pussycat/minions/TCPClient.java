package com.pussycat.minions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;


import android.util.Log;

public class TCPClient {

	private String serverMessage;
	public static String SERVERIP = "192.168.43.148";

	public static final int SERVERPORT = 4444;
	private boolean isRunning = false;
	
	PrintWriter out;
	BufferedReader in;
	OutputStream dout;
	InputStream din;
	BufferedWriter buffw;
	
	GLOBAL_STATE__ internalState;
	BallHandler ballHandler;
	//public volatile LinkedBlockingQueue<DataPackage> messages = new LinkedBlockingQueue <DataPackage>(); 	// Old legacy
	//public volatile ArrayList<DataPackage> messages = new ArrayList<DataPackage>();
	
	public EndlessQueue messages;
	
	public TCPClient(GLOBAL_STATE__ internalState, BallHandler ballHandler) {
		this.internalState = internalState;
		this.ballHandler = ballHandler;
		 messages = new EndlessQueue(new DataPackage[3]);
	}

	
	public synchronized void sendData(byte[] buffer) {
		if(buffer.length > 0) {	
			try {
				ByteBuffer header = ByteBuffer.allocate(8);
				header.putInt(buffer.length);
				header.putFloat(System.nanoTime());
				
				if(dout != null) {
					dout.write(header.array());
					dout.write(buffer);
					dout.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopCommuinication() {
		isRunning = false;
	}
	
	public void run() {
		isRunning = true;
		
		try {
			InetAddress serverAddr = InetAddress.getByName(SERVERIP);
			Socket socket = new Socket(serverAddr, SERVERPORT);
			
			socket.setTcpNoDelay(true);
			//socket.setPerformancePreferences(connectionTime, latency, bandwidth)
			try {
				
				dout = socket.getOutputStream();
				din = socket.getInputStream();
				byte[] headerBuffer = new byte[8];
				
				while(isRunning) {
					
					din.read(headerBuffer);	
					float reciveTime = System.nanoTime();
					
					ByteBuffer header = ByteBuffer.wrap(headerBuffer);
					
					int length = header.getInt();
				    length = Math.max(0, length);
				    
				    float sendTime = header.getFloat();

					if(length > 0) {
						
						byte[] bytes = new byte[length];
					
						din.read(bytes);
						DataPackage dataPackageToAdd = new DataPackage(bytes, socket.getInetAddress().toString(), socket.getPort(), sendTime, reciveTime);
						
						messages.add(dataPackageToAdd);
						
						synchronized (messages) {
							messages.notify();
						};
						
						/*
					
			        		ByteBuffer buffer = ByteBuffer.wrap(bytes);
			        		short state = buffer.getShort();
							
			        		GLOBAL_STATE__ actualState;
							
							try {
								actualState = GLOBAL_STATE__.values()[state];
							} catch(Exception e) {
								actualState = GLOBAL_STATE__.ADD_BALL;
							}
						
				    		
				    		switch(actualState) {
				    			case ADD_BALL:
				    			{
				    				int id = buffer.getInt();
			    					float xPos = buffer.getInt();
						        	float yPos = buffer.getInt();	
				        			float xVel = buffer.getFloat();	
				        			float yVel = buffer.getFloat();		
				        			
					        		//Log.d("GOT", "GOT from " + ip + "  :   " + xPos + ", " + yPos + "   " + xVel + ", " + yVel);
				        			ballHandler.addBall(id, xPos, yPos, 1, xVel, yVel, false);
				    			}
				    			break;
				    			
				    			case ADD_BALLS:
				    			{
				    				final short nBalls = buffer.getShort();
				    				//Log.d("NBALLS", "NBALLS: " + nBalls);
				    				for(int i=0; i<nBalls; i++) {
				    					int id = buffer.getInt();
				    					float xPos = buffer.getFloat();
							        	float yPos = buffer.getFloat();	
					        			float xVel = buffer.getFloat();	
					        			float yVel = buffer.getFloat();		
					        			
					        			Log.d("VEL", "GOT xVel = " + xVel * Math.pow(10, 9) * 2.5);
					        			Log.d("VEL", "GOT yVel = " + yVel * Math.pow(10, 9) * 2.5);
					        			
					        			//Log.d("GOT", "GOT from " + ip + "  :   " + xPos + ", " + yPos + "   " + xVel + ", " + yVel);
					        			ballHandler.addBall(id, xPos, yPos, 1, xVel, yVel, false);
				    				}
				    			}
				    			break;
				    			
				    			case SET_STATE:
				    			{
				    				short newState = buffer.getShort();
				    				
				    				Log.d("FINGERS", "GOT: " + newState);
				    				Log.d("GOT", "NEW STATE: " + newState);
				    				
				    				GLOBAL_STATE__ newInternalState;
				    				
			        				try {
			        					newInternalState = GLOBAL_STATE__.values()[newState];
									} catch(Exception e) {
										newInternalState = internalState;
										System.out.println("ERROR: Invalid state: " + newState);
									}
			        				
			        				 internalState = newInternalState;
				    			}
				    			break;
				    			
				    			default:
				    			break;
				    		}*/
			        	}
						

				}
				
			} catch (Exception e) {
				Log.e("Android", "ERROR", e);
			} finally {
				socket.close();
			}
			
		} catch (Exception e) {
			Log.e("Android", "ERROR", e);
		}
	}
	
}

