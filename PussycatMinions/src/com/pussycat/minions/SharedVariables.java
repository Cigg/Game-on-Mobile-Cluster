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
	
	
	// =========================================
	// Set methods
	// =========================================
	
	
	public void setInternalState( GLOBAL_STATE__ newInternalState ) {
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
