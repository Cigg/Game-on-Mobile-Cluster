package com.pussycat.minions;

import com.pussycat.framework.Image;

import android.R.bool;
import android.graphics.PointF;

public class Target {
	
	Image image;
	private PointF pos;
	private float scale;
	
	public Target(float centerX, float centerY, float height){
		image = Assets.frog;
		scale = height/image.getHeight();
		setPos(new PointF(centerX-image.getWidth()/2, centerY-image.getHeight()/2));
	}
	
	public Image getImage(){
		return image;
	}
	
	public PointF getPoint() {
		return pos;
	}
	
	public float getX() {
		return pos.x;
	}

	public float getY() {
		return pos.y;
	}
	
	public float getScale() {
		return scale;
	}
	
	public void setX(float x) {
		this.pos.x = x;
	}

	public void setY(float y) {
		this.pos.y = y;
	}

	public void setPos(PointF pos) {
		this.pos = pos;
	}	
}
