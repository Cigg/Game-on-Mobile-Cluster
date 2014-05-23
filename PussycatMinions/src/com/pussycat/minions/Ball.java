package com.pussycat.minions;

import com.pussycat.framework.Graphics;
import com.pussycat.framework.Image;

public abstract class Ball {

	protected int id;
	protected int parent;
	
	protected float x;
	protected float y;
	
	protected float vx;
	protected float vy;
	
	protected Image image;
	protected int radius;
	
	protected int type;
	
	
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
			    				 	this.radius * 2, 
			    				 	this.radius * 2, 
			    				 	0, 
			    				 	0, 
			    				 	this.image.getWidth(), 
			    				 	this.image.getHeight(), 0.0f	);		
	}
	
	
	public float getX() {
		return x;
	}
	
	
	public float getY() {
		return y;
	}
	
	
	public void setX(final float x) {
		this.x = x;
	}
	
	
	public void setY(final float y) {
		this.y = y;
	}
}
