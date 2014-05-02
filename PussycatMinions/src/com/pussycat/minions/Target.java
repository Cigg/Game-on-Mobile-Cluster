package com.pussycat.minions;

import com.pussycat.framework.Image;

import android.R.bool;
import android.graphics.PointF;
import android.util.Log;

public class Target {
	
	Image image;
	private PointF pos;
	private float scale;
	private float radAngle;
	private float centerX, centerY;

	// Height in meters
	public Target(float centerX, float centerY, float height){
		image = Assets.frog;
		scale = (float)PussycatMinions.meters2Pixels(height)/image.getHeight();
        radAngle = 0;
        this.centerX = centerX;
        this.centerY = centerY;
		//pos = new PointF(centerX-(image.getWidth()/2)*scale, centerY-(image.getHeight()/2)*scale);
	}
	
	public Image getImage(){
		return image;
	}
	
	public float getX() {
		return (float) (centerX - scale*(image.getWidth()*Math.cos(radAngle) + image.getHeight()*Math.sin(radAngle))/2);
	}

	public float getY() {
		return (float) (centerY - scale*(image.getHeight()*Math.cos(radAngle) + image.getWidth()*Math.sin(radAngle))/2);
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
}
