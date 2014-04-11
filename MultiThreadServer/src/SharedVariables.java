

import java.util.concurrent.atomic.AtomicInteger;

public class SharedVariables {
	

	private AtomicInteger ballCounter;
	
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
		ballCounter = new AtomicInteger(0);
	}
		
	
	// =========================================
	// Get methods
	// =========================================
		
	public int getBallCounter() {
		return this.ballCounter.get();
	}
	
	
	// =========================================
	// Set methods
	// =========================================
	
	public void setBallCounter(int newNClocks) {
		this.ballCounter.set(newNClocks);
	}
	
	
	public void incrementBallCounter() {
		this.ballCounter.incrementAndGet();
	}

}
