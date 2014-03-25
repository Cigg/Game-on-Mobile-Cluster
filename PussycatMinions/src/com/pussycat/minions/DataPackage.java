package com.pussycat.minions;

public class DataPackage {
	
	public DataPackage(byte[] data, String ip, int port) {
		this.data = data;
		this.ip = ip;
		this.port = port;    		
	}
	
	public byte[] getData() {
		return this.data;
	}
	
	public String getIp() {
		return this.ip;
	}
	
	public int getPort() {
		return this.port;
	}
	
	private byte[] data;
	private String ip;
	private int port;
	
}