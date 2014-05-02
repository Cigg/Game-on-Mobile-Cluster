package com.pussycat.minions;

import com.pussycat.framework.Graphics;
import com.pussycat.framework.Image;

public abstract class Ball {

	protected int id;
	
	protected float posX;
	protected float posY;
	
	protected float velX;
	protected float velY;
	
	protected Image image;
	protected int imageWidth, imageHeight;
	protected int radius;
	
	
	public void update(final float timeStep) {
	    this.posX += this.velX * timeStep;
	    this.posY += this.velY * timeStep;  
	}
	
	
	public int getRadius() {
		return this.radius;
	}
	
	
	public void draw(Graphics graphics) {
		
	    graphics.drawScaledImage(	this.image, 
			    				 	(int)this.posX - this.radius, 
			    				 	(int)this.posY - this.radius, 
			    				 	this.radius * 2, 
			    				 	this.radius * 2, 
			    				 	0, 
			    				 	0, 
			    				 	this.imageWidth, 
			    				 	this.imageHeight,
			    				 	0.0f	);		
	}
	
}
