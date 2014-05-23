package com.pussycat.minions;

import java.util.Random;

import com.pussycat.framework.Graphics;

import android.graphics.Color;
import android.util.Log;



public class BallRegular extends Ball {

	
	public BallRegular (final int id, final int parent, final float x, final float y, final float vx, final float vy, final int type) {
		this.id = id;
		this.parent = parent;
		
		this.x = x;
		this.y = y;
		
		this.vx = vx;
		this.vy = vy;
		
		this.type = type;
		
		Log.d("RADIUS_2", "PussycatMinions.meters2Pixels(0.0075f); = " + PussycatMinions.meters2Pixels(0.0075f));
		this.radius = PussycatMinions.meters2Pixels(0.0075f);
		
		if(this.type == 1){
			this.image = Assets.ball1;
		} else if(this.type == 2) {
			this.image = Assets.ball2;
		} else if(this.type == 3){
			this.image = Assets.ball3;
		} else {
			this.image = Assets.ball;
		}
	}
	
	public void draw(Graphics graphics) {
		if(parent == -1){
			return;
		}
		
		graphics.drawCircle((int)this.x, (int)this.y, (float)(this.radius*0.95),  SharedVariables.getInstance().getColor(parent));

		
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
	
}
