package com.pussycat.minions;

import java.util.Enumeration;
import java.util.Hashtable;

import com.pussycat.framework.Graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class BallHandler {
	
	static Hashtable<Integer, Ball> ballz = new Hashtable<Integer, Ball>();
	static Hashtable<Integer, Ball> newBallz = new Hashtable<Integer, Ball>();
	
	int screenHeight;
	int screenWidth;
	
	
	public BallHandler() {
		this.screenHeight = PussycatMinions.getScreenHeight();
		this.screenWidth = PussycatMinions.getScreenWidth();
	}
	
	
	public void addBall(Ball ball) {
		ballz.put(ball.id, ball);
	}
	
	
	private boolean isOutOfBounds( float x, float y ) {
		return ( 	x < 0 ||
				 	y < 0 || 
				 	x > this.screenWidth || 
				 	y > this.screenHeight 		);
	}
	
	
	public void updateBalls( float timeStep ) {
		Enumeration<Integer> enumKey = ballz.keys();
		Log.d("HASH", "UPDATE: " + ballz.size());
		
		while( enumKey.hasMoreElements() ) {
		    Integer key = enumKey.nextElement();
		    Ball ball = ballz.get(key);
		    
		    ball.x += ball.vx * timeStep;
		    ball.y += ball.vy * timeStep;  
		    
		    if( isOutOfBounds(ball.x, ball.y)) {
		    	ballz.remove(ball.id);
		    }
		    
		}		
	}
	
	
	public static void drawBalls( Graphics graphics ) {
		Enumeration<Integer> enumKey = ballz.keys();
		
		Canvas canvas = graphics.getCanvas();
		Paint paint = graphics.getPaint();	
		
		paint.setColor(Color.BLUE);
		int radious = 50;
		 
		while( enumKey.hasMoreElements() ) {
		    Integer key = enumKey.nextElement();
		    Ball ball = ballz.get(key);
		   
		    graphics.drawScaledImage(	Assets.localBall, 
				    				 	(int)ball.x - radious, 
				    				 	(int)ball.y - radious, 
				    				 	100, 
				    				 	100, 
				    				 	0, 
				    				 	0, 
				    				 	128, 
				    				 	128		);
		    
		    
		    /*
		    paint.setColor(Color.BLUE)
		    canvas.drawCircle(	(int)ball.x, 
							 	(int)ball.y, 
							 	50, 
							 	paint 			);

		    */
		    
		    Log.d("HASH", "DRAW: " + ball.id + "  " + ball.x + "  " + ball.y + "  " + ball.vx + "  " + ball.vy);
		}

	}
	
}
