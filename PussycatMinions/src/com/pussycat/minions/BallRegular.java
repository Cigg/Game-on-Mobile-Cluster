package com.pussycat.minions;

import android.util.Log;

public class BallRegular extends Ball {
	
	public BallRegular (final int id, final float x, final float y, final float vx, final float vy) {
		this.id = id;
		
		this.x = x;
		this.y = y;
		
		this.vx = vx;
		this.vy = vy;
		
		Log.d("RADIUS_2", "PussycatMinions.meters2Pixels(0.0075f); = " + PussycatMinions.meters2Pixels(0.0075f));
		this.radius = PussycatMinions.meters2Pixels(0.0075f);
		this.image = Assets.localBall;
	}

}
