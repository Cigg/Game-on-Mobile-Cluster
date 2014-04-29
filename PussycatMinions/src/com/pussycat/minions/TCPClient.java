package com.pussycat.minions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.util.Log;

public class TCPClient extends Thread {
	
	private final String SERVER_IP = "192.168.43.122";
	private final int SERVER_PORT = 4444;
	private final int NUMBER_OF_INCOMING_MESSAGES = 64; 
	
	private volatile boolean isRunning = false;
	private OutputStream outputStream;
	private InputStream inputStream;
	private Socket socket;
	
	public EndlessQueue<DataPackage> incomingMessages = new EndlessQueue<DataPackage>(new DataPackage[NUMBER_OF_INCOMING_MESSAGES]);
	
	
	public TCPClient() {

	}
	
	
	public synchronized void sendData(final byte[] buffer) {
		if( isValid(buffer) ) {	
			try {
				Header header = new Header(buffer.length, System.nanoTime());
				tcpWrite(header.getBuffer(), buffer);	
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static boolean isValid(final byte[] buffer) {
		return buffer.length > 0;
	}
	
	
	public void tcpWrite(final byte[] header, final byte[] buffer) throws IOException {
		if( outputStream != null ) {
			outputStream.write(header);
			outputStream.write(buffer);
			outputStream.flush();
		}	
	}
	
	
	public void run() {
		try {
			setUpRunningThread();
			socket = setUpAndGetSocket();
			setUpStreams(socket);
				
			while( isRunning ) {
				Header header = new Header(inputStream);
				final float dataPackageReciveTime = System.nanoTime();

				if( header.isValid() ) {
					DataPackage dataPackageToAdd = getDataPackageToAdd(header, dataPackageReciveTime);
					addDataPackageToIncomingMessages(dataPackageToAdd);
		        }
			}
		} catch ( Exception e ) {
			Log.e("Android", "ERROR", e);
		} finally {
			cleanUpRunningThread();
		}
	}
			
	
	private void setUpRunningThread() {
		setIsRunning(true);
		Thread.currentThread().setName("TCPClient");
	}
	
	
	public void setIsRunning(final boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	
	private Socket setUpAndGetSocket() throws IOException {
		InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
		Socket socket = new Socket(serverAddress, SERVER_PORT);
		socket.setTcpNoDelay(true);
		return socket;
	}
	
	
	private void setUpStreams(final Socket socket) throws IOException {
		outputStream = socket.getOutputStream();
		inputStream = socket.getInputStream();
	}
	
	
	private DataPackage getDataPackageToAdd(final Header header, final float dataPackageReciveTime) throws IOException {
		byte[] dataPackageBytes = new byte[header.getDataPackageLength()];
		inputStream.read(dataPackageBytes);
		return new DataPackage(dataPackageBytes, socket.getInetAddress().toString(), socket.getPort(), header.getDataPackageSendTime(), dataPackageReciveTime);
	}
	
	
	private void addDataPackageToIncomingMessages(final DataPackage dataPackageToAdd) {
		incomingMessages.add(dataPackageToAdd);
		synchronized( incomingMessages ) {
			incomingMessages.notify();
		};
	}
	
	
	private void cleanUpRunningThread() {
		setIsRunning(false);
		if( socket != null ) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}

