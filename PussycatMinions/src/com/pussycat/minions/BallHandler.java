package com.pussycat.minions;

import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import com.pussycat.framework.Graphics;


public class BallHandler {
	
	private ConcurrentHashMap<Integer, Ball> balls = new ConcurrentHashMap<Integer, Ball>();
	private final int screenWidth;
	private final int screenHeight;
    
    
	public BallHandler(final int screenWidth, final int screenHeight) {
		this.screenWidth = screenWidth;	
		this.screenHeight = screenHeight;
	}
	
	
	public void addBall(Ball ball) {
		balls.put(ball.id, ball);
	}


	public void updateBalls(final float timeStep) {
		//forAllBallsCall("update", timeStep);
		
		Enumeration<Integer> enumKey = balls.keys();
		while( enumKey.hasMoreElements() ) {
		    int key = enumKey.nextElement();
		    Ball ball = balls.get(key);		 
			ball.update(timeStep);
		}	
		
	}
	
	
	public void forAllBallsCall(String methodName, Object... arguments) {
		Enumeration<Integer> enumKey = balls.keys();
		while( enumKey.hasMoreElements() ) {
		    int key = enumKey.nextElement();
		    Ball ball = balls.get(key);		 
		    try {
				ball.getClass().getMethod(methodName).invoke(ball, arguments);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}	
	}

	
	public void removeBallsOutOfBounds() {
		Enumeration<Integer> enumKey = balls.keys();
		while( enumKey.hasMoreElements() ) {
		    int key = enumKey.nextElement();
		    Ball ball = balls.get(key);		 
		    removeIfOutOfBounds(ball);
		}	
	}

	
	private void removeIfOutOfBounds(Ball ball) {
		if( isOutOfBounds(ball.x, ball.y, ball.getRadius()) ) {
	    	balls.remove(ball.id);
	    }
	}
	
	
	private boolean isOutOfBounds(final float x, final float y, final float radius) {
		return ( 	x + radius < 0					||
					y + radius < 0 					|| 
					x - radius > this.screenWidth 	|| 
					y - radius > this.screenHeight 		);
	}
	
	
	public void drawBalls(Graphics graphics) {
		Enumeration<Integer> enumKey = balls.keys();
		while( enumKey.hasMoreElements() ) {
		    int key = enumKey.nextElement();
		    Ball ball = balls.get(key);
		    ball.draw(graphics);
		}
	}
	
	
}
