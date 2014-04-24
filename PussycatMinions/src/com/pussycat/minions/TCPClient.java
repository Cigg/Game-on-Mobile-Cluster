package com.pussycat.minions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

import android.util.Log;

public class TCPClient extends Thread {
	
	public static final String SERVERIP = "192.168.43.122";
	public static final int SERVERPORT = 4444;
	private volatile boolean isRunning = false;
	
	OutputStream outputStream;
	InputStream inputStream;
	BufferedWriter buffw;
	
	private final int numberOfMessages = 64; 
	public EndlessQueue<DataPackage> messages;

	
	public TCPClient() {
		messages = new EndlessQueue<DataPackage>(new DataPackage[numberOfMessages]);
	}

	
	public synchronized void sendData(byte[] buffer) {
		if( buffer.length > 0 ) {	
			try {
				ByteBuffer header = ByteBuffer.allocate(8);
				header.putInt(buffer.length);
				header.putFloat(System.nanoTime());
				
				if( outputStream != null ) {
					outputStream.write(header.array());
					outputStream.write(buffer);
					outputStream.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	public void setIsRunning(final boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	
	public void run() {
		isRunning = true;
		
		Thread.currentThread().setName("TCPClient");
		
		try {
			InetAddress serverAddress = InetAddress.getByName(SERVERIP);
			Socket socket = new Socket(serverAddress, SERVERPORT);
			
			socket.setTcpNoDelay(true);

			try {
				
				outputStream = socket.getOutputStream();
				inputStream = socket.getInputStream();
				byte[] headerBuffer = new byte[8];
				
				while( isRunning ) {
				
					inputStream.read(headerBuffer);	
					final float dataPackageReciveTime = System.nanoTime();
					
					ByteBuffer header = ByteBuffer.wrap(headerBuffer);
					
					int dataPackageLength = header.getInt();
				    dataPackageLength = Math.max(0, dataPackageLength);
				    
				    final float dataPackageSendTime = header.getFloat();

					if( dataPackageLength > 0 ) {
						
						byte[] dataPackageBytes = new byte[dataPackageLength];
					
						inputStream.read(dataPackageBytes);
						DataPackage dataPackageToAdd = new DataPackage(dataPackageBytes, socket.getInetAddress().toString(), socket.getPort(), dataPackageSendTime, dataPackageReciveTime);
						
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

