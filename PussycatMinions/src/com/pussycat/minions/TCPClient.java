package com.pussycat.minions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;


import android.util.Log;

public class TCPClient {
	private String serverMessage;
	public static String SERVERIP = "192.168.43.213";
	public static final int SERVERPORT = 4444;
	private boolean mRun = false;
	private OnMessageReceived messageListner = null;
	
	PrintWriter out;
	BufferedReader in;
	OutputStream dout;
	BufferedWriter buffw;
	
	LOCAL_STATE__ internalState;
	//public volatile LinkedBlockingQueue<DataPackage> messages = new LinkedBlockingQueue <DataPackage>();
	public DataPackage dataPackage = null;
	
	
	public TCPClient(OnMessageReceived listner){
		messageListner = listner;
	}
	
	public TCPClient() {
		
	}
	
	public void sendMessage(String message) {
		if(out != null && !out.checkError()) {
			out.println(message);
			out.flush();
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
	
	public void getData(){
		if(out !=null && !out.checkError()) {
			out.println("");
			out.flush();
		}
	}
	
	public void stopClient() {
		mRun = false;
	}
	
	public void run() {
		mRun = true;
		
		try {
			InetAddress serverAddr = InetAddress.getByName(SERVERIP);
			Socket socket = new Socket(serverAddr, SERVERPORT);
			
			try {
			/*	out = 	new PrintWriter(
						new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
			*/
				dout = socket.getOutputStream();
				
			/*	in = 	new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
						*/

				while(mRun) {
					/*
					serverMessage = in.readLine();
					if(serverMessage != null && messageListner != null) {
						//System.out.println("Server says: " + serverMessage);
						messageListner.messageReceived(serverMessage);
					}
					serverMessage = null;
					*/
					
					/*
					int length = socket.getInputStream().available();					
					Log.d("DATAP", "DATAP available: " + socket.getInputStream().available());
					
					if(length > 0) {
						length = Math.min(1024, length);
						byte[] bytes = new byte[length];
						socket.getInputStream().read(bytes);
			     		messages.add(new DataPackage(bytes, socket.getInetAddress().toString(), socket.getPort()));
					}
					*/
					byte[] bytesLength = new byte[4];
					socket.getInputStream().read(bytesLength);	
					ByteBuffer bufferLength = ByteBuffer.wrap(bytesLength);
					
					int length = bufferLength.getInt();
				    length = Math.max(0, length);

					if(length > 0) {
						byte[] bytes = new byte[length];
						socket.getInputStream().read(bytes);
						dataPackage = new DataPackage(bytes, socket.getInetAddress().toString(), socket.getPort());
						//messages.add(new DataPackage(bytes, socket.getInetAddress().toString(), socket.getPort()));
					}

					
		     		/*
					short state = buffer.getShort();
					GLOBAL_STATE__ actualState;
					
					try {
						actualState = GLOBAL_STATE__.values()[state];
					} catch(Exception e) {
						actualState = GLOBAL_STATE__.ADD_BALL;
					}
					
	        		String ip = socket.getInetAddress().toString();

	        		switch(actualState) {
	        			case ADD_BALL:
		        			float xPos = buffer.getInt();
				        	float yPos = buffer.getInt();	
		        			float xVel = buffer.getFloat();	
		        			float yVel = buffer.getFloat();		
		        			Log.d("GOT", "GOT from " + ip + "  :   " + xPos + ", " + yPos + "   " + xVel + ", " + yVel);
	        			break;
	        			
	        			case SET_STATE:
	        				short newState = buffer.getShort();
	        				Log.d("GOT", "NEW STATE: " + newState);
	        			break;
	        			
	        			default:
	        			break;
	        		}
	        		*/
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
	
	public interface OnMessageReceived {
		public void messageReceived(String message);
	}
	
}//End of TCPClient

