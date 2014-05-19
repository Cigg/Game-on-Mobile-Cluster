package com.pussycat.minions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

public class TCPClient extends Thread {
	
	private final String SERVER_IP = "192.168.43.122";
	private final int SERVER_PORT = 4444;
	private final int NUMBER_OF_INCOMING_MESSAGES = 32; 
	
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private OutputStream outputStream;
	private InputStream inputStream;
	private Socket socket;
	
	public EndlessQueue<DataPackage> incomingMessages = new EndlessQueue<DataPackage>(new DataPackage[NUMBER_OF_INCOMING_MESSAGES]);
	
	
	public TCPClient() {

	}
	
	
	public boolean isRunning() {
		return isRunning.get();
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
			socket = setUpAndGetSocket();
			setUpStreams(socket);
			setUpRunningThread();
			
			while( isRunning.get() ) {
				Header header = new Header(inputStream);
				if( header.isValid() ) {
					DataPackage dataPackageToAdd = readDataPackage(header);
					addIncomingMessage(dataPackageToAdd);
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
		this.isRunning.set(isRunning);
		synchronized(this) {
			if(isRunning) {
				this.notifyAll();
			}
		}

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
	
	
	private DataPackage readDataPackage(final Header header) throws IOException {
		byte[] dataPackageBytes = new byte[header.getDataPackageLength()];
		inputStream.read(dataPackageBytes);
		return new DataPackage(dataPackageBytes, socket.getInetAddress().toString(), socket.getPort(), header.getDataPackageSendTime(), header.getDataPackageReciveTime());
	}
	
	
	private void addIncomingMessage(final DataPackage dataPackageToAdd) {
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

