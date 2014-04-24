package com.pussycat.minions;

public class BallRegular extends Ball {
	
	public BallRegular (final int id, final float x, final float y, final float vx, final float vy) {
		this.id = id;
		
		this.x = x;
		this.y = y;
		
		this.vx = vx;
		this.vy = vy;
		
		this.radius = PussycatMinions.meters2Pixels(0.0075f);
		this.image = Assets.localBall;
	}

}
