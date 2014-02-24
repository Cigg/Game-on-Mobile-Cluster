package com.example.androidclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.util.Log;

public class TCPClient {
	private String serverMessage;
	public static String SERVERIP = "192.168.43.213";
	public static final int SERVERPORT = 4444;
	private boolean mRun = false;
	private OnMessageReceived messageListner = null;
	
	PrintWriter out;
	BufferedReader in;
	
	public TCPClient(OnMessageReceived listner){
		messageListner = listner;
	}
	
	public void sendMessage(String message) {
		if(out != null && !out.checkError()) {
			out.println(message);
			out.flush();
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
				out = 	new PrintWriter(
						new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())),true);
				in = 	new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				while(mRun) {
					serverMessage = in.readLine();
					if(serverMessage != null && messageListner != null) {
						//System.out.println("Server says: " + serverMessage);
						messageListner.messageReceived(serverMessage);
					}
					serverMessage = null;
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