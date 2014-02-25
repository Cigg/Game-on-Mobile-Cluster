package com.pussycat.minions;

import android.graphics.Point;

import com.pussycat.framework.Image;
import com.pussycat.minions.GameScreen;

public class Ball {

	Image image;
	private int x, y, speedX, speedY;
	private boolean visible;
	
	public Ball(int startX, int startY){
		image = Assets.ball;
		x = startX;
		y = startY;
		speedX = 7;
		speedY = 0;
		visible = true;
	}
	
	public void update(){
		x += speedX;
		if (x > 800){
			visible = false;
		}
		if (visible){
			 //TO-DO: add arguments from BallHandler
			if(checkCollision(null, 5, null, 5)){
				//Delete the connected balls
			}
		}
		
	}

	private boolean checkCollision(Point p1, double r1, Point p2, double r2) {
		// Code example from http://stackoverflow.com/questions/697188/fast-circle-collision-detection
		final double a = r1 + r2;
	    final double dx = p1.x - p2.x;
	    final double dy = p1.y - p2.y;
	    return a * a > (dx * dx + dy * dy);
	}

	public Image getImage(){
		return image;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getSpeedX() {
		return speedX;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
