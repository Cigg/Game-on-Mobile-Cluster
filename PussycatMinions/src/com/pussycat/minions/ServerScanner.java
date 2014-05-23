package com.pussycat.minions;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.util.Log;

public class ServerScanner {
	
	final int PORT = 4444;
	final String IP = "237.0.0.1";
	final float SECONDS_TO_WAIT = 1.0f;
	
	public ServerScanner() {
		Log.d("BROAD", "NEW BROADCAST()");
	}
	
	
	private class Sender implements Runnable {

		 MulticastSocket socket;
		 final float startTime;
		 final float durationTime;
		 final DatagramPacket packetToSend;
		 
		 private Sender(MulticastSocket socket, final float startTime, final float durationTime) {
			this.socket = socket;
			this.startTime = startTime; 
			this.durationTime = durationTime; 
			
			final short sendState = (short) GLOBAL_STATE__.HAND_SHAKE.ordinal();
			ByteBuffer buffer = ByteBuffer.allocate(2 + 4*2);
			buffer.clear();
			
			buffer.putShort(sendState);
			
			buffer.putChar('Y');
			buffer.putChar('O');
			buffer.putChar('L');
			buffer.putChar('O');
			
			byte[] sendData = buffer.array();
			
			InetAddress ipGroup = null;
			try {
				ipGroup = InetAddress.getByName(IP);
			} catch (UnknownHostException e) {
				Log.d("BROAD", "ERROR GETTING IPGROUP");
				e.printStackTrace();
			}
			
			Log.d("BROAD", "SENDDATA LENGTH = " + sendData.length);
	        packetToSend = new DatagramPacket(sendData, sendData.length, ipGroup, PORT);
	        
		 }

		public void run() {
	        float timeLeft = durationTime;
	        while(timeLeft > 0) {
			   try {
		        	socket.send(packetToSend);
				} catch (IOException e) {
					Log.d("BROAD", "ERROR SENDING PACKET");
					e.printStackTrace();
				}
			   
			   try {
				   Thread.currentThread().sleep(50);
				} catch (InterruptedException e) {
					Log.d("BROAD", "ERROR SENDING THREAD SLEEP");
					e.printStackTrace();
				}
			   timeLeft = startTime + durationTime - System.nanoTime();
	        };
		}
		 
	 }
	
	public void scanForServers() {
		Log.d("BROAD", "scanForServers");
		SharedVariables.getInstance().clearServers();

	    WifiManager wifi = PussycatMinions.getWifiManger();
	    if(wifi != null) {
	        WifiInfo w = wifi.getConnectionInfo();
	        Log.d("BROAD", w.toString());
	        
		    MulticastLock multicastLock = wifi.createMulticastLock("multicastLock");
		    multicastLock.setReferenceCounted(true);
		    multicastLock.acquire();
				
	        final float startTime = System.nanoTime();
	        final float durationTime = (float) (SECONDS_TO_WAIT * Math.pow(10, 9));
	       
			try {
				MulticastSocket socket = new MulticastSocket(PORT);
		
		        Thread sender = new Thread(new Sender(socket, startTime, durationTime));
		        sender.start();
		     
		       
		        float timeLeft = durationTime;   
		        while(timeLeft > 0) {
	
		 	        try {
		 	        	socket.setSoTimeout((int) (timeLeft * Math.pow(10, -6)));
		 			} catch (SocketException e1) {
		 				Log.d("BROAD", "ERROR SETTING SETSOTIMEOUT");
		 				e1.printStackTrace();
		 			}
		
					try {
						byte[] receiveData = new byte[1024];
						DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
						
						socket.receive(receivePacket);
						
						try{
							ByteBuffer bufferz = ByteBuffer.wrap(receivePacket.getData());
							final short state = bufferz.getShort();
							final short nChars = bufferz.getShort();
							char[] name = new char[nChars];
							
							try{
								for(int c=0; c<nChars; c++) {
									name[c] = bufferz.getChar();
								}
							} catch (Exception e) {
								Log.d("BROAD", "ERROR HANDLING UDP  -   READING CHAR");
							}
							
							String nameStr = String.valueOf(name);
							final int slotsTaken = bufferz.getInt();
							final int numberOfSlots = bufferz.getInt();
							
						
							Log.d("BROAD", "GOT SERVER: " + nameStr+ ", " + receivePacket.getAddress().toString() + ", " + receivePacket.getPort());
							SharedVariables.getInstance().addServer(new Server(nameStr, slotsTaken, numberOfSlots, receivePacket.getAddress().toString(), receivePacket.getPort()));
						} catch (Exception e) {
								Log.d("BROAD", "ERROR HANDLING UDP");
								e.printStackTrace();
						}	
					}  catch (Exception e) {
						Log.d("BROAD", "ERROR RECIVING UDP");
						e.printStackTrace();
					} 				
					
					timeLeft = startTime + durationTime - System.nanoTime();
		    	} 
		        
		      
		    	try {
		    		socket.close();
				} catch(Exception e) {
					Log.d("BROAD", "ERROR CLOSING MULTICAST SOCKET");
				}
		    	
		    	try {
					sender.join();
				} catch (InterruptedException e) {
					Log.d("BROAD", "ERROR JOINING SENDER");
					e.printStackTrace();
				}
		       
		        
		 	   if (multicastLock != null) {
			        multicastLock.release();
			        multicastLock = null;
			   }
	 	   
			} catch(Exception e) {
				Log.d("BROAD", "ERROR CREATING NEW MULTICAST SOCKET");
			}
			
	  } else {
		  Log.d("BROAD", "WifiManager = null");
	  }
	    
 	  Log.d("BROAD", "DONE");
	   
 	  ArrayList<Server> servers = SharedVariables.getInstance().getServers();
 	  Log.d("BROAD", "Servers: ");
 	  for(Server server : servers) {
 		  Log.d("BROAD", server.name + ",  " + server.slotsTaken + "/" + server.numberOfSlots + ",  " + server.ip + ",  " + server.port);
 	  }
 		
	}
	
}
