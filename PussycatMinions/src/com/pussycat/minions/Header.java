package com.pussycat.minions;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;


public class Header {
	
	private final int HEADER_BUFFER_LENGTH = 8;
	private byte[] headerBuffer = new byte[HEADER_BUFFER_LENGTH];
	
	private final int dataPackageLength;
	private final float dataPackageSendTime;
	
	
	public Header(final int dataPackageLength, final float dataPackageSendTime) {
		this.dataPackageLength = dataPackageLength;
		this.dataPackageSendTime = dataPackageSendTime;
	}
	
	
	public Header(InputStream inputStream) throws IOException {
		inputStream.read(headerBuffer);	
		ByteBuffer header = ByteBuffer.wrap(headerBuffer);
		dataPackageLength = header.getInt(); 
		dataPackageSendTime = header.getFloat();
	}
	
	
	public byte[] getBuffer() {
		ByteBuffer header = ByteBuffer.allocate(HEADER_BUFFER_LENGTH);
		header.putInt(dataPackageLength);
		header.putFloat(dataPackageSendTime);
		return header.array();
	}
	
	
	public boolean isValid() {
		return dataPackageLength > 0;
	}
		
	
	public int getDataPackageLength() {
		return dataPackageLength;
	}
	
	
	public float getDataPackageSendTime() {
		return dataPackageSendTime;
	}
	
}