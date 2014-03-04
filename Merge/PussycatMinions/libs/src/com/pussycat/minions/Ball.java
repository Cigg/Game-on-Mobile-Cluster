package com.pussycat.minions;

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
			checkCollision();
		}
		
	}

	private void checkCollision() {
		// Kod
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
