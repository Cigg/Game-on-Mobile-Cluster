package com.pussycat.minions;

public class DataPackage {
	
	private byte[] data;
	private String ip;
	private int port;
	private float sendTime;
	private float reciveTime;
	
	
	public DataPackage(byte[] data, String ip, int port, float sendTime, float reciveTime) {
		this.data = data;
		this.ip = ip;
		this.port = port; 
		this.sendTime = sendTime;
		this.reciveTime = reciveTime;
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
	
	
	public float getSendTime() {
		return this.sendTime;
	}
	
	
	public float getReciveTime() {
		return this.reciveTime;
	}
	
}