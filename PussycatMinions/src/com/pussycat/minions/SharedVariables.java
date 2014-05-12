package com.pussycat.minions;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;

public class SharedVariables {
	
	private static SharedVariables theOnlyInstance = null;
	private static Object theOnlyInstanceMutex = new Object();
	
	
	private GLOBAL_STATE__ internalState;
	private Object internalStateMutex = new Object();
	
	private float reciveDelay;
	private Object reciveDelayMutex = new Object();
	
	private float sendDelay;
	private Object sendDelayMutex = new Object();
	
	private AtomicInteger nClocks;	
	
	private float deviceAngle;
	private Object deviceAngleMutex = new Object();
	
	private float deviceMiddleX;
	private Object deviceMiddleXMutex = new Object();
	
	private float deviceMiddleY;
	private Object deviceMiddleYMutex = new Object();

	private float mainMiddleX;
	private Object mainMiddleXMutex = new Object();
	
	private float mainMiddleY;
	private Object mainMiddleYMutex = new Object();
	
	private float middleAngle;
	private Object middleAngleMutex = new Object();
	
	private AtomicInteger[] points;	
	private AtomicInteger totalPoints;	
	private AtomicBoolean updatedPoints;
	private AtomicBoolean isRunning;
	private AtomicBoolean mapDone;
	private AtomicBoolean isRemapping;
	private AtomicBoolean startGame;
	private AtomicInteger numberOfPlayers;
	
	
	// Singleton design pattern
	public static SharedVariables getInstance() {
		if( theOnlyInstance == null ) {
			synchronized( theOnlyInstanceMutex ) {
				if( theOnlyInstance == null ) {
					theOnlyInstance = new SharedVariables();
				}
			}
		}
		return theOnlyInstance;
	}
	
	
	private SharedVariables() {
		initialize();
	}
	
	public void initialize() {
		setInternalState(GLOBAL_STATE__.START);

		setRecivieDelay(0.0f);
		setSendDelay(0.0f);
		
		nClocks = new AtomicInteger(0);
		updatedPoints = new AtomicBoolean(false);
		isRunning = new AtomicBoolean(false);
		mapDone = new AtomicBoolean(false);
		isRemapping = new AtomicBoolean(false);
		startGame = new AtomicBoolean(false);
		numberOfPlayers = new AtomicInteger(10);
		initializePoints((short) numberOfPlayers.get());
	}
	
	public void initializePoints(final short nPlayers) {
		this.numberOfPlayers.set(nPlayers);
		points = new AtomicInteger[nPlayers];
		totalPoints = new AtomicInteger();
		
		for(short i=0; i<nPlayers; i++) {
			points[i] = new AtomicInteger();
		}
		
		/*
		// TODO: FIX
		int totalPoints = 0;
		for(short i=0; i<nPlayers; i++) {
			points[i] = new AtomicInteger();
			int pointz = (int)(Math.random() * 100.0f);
			Log.d("POINTS", "POINTS: " + pointz);
			setPoints(i, pointz);
			totalPoints += pointz;
		}
		setTotalPoints(totalPoints);
		setPointsIsUpdated(true);
		Log.d("POINTS", "totalPoints: " + totalPoints);
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				while(true) {
					AtomicInteger[] points = SharedVariables.getInstance().getPoints();
					AtomicInteger totalPoints = SharedVariables.getInstance().getTotalPoints();
					
					int totalPointsInt = 0;
					for(short i=0; i<SharedVariables.getInstance().getPoints().length; i++) {
						int pointz = (int)(Math.random() * 100.0f);
						SharedVariables.getInstance().setPoints(i, pointz);
						totalPointsInt += pointz;
					}
					SharedVariables.getInstance().setTotalPoints(totalPointsInt);
					SharedVariables.getInstance().setPointsIsUpdated(true);
					try {
						Thread.currentThread().sleep( (int)(Math.random() * 3000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
		});
		t.start();
		*/
		
	}
	
	
	// =========================================
	// Get methods
	// =========================================
	
	public boolean isRunning() {
		return isRunning.get();
	}
	
	
	public boolean shouldStartGame() {
		return startGame.get();
	}
	
	
	public boolean isMapped() {
		return mapDone.get();
	}
	
	
	public boolean isRemapping() {
		return isRemapping.get();
	}
	
	
	public int getNumberOfPlayers() {
		return numberOfPlayers.get();
	}
	
	
	public AtomicInteger[] getPoints() {
		return points;
	}
	
	
	public AtomicInteger getTotalPoints() {
		return totalPoints;
	}
	
	
	public boolean pointsIsUpdated() {
		return updatedPoints.get();
	}
	
	
	public float getDeviceAngle() {
		synchronized( this.deviceAngleMutex ) {
			return this.deviceAngle;
		}
	}
	
	
	public float getDeviceMiddleX() {
		synchronized( this.deviceMiddleXMutex ) {
			return this.deviceMiddleX;
		}
	}
	
	
	public float getDeviceMiddleY() {
		synchronized( this.deviceMiddleYMutex ) {
			return this.deviceMiddleY;
		}
	}
	
	
	public GLOBAL_STATE__ getInternalState() {
		synchronized( this.internalStateMutex ) {
			return this.internalState;
		}
	}
	
	
	public float getRecivieDelay() {
		synchronized( this.reciveDelayMutex ) {
			return this.reciveDelay;
		}
	}
	
	
	public float getSendDelay() {
		synchronized( this.sendDelayMutex ) {
			return this.sendDelay;
		}
	}
	
	public int getNClocks() {
		return this.nClocks.get();
	}
	

	public float getMainMiddleX() {
		synchronized( this.mainMiddleXMutex ) {
			return this.mainMiddleX;
		}
	}


	public float getMainMiddleY() {
		synchronized( this.mainMiddleYMutex ) {
			return this.mainMiddleY;
		}
	}
	
	
	public float getMiddleAngle() {
		synchronized( this.middleAngleMutex ) {
			return this.middleAngle;
		}
	}
	
	
	// =========================================
	// Set methods
	// =========================================
	
	public void setMapDone(final boolean mapDone) {
		this.mapDone.set(mapDone);
	}
	
	
	public void setIsRemapping(final boolean isRemapping) {
		this.isRemapping.set(isRemapping);
	}
	
	
	public void setIsRunning(final boolean isRunning) {
		this.isRunning.set(isRunning);
	}
	
	
	public void setStartGame(final boolean startGame) {
		this.startGame.set(startGame);
	}
	
	
	public void setPoints(final int id, final int points) {
		this.points[id].set(points);
	}


	public void setTotalPoints(final int totalPoints) {
		this.totalPoints.set(totalPoints);
	}
	
	
	public void setPointsIsUpdated(final boolean isUpdated) {
		this.updatedPoints.set(isUpdated);
	}
	
	
	public void setDeviceAngle(float newDeviceAngle) {
		synchronized( this.deviceAngleMutex ) {
			this.deviceAngle = newDeviceAngle;
		}
	}
	
	
	public void setDeviceMiddleX(float newDeviceMiddleX) {
		synchronized( this.deviceMiddleXMutex ) {
			this.deviceMiddleX = newDeviceMiddleX;
		}
	}
	
	
	public void setDeviceMiddleY(float newDeviceMiddleY) {
		synchronized( this.deviceMiddleYMutex ) {
			this.deviceMiddleY = newDeviceMiddleY;
		}
	}
	
	
	public void setMainMiddleX(float newMainMiddleX) {
		synchronized( this.mainMiddleXMutex ) {
			this.mainMiddleX = newMainMiddleX;
		}
	}
	
	
	public void setMainMiddleY(float newMainMiddleY) {
		synchronized( this.mainMiddleYMutex ) {
			this.mainMiddleY = newMainMiddleY;
		}
	}
	
	
	public void setInternalState(GLOBAL_STATE__ newInternalState) {
		synchronized( this.internalStateMutex ) {
			this.internalState = newInternalState;
		}
	}
	
	
	public void setRecivieDelay(float newReciveDelay) {
		synchronized( this.reciveDelayMutex ){
			this.reciveDelay = newReciveDelay;
		}
	}
	
	
	public void setSendDelay(float newSendDelay) {
		synchronized( this.sendDelayMutex ){
			this.sendDelay = newSendDelay;
		}
	}
	
	
	public void setNClocks(int newNClocks) {
		this.nClocks.set(newNClocks);
	}
	
	
	public void incrementNClocks() {
		this.nClocks.incrementAndGet();
	}


	public void setMiddleAngle(final float middleAngle) {
		synchronized( this.middleAngleMutex ){
			this.middleAngle = middleAngle;
		}
	}



}
