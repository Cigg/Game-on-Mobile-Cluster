package com.pussycat.minions;

import android.graphics.PointF;
import android.util.Log;

import com.pussycat.framework.Image;

public class Ball {

	Image image;
	private int id;
	private PointF pos;
	private float speedX, speedY, diameter;
	//private boolean visible;
	private boolean locked;
	private int color;
	
	private float newX, newY, oldX, oldY, newSpeedX, newSpeedY, oldSpeedX, oldSpeedY, newTime, oldTime;
	
	
	public Ball(float startX, float startY, float diameter){
		image = Assets.ball;
		pos = new PointF(startX, startY);
		speedX = 0;
		speedY = 0;
		this.diameter = diameter;
		locked = true;
	}
	
	public Ball(float startX, float startY, float diameter, float speedX, float speedY, boolean local){
		if(local) {
			image = Assets.localBall;
		} else {
			image = Assets.ball;
		}
		pos = new PointF(startX, startY);
		this.speedX = speedX;
		this.speedY = speedY;
		this.diameter = diameter;
		locked = false;
	}
	
	public Ball(int id, float startX, float startY, float diameter, float speedX, float speedY){
		image = Assets.ball;
		this.id = id;
		pos = new PointF(startX, startY);
		this.speedX = speedX;
		this.speedY = speedY;
		this.diameter = diameter;
		locked = false;
	}
	
	public Ball(int id, float startX, float startY, float diameter, float speedX, float speedY, boolean local, float time){
		
		if(local) {
			image = Assets.localBall;
		} else {
			image = Assets.ball;
		}
		this.id = id;
		pos = new PointF(startX, startY);
		this.speedX = speedX;
		this.speedY = speedY;
		this.diameter = diameter;
		locked = false;
		
		this.newX = startX;
		this.newY = startY;
		this.newSpeedX = speedX;
		this.newSpeedY = speedY;
		this.newTime = time;

	}
	
	public void update(float reciveDelay){
			/*
			pos.x = (int) (pos.x + speedX * timeDelta);
			pos.y = (int) (pos.y + speedY * timeDelta);
			*/
		
		float untilNowFromLast = System.nanoTime() + reciveDelay - this.oldTime;
		float deltaTime = this.newTime - this.oldTime;
		
		
		float scale = untilNowFromLast / deltaTime;
		
		Log.d("SCALE", "SCALE = " + scale);
		
	//	if(scale <= 1) {
		/*
			pos.x = cubicInterpolation(oldX, oldX + oldSpeedX * deltaTime, newX - newSpeedX * deltaTime, newX, scale);
			pos.y = cubicInterpolation(oldY, oldY + oldSpeedY * deltaTime, newY - newSpeedY * deltaTime, newY, scale);
			speedX = cosineInterpolation(oldSpeedX, newSpeedX, scale);
			speedY = cosineInterpolation(oldSpeedY, newSpeedY, scale);
		*/
		
			pos.x = linearInterpolation(oldX, newX, scale);
			pos.y = linearInterpolation(oldY, newY, scale);
			speedX = linearInterpolation(oldSpeedX, newSpeedX, scale);
			speedY = linearInterpolation(oldSpeedY, newSpeedY, scale);
	//	}
		
		
	}
	
	float linearInterpolation(float x1, float x2, float scale) {
		return x1 * (1 - scale) + x2 * scale;
	}
	
	float cosineInterpolation(float x1, float x2, float scale) {
		float scaleModified = (float) ((1 - Math.cos(scale * Math.PI)) / 2);
		return x1 * (1 - scaleModified) + x2 * scaleModified;
	}
	
	float cubicInterpolation(float x0, float x1, float x2, float x3, float scale) {
		float a0, a1, a2, a3, scale2;
		scale2 = scale * scale;
		a0 = x3 - x2 - x0 + x1;
		a1 = x0 - x1 - a0;
		a2 = x2 - x0;
		a3 = x1;
		return (a0*scale*scale2 + a1*scale2 + a2*scale + a3); 
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

	public float getSpeedY() {
		return speedY;
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
	

	public void setPos(float x, float y) {
		this.pos.x = x;
		this.pos.y = y;
	}

	public void updateBall(float posX, float posY, float speedX, float speedY){
		this.pos.x = posX;
		this.pos.y = posY;
		this.speedX = speedX;
		this.speedY = speedY;
	}
	
	public void updatePos(float x, float y, float speedX, float speedY, float time) {
		this.oldX = this.newX;
		this.oldY = this.newY;
		this.oldSpeedX = this.newSpeedX;
		this.oldSpeedY = this.newSpeedY;
		this.oldTime = this.newTime;
		
		this.newX = x;
		this.newY = y;
		this.newSpeedX = speedX;
		this.newSpeedY = speedY;
		this.newTime = time;
	}
	
	
	public int getId() {
		return id; 
	}
	/*
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	*/
}
