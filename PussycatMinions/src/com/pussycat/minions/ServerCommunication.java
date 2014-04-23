package com.pussycat.minions;


import java.nio.ByteBuffer;

import android.util.Log;

public class ServerCommunication extends Thread {

	private volatile boolean communicate;
	private TCPClient tcp;
	private BallHandler ballHandler;
	private BallTypesHandler ballTypesHandler;
	private Target target;

	ServerCommunication(TCPClient stcp, BallHandler ballHandler, Target target ) {
		this.ballHandler = ballHandler;
		this.ballTypesHandler = new BallTypesHandler();
		this.tcp = stcp;
		this.target = target;
		communicate = true;
	}
	

	public void stopCommunication() {
		communicate = false;
	}
	
	
	public void run() {
		Log.d("ADDBALLS", "RUN");
		
		Thread.currentThread().setName("ServerCommunication");
		
		while( communicate ) {

			if( tcp != null ) {
				DataPackage incomingData = (DataPackage)tcp.messages.popFront();
				
				if( incomingData != null ) {
					ByteBuffer buffer = ByteBuffer.wrap(incomingData.getData());
					short state = buffer.getShort();
					
					GLOBAL_STATE__ actualState;
					
					try {
						actualState = GLOBAL_STATE__.values()[state];
					} catch(Exception e) {
						actualState = GLOBAL_STATE__.REG; // Do nothing
					}
					
					switch( actualState ) {
					
						case SYNCHRONIZE_DEVICE: {		   
		    				synchronizeDevice( incomingData );
		    			} break;
		    				
						case ADD_BALLS: {
							addBalls( incomingData );
						} break;
						
						case SET_STATE: {
							setState( incomingData );
						} break;
		    			
						case ADD_MAP: {
							addMap( incomingData );
						} break;

		    			default:
		    			break;
		    			
					}
					
				} else {
	    			synchronized( tcp.messages ) {
						try {
							tcp.messages.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					};
	        	}
			} 	
			
		}
	}
	
	private void addMap(DataPackage incomingData) {
		ByteBuffer buffer = ByteBuffer.wrap(incomingData.getData());
		short state = buffer.getShort();
		
		float angle = buffer.getFloat();
		// TODO: Make function convertFromRadiansToDegrees
		angle = (float) (angle * 180 / Math.PI);
		
		float deviceMiddleX = buffer.getFloat();
		float deviceMiddleY = buffer.getFloat();
		float mainMiddleX = buffer.getFloat();
		float mainMiddleY = buffer.getFloat();
		
		SharedVariables.getInstance().setDeviceAngle(angle);
		SharedVariables.getInstance().setDeviceMiddleX(deviceMiddleX);
		SharedVariables.getInstance().setDeviceMiddleY(deviceMiddleY);
		SharedVariables.getInstance().setMainMiddleX(mainMiddleX);
		SharedVariables.getInstance().setMainMiddleY(mainMiddleY);
		
		//SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.REG);
		Device.setOnce();
		Log.d("ADDMAP", "GOT: " + angle + "  " + deviceMiddleX + "  " + deviceMiddleY + "  " + mainMiddleX + "  " + mainMiddleY);
	}
	
	
	private void setState(DataPackage incomingData) {
		
		ByteBuffer buffer = ByteBuffer.wrap(incomingData.getData());
		short state = buffer.getShort();
		
		short newState = buffer.getShort();
				
		GLOBAL_STATE__ newInternalState;
	
		try {
			newInternalState = GLOBAL_STATE__.values()[newState];
		} catch(Exception e) {
			newInternalState = SharedVariables.getInstance().getInternalState();
		}
		
		SharedVariables.getInstance().setInternalState(newInternalState);
	}


	private void addBalls(DataPackage incomingData) {
		ByteBuffer buffer = ByteBuffer.wrap(incomingData.getData());
		short state = buffer.getShort();
		
		final short nBalls = buffer.getShort();
		
		for(int i=0; i<nBalls; i++) {
			int id = buffer.getInt();
			float x = buffer.getFloat();
        	float y = buffer.getFloat();	
			float vx = buffer.getFloat();	
			float vy = buffer.getFloat();					        			
			Log.d("BALLINFO", "ADD: " + id + "  " + x + "  " + y + "  " + vx + "  " + vy);
			
			// TODO: FIX ALLL BALL TYPES
			ballHandler.addBall(new BallRegular(id, x, y, vx, vy));			
			//SharedVariables.getInstance().setMiddleAngle(middleAngle);

		}
		
		/*
		if(target != null) {
			target.setAngle(middleAngle);
		}
		
		float targetAngle = buffer.getFloat();
		target.setRadAngle(targetAngle);
		*/
	}


	private void synchronizeDevice(DataPackage incomingData) {
		ByteBuffer buffer = ByteBuffer.wrap(incomingData.getData());
		short state = buffer.getShort();
		
		float t1 = buffer.getFloat();
		float t2 = buffer.getFloat();
		float t3 = incomingData.getSendTime();
		float t4 = incomingData.getReciveTime();
							
		SharedVariables.getInstance().incrementNClocks();
		SharedVariables.getInstance().setSendDelay(t2 - t1);
		SharedVariables.getInstance().setRecivieDelay(-(t4 - t3));
	}
	
}
