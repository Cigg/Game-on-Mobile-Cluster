package com.pussycat.minions;


import java.nio.ByteBuffer;

import android.util.Log;

public class ServerCommunication extends Thread {

	private volatile boolean isCommunicating;
	private TCPClient tcp;
	private BallHandler ballHandler;
	private BallTypesHandler ballTypesHandler;
	private Target target;

	
	ServerCommunication(TCPClient stcp, BallHandler ballHandler, Target target ) {
		this.ballHandler = ballHandler;
		this.ballTypesHandler = new BallTypesHandler();
		this.tcp = stcp;
		this.target = target;
		isCommunicating = true;
	}

	
	public void run() {
		Thread.currentThread().setName("ServerCommunication");
		
		while( isCommunicating ) {

			if( tcp != null ) {
				DataPackage incomingData = tcp.incomingMessages.popFront();
				
				if( incomingData != null ) {
					ByteBuffer buffer = ByteBuffer.wrap(incomingData.getData());
					final short state = buffer.getShort();
					
					GLOBAL_STATE__ actualState;
					
					try {
						actualState = GLOBAL_STATE__.values()[state];
					} catch(Exception e) {
						actualState = GLOBAL_STATE__.REG;
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
						
						case SET_MIDDLE_ANGLE: {
							setMiddleAngle( incomingData );
						} break;
						
						case SET_POINTS: {
							setPoints( incomingData );
						} break;
						
		    			default:
		    			break;
		    			
					}			
				} else {
	    			synchronized( tcp.incomingMessages ) {
						try {
							tcp.incomingMessages.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					};
	        	}
			} 	
			
		}
	}
	
	
	public void setIsCommunicating(final boolean isCommunicating) {
		this.isCommunicating = isCommunicating;
	}
	
	
	private void setPoints(DataPackage incomingData) {
		ByteBuffer buffer = ByteBuffer.wrap(incomingData.getData());
		final short state = buffer.getShort();
		
		final short nPlayers = buffer.getShort();
		
		int totalPoints = 0;
		for(short i=0; i<nPlayers; i++) {
			final int points = buffer.getInt();
			SharedVariables.getInstance().setPoints(i, points);
			totalPoints += points;
		}
		SharedVariables.getInstance().setTotalPoints(totalPoints);
		SharedVariables.getInstance().setPointsIsUpdated(true);
	}
	
	
	private void setMiddleAngle(DataPackage incomingData) {
		ByteBuffer buffer = ByteBuffer.wrap(incomingData.getData());
		final short state = buffer.getShort();
		
		final float angle = buffer.getFloat();
		
		Log.d("GOTANGLE", "GOTANGLE: " + angle);
		SharedVariables.getInstance().setMiddleAngle(angle);		
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
