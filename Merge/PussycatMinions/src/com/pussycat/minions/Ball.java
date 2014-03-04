package com.pussycat.minions;

import android.graphics.PointF;

import com.pussycat.framework.Image;
import com.pussycat.minions.GameScreen;

public class Ball {

	Image image;
	private PointF pos;
	private float speedX, speedY, diameter;
	//private boolean visible;
	private boolean locked;
	private int color;
	
	
	public Ball(float startX, float startY, float diameter){
		image = Assets.ball;
		pos = new PointF(startX, startY);
		speedX = 0;
		speedY = 0;
		this.diameter = diameter;
		locked = true;
	}
	
	public Ball(float startX, float startY, float diameter, float speedX, float speedY){
		image = Assets.ball;
		pos = new PointF(startX, startY);
		(this).speedX = speedX;
		(this).speedY = speedY;
		this.diameter = diameter;
		locked = false;
	}
	
	public void update(){
		if(!locked){
			pos.x += speedX;
			pos.y += speedY;
		}
	}

	public Image getImage(){
		return image;
	}
	
	public PointF getPoint(){
		return pos;
	}
	
	public float getX() {
		return pos.x;
	}

	public float getY() {
		return pos.y;
	}
	
	public float getDiameter(){
		return diameter;
	}
	
	public float getRadius(){
		return diameter/2;
	}

	public float getSpeedX() {
		return speedX;
	}

	/*
	public boolean isVisible() {
		return visible;
	}
	*/

	public void setX(float x) {
		this.pos.x = x;
	}

	public void setY(float y) {
		this.pos.y = y;
	}

	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	/*
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	*/
}