package com.pussycat.minions;


import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

public class ServerCommunication extends Thread {

	private AtomicBoolean isCommunicating = new AtomicBoolean(false);
	private TCPClient tcp;
	private BallHandler ballHandler;
	private BallTypesHandler ballTypesHandler;
	private Target target;

	
	ServerCommunication(TCPClient stcp, BallHandler ballHandler, Target target ) {
		this.ballHandler = ballHandler;
		this.ballTypesHandler = new BallTypesHandler();
		this.tcp = stcp;
		this.target = target;
	}

	
	public boolean isCommunicating() {
		return isCommunicating.get();
	}
	
	
	public void run() {
		Thread.currentThread().setName("ServerCommunication");
		
		setIsCommunicating(true);
		
		while( isCommunicating.get()) {

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
						
						case START_GAME: {
							startGame( incomingData );
						}
						
						case HAND_SHAKE: {
							shakeHands( incomingData );
						} break;
						
						case GAME_OVER: {
							gameOver( incomingData );
						} break;
						
						case ADD_DEVICE: {
							addDevice( incomingData );
						}
						
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
		this.isCommunicating.set(isCommunicating);
		synchronized(this) {
			if(isCommunicating) {
				this.notifyAll();
			}
		}
	}
	
	
	public void addDevice(DataPackage incomingData) {
		ByteBuffer buffer = ByteBuffer.wrap(incomingData.getData());
		final short state = buffer.getShort();
		final short deviceId = buffer.getShort();
		
		SharedVariables.getInstance().setDeviceId(deviceId);
	}
	
	
	private void gameOver(DataPackage incomingData) {
		ByteBuffer buffer = ByteBuffer.wrap(incomingData.getData());
		final short state = buffer.getShort();
		final short nPlayers = buffer.getShort();
		
		SharedVariables.getInstance().clearScores();
		
		for(short i=0; i<nPlayers; i++) {
			final short id = buffer.getShort();
			final int points = buffer.getInt();
			SharedVariables.getInstance().addScore(new ShortPair(id, points));
		}
		
		SharedVariables.getInstance().setIsRunning(false);
	}
	
	private void shakeHands(DataPackage incomingData) {
		ByteBuffer buffer = ByteBuffer.wrap(incomingData.getData());
		final short state = buffer.getShort();
		final short nChars = buffer.getShort();
		char[] name = new char[20];
		
		try{
			for(short c=0; c<nChars; c++) {
				name[c] = buffer.getChar();
			}
		} catch (Exception e) {
			Log.d("BROAD", "ERROR HANDLING UDP  -   READING CHAR");
		}
		
		String nameStr = String.valueOf(name);
		
		Log.d("BROAD", "GOT SERVER: " + nameStr+ ", " + incomingData.getIp() + ", " + incomingData.getPort());
		
		//SharedVariables.getInstance().addServer(new Server(nameStr, incomingData.getIp(), incomingData.getPort()));
	}
		
	
	private void startGame(DataPackage incomingData) {
		ByteBuffer buffer = ByteBuffer.wrap(incomingData.getData());
		final short state = buffer.getShort();
		final short gameTimeInSeconds = buffer.getShort();
		SharedVariables.getInstance().setGameTimeInSeconds(gameTimeInSeconds);
		
		final short nPlayers = buffer.getShort();
		
		for(short i=0; i<nPlayers; i++) {
			final short id = buffer.getShort();
			final short color = buffer.getShort();
			Log.d("SOON", "START GOT: id = " + id + " ,,  " + color);
			SharedVariables.getInstance().setColor(id, color);
		}
				
		SharedVariables.getInstance().initializePoints(nPlayers);
		SharedVariables.getInstance().setStartGame(true);
	}
	
	
	private void setPoints(DataPackage incomingData) {
		ByteBuffer buffer = ByteBuffer.wrap(incomingData.getData());
		final short state = buffer.getShort();
		final short nPlayers = buffer.getShort();
		
		int totalPoints = 0;
		for(short i=0; i<nPlayers; i++) {
			final short id = buffer.getShort();
			final int points = buffer.getInt();
			SharedVariables.getInstance().setPoints(id, points);
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
		
		SharedVariables.getInstance().setMapDone(true);
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
			int parent = buffer.getInt();
			Log.d("PARENT" ,"PARENT = " + parent);
			float x = buffer.getFloat();
        	float y = buffer.getFloat();	
			float vx = buffer.getFloat();	
			float vy = buffer.getFloat();					        			
			Log.d("BALLINFO", "ADD: " + id + "  " + x + "  " + y + "  " + vx + "  " + vy);
			
			// TODO: FIX ALLL BALL TYPES
			ballHandler.addBall(new BallRegular(id, parent, x, y, vx, vy));			
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
