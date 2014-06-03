package com.pussycat.minions;

public class TestObject {

	private int x;
	private int y;
	
	private AnimatedValue xx;
	private AnimatedValue yy;
	
	public TestObject(int x, int y) {
		this.x = x;
		this.y = y;
		
		xx = new AnimatedValue(x);
		yy = new AnimatedValue(x);
	}
	
	
	public AnimatedValue getXX() {
		return xx;
	}
	
	public AnimatedValue getYY() {
		return yy;
	}
	
	
	public int getX() {
		return x;
	}
	
	
	public int getY() {
		return y;
	}
}
