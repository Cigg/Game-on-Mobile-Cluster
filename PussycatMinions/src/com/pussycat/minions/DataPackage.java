package com.pussycat.minions;

public class DataPackage {
	
	private final byte[] data;
	private final String ip;
	private final int port;
	private final float sendTime;
	private final float reciveTime;
	
	
	public DataPackage(final byte[] data, final String ip, final int port, final float sendTime, final float reciveTime) {
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