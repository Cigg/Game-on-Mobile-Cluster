package com.pussycat.minions;

import com.pussycat.framework.Graphics;
import com.pussycat.framework.Image;

import android.R.bool;
import android.graphics.PointF;

public class Target {
	
	Image image;
	private PointF pos;
	private float scale;
	private float radAngle;

	// Height in meters
	public Target(float centerX, float centerY, float height){
		image = Assets.frog;
		scale = PussycatMinions.meters2Pixels(height)/image.getHeight();
		pos = new PointF(centerX-(image.getWidth()/2)*scale, centerY-(image.getHeight()/2)*scale);
	}
	
	public Image getImage(){
		return image;
	}
	
	public float getX() {
		return pos.x;
	}

	public float getY() {
		return pos.y;
	}
	
	public float getImageWidth() {
		return image.getWidth();
	}
	
	public float getImageHeight() {
		return image.getHeight();
	}

	public float getPixelWidth() {
		return image.getWidth()*scale;
	}
	
	public float getPixelHeight() {
		return image.getHeight()*scale;
	}
	
	public float getRadAngle(){
		return radAngle;
	}
	
	public void setRadAngle(float radAngle){
		this.radAngle = radAngle;
	}

	public void drawTarget(Graphics graphics) {
		graphics.drawScaledImage(image, (int)pos.x, (int)pos.y, (int)getPixelWidth(), (int)getPixelHeight(), 0, 0, (int)getImageWidth(), (int)getImageHeight(), SharedVariables.getInstance().getMiddleAngle());
		
	}
}
