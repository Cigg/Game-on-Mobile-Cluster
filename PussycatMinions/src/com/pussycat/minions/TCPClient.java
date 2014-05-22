package com.pussycat.minions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.pussycat.minions.Device.IndexPair;

import android.graphics.Bitmap;
import android.util.Log;

public class TCPClient extends Thread {
	

	//private final String SERVER_IP = "192.168.43.122";
	private String SERVER_IP;
	
	//private final static int SERVER_PORT = 4444;
	private int SERVER_PORT;

	private final int NUMBER_OF_INCOMING_MESSAGES = 32; 
	final static int TIME_OUT = 200;
	
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private OutputStream outputStream;
	private InputStream inputStream;
	private Socket socket;
	
	public EndlessQueue<DataPackage> incomingMessages = new EndlessQueue<DataPackage>(new DataPackage[NUMBER_OF_INCOMING_MESSAGES]);
	
	
	public TCPClient() {
		Server server = SharedVariables.getInstance().getServer();
		SERVER_IP = server.ip;
		SERVER_PORT = server.port;
	}
	
	
	public static class Executor implements Runnable {

		final int begin;
		final int end;
		
		public Executor(final int begin, final int end) {
			//Log.d("BROAD", "BEG = " + begin + ", END = " + end);
			this.begin = begin;
			this.end = end;
		}
		
		public void run() {

			for(int i=begin; i<end; i++) {
				for(int j=0; j<256; j++) {
					Log.d("BROAD", "loop: " + i + "." + j);
					try{
						String ip = "192.168".concat("." + i + "." + j);
						Socket trySocket = new Socket();
						trySocket.connect(new InetSocketAddress(ip, 4444), 10);
						if (trySocket.isConnected()) {
							Log.d("BROAD", "Found: " + ip);
						//	SERVER_IP = ip;
							return;	
						}
						trySocket.close();
					} catch (Exception e) {
					} 
				}
			}
			
		}
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

