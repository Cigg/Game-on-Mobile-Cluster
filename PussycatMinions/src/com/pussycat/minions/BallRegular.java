package com.pussycat.minions;

import android.util.Log;

public class BallRegular extends Ball {
	
	public BallRegular (final int id, final float x, final float y, final float vx, final float vy) {
		this.id = id;
		
		this.posX = x;
		this.posY = y;
		
		this.velX = vx;
		this.velY = vy;
		

		Log.d("RADIUS_2", "PussycatMinions.meters2Pixels(0.0075f); = " + PussycatMinions.meters2Pixels(0.0075f));
		this.radius = PussycatMinions.meters2Pixels(0.0075f);

		//this.radius = PussycatMinions.meters2Pixels(Float.parseFloat(PussycatMinions.settings.getSetting("ballRadius")));

		this.image = Assets.localBall;
		this.imageWidth = Assets.localBall.getWidth();
		this.imageHeight = Assets.localBall.getHeight();
	}

}
