package com.pussycat.minions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

import android.util.Log;

public class TCPClient extends Thread {
	

	public static String SERVERIP = "192.168.43.122";


	public static final int SERVERPORT = 4444;
	private boolean isRunning = false;
	
	PrintWriter out;
	BufferedReader in;
	OutputStream dout;
	InputStream din;
	BufferedWriter buffw;
	
	public EndlessQueue<DataPackage> messages;
	private final int numberOfMessages = 64; 

	
	public TCPClient() {
		messages = new EndlessQueue<DataPackage>(new DataPackage[numberOfMessages]);
	}

	
	public synchronized void sendData(byte[] buffer) {
		if( buffer.length > 0 ) {	
			try {
				ByteBuffer header = ByteBuffer.allocate(8);
				header.putInt(buffer.length);
				header.putFloat(System.nanoTime());
				
				if( dout != null ) {
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
		
		Thread.currentThread().setName("TCPClient");
		
		try {
			InetAddress serverAddr = InetAddress.getByName(SERVERIP);
			Socket socket = new Socket(serverAddr, SERVERPORT);
			
			socket.setTcpNoDelay(true);

			try {
				
				dout = socket.getOutputStream();
				din = socket.getInputStream();
				byte[] headerBuffer = new byte[8];
				
				while( isRunning ) {
				
					din.read(headerBuffer);	
					float reciveTime = System.nanoTime();
					
					ByteBuffer header = ByteBuffer.wrap(headerBuffer);
					
					int length = header.getInt();
				    length = Math.max(0, length);
				    
				    float sendTime = header.getFloat();

					if( length > 0 ) {
						
						byte[] bytes = new byte[length];
					
						din.read(bytes);
						DataPackage dataPackageToAdd = new DataPackage(bytes, socket.getInetAddress().toString(), socket.getPort(), sendTime, reciveTime);
						
						messages.add(dataPackageToAdd);
						
						synchronized( messages ) {
							messages.notify();
						};
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

