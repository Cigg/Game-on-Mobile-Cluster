package com.pussycat.minions;

import java.util.Random;

import android.util.Log;

public class BallRegular extends Ball {
	
	public BallRegular (final int id, final int parent, final float x, final float y, final float vx, final float vy) {
		this.id = id;
		this.parent = parent;
		
		this.x = x;
		this.y = y;
		
		this.vx = vx;
		this.vy = vy;
		
		Log.d("RADIUS_2", "PussycatMinions.meters2Pixels(0.0075f); = " + PussycatMinions.meters2Pixels(0.0075f));
		this.radius = PussycatMinions.meters2Pixels(0.0075f);
		//this.image = Assets.localBall;
		
		Random generator = new Random(); 
		int randType = generator.nextInt(2) + 1;
		
		this.type = randType;
		
		if(randType == 1){
			this.image = Assets.ball1;
		} else if (randType == 2) {
			this.image = Assets.ball2;
		} else {
			this.image = Assets.ball3;
			this.type = 3;
		}
	}

}
