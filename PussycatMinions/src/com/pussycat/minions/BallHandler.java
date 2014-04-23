package com.pussycat.minions;

import java.util.Enumeration;
import java.util.Hashtable;

import com.pussycat.framework.Graphics;


public class BallHandler {
	
	private static Hashtable<Integer, Ball> balls = new Hashtable<Integer, Ball>();
	private static BallType[] ballTypes;
	
	private int screenHeight;
	private int screenWidth;
	
	
	public BallHandler() {
		this.screenHeight = PussycatMinions.getScreenHeight();
		this.screenWidth = PussycatMinions.getScreenWidth();
		
		
		float xdpi = PussycatMinions.getXDPI();
		ballTypes = new BallType[32];
		ballTypes[0] = new BallType(Assets.localBall, Float.parseFloat(PussycatMinions.settings.getSetting("ballRadius")));
	}
	
	
	public void addBall(Ball ball) {
		balls.put(ball.id, ball);
	}
	
	
	private boolean isOutOfBounds( float x, float y, float radius ) {
		return ( 	x + radius < 0					||
					y + radius < 0 					|| 
					x - radius > this.screenWidth 	|| 
					y - radius > this.screenHeight 		);
	}
	
	
	public void updateBalls( float timeStep ) {
		Enumeration<Integer> enumKey = balls.keys();
		
		while( enumKey.hasMoreElements() ) {
		    int key = enumKey.nextElement();
		    Ball ball = balls.get(key);
		    
		    ball.x += ball.vx * timeStep;
		    ball.y += ball.vy * timeStep;  
		    
		    if( isOutOfBounds( ball.x, ball.y, PussycatMinions.meters2Pixels(ballTypes[ball.type].radius ))) {
		    	balls.remove(ball.id);
		    }
		    
		}	
	}
	
	
	public static void drawBalls( Graphics graphics ) {
		Enumeration<Integer> enumKey = balls.keys();

		while( enumKey.hasMoreElements() ) {
		    int key = enumKey.nextElement();
		    Ball ball = balls.get(key);
		   
		    graphics.drawScaledImage(	ballTypes[ball.type].image, 
				    				 	(int)(ball.x - PussycatMinions.meters2Pixels(ballTypes[ball.type].radius)), 
				    				 	(int)(ball.y - PussycatMinions.meters2Pixels(ballTypes[ball.type].radius)), 
				    				 	(int)(PussycatMinions.meters2Pixels(ballTypes[ball.type].radius)*2), 
				    				 	(int)(PussycatMinions.meters2Pixels(ballTypes[ball.type].radius)*2), 
				    				 	0, 
				    				 	0, 
				    				 	128, 
				    				 	128,
				    				 	0.0f);
		}

	}
	
	
}
