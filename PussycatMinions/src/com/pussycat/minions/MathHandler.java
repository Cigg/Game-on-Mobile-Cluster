package com.pussycat.minions;

public class MathHandler {
	
	
	public MathHandler() {
		
	}
	
	
	public double angle(Vec2d v1, Vec2d v2) {
		return Math.acos( dot(v1, v2) / (v1.length() * v2.length()) );
	}
	
	
	public double dot(Vec2d v1, Vec2d v2) {
		return (v1.x * v2.x) + (v1.y * v2.y);
	}
	

}
