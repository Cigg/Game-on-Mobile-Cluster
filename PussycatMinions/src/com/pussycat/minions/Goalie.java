package com.pussycat.minions;

import com.pussycat.framework.Image;

import android.R.bool;
import android.graphics.PointF;

public class Goalie {
	
	/*
	 * As of now, this is more or less only a copy from class Ball.
	 */
	
	Image image;
	private PointF pos;
	private float diameter;
	// private float direction;
	// private float goalOpeningWidth; //the distance between the two corners that creates the opening
	// private bool goal;
	
	public Goalie(float startX, float startY, float diameter){
		image = Assets.hole;
		setPos(new PointF(startX, startY));
		this.setDiameter(diameter);
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
	
	public void setX(float x) {
		this.pos.x = x;
	}

	public void setY(float y) {
		this.pos.y = y;
	}

	public void setPos(PointF pos) {
		this.pos = pos;
	}

	public float getDiameter() {
		return diameter;
	}

	public void setDiameter(float diameter) {
		this.diameter = diameter;
	}
	
}
