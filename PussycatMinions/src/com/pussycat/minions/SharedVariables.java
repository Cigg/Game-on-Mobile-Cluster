package com.pussycat.minions;

import java.util.concurrent.atomic.AtomicInteger;

public class SharedVariables {
	
	private GLOBAL_STATE__ internalState;
	private Object internalStateMutex = new Object();
	
	private float reciveDelay;
	private Object reciveDelayMutex = new Object();
	
	private float sendDelay;
	private Object sendDelayMutex = new Object();
	
	private AtomicInteger nClocks;
	
	private static SharedVariables theOnlyInstance = null;
	private static Object theOnlyInstanceMutex = new Object();
	
	
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
		setInternalState(GLOBAL_STATE__.START);

		setRecivieDelay(0.0f);
		setSendDelay(0.0f);
		
		nClocks = new AtomicInteger(0);
	}
		
	
	// =========================================
	// Get methods
	// =========================================
	
	
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
	
	
	// =========================================
	// Set methods
	// =========================================
	
	
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



}
