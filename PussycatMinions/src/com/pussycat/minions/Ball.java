package com.pussycat.minions;

public class Ball {

	public final int id;
	public int type;
	
	public float x;
	public float y;
	
	public float vx;
	public float vy;

	
	Ball(final int id, final int type, final float x, final float y, final float vx, final float vy) {
		
		this.id = id;
		this.type = type;
		
		this.x = x;
		this.y = y;
		
		this.vx = vx;
		this.vy = vy;

	}
	
}
