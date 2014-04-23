package com.pussycat.minions;

import com.pussycat.framework.Graphics;
import com.pussycat.framework.Image;

public abstract class Ball {

	protected int id;
	
	protected float x;
	protected float y;
	
	protected float vx;
	protected float vy;
	
	protected Image image;
	protected int radius;
	
	
	public void update(final float timeStep) {
	    this.x += this.vx * timeStep;
	    this.y += this.vy * timeStep;  
	}
	
	
	public int getRadius() {
		return this.radius;
	}
	
	
	public void draw(Graphics graphics) {
	    graphics.drawScaledImage(	this.image, 
			    				 	(int)this.x - this.radius, 
			    				 	(int)this.y - this.radius, 
			    				 	100, 
			    				 	100, 
			    				 	0, 
			    				 	0, 
			    				 	128, 
			    				 	128, 0.0f	);		
	    /*
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
	    */
	}
	
}
