package com.pussycat.minions;

public class Vec2d {
	
	public double x;
	public double y;
	
	
	public Vec2d(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	
	public void rotateClockwise(double a) {
		
		double t = x;
		
		x = t * Math.cos(a) - y * Math.sin(a);
		y = y * Math.sin(a) + t * Math.cos(a);
		
	}
	
	
	public void rotateCounterClockwise(double a) {
		
		double t = x;
		
		x = t * Math.cos(a) + y * Math.sin(a);
		y = - y * Math.sin(a) + t * Math.cos(a);
		
	}
	
	
	public double length() {
		return Math.sqrt( Math.pow(x, 2) +  Math.pow(y, 2) );
	}
	
	
}
