package com.pussycat.minions;

import com.pussycat.framework.Graphics;
import com.pussycat.framework.Image;

import android.R.bool;
import android.graphics.PointF;
import android.util.Log;

public class Target {
	
	Image image;
	private PointF pos;
	private float scale;
	private float centerX, centerY;

	// Height in meters
	public Target(float centerX, float centerY, float targetWidth){
		image = Assets.octopus;
		scale = (float)PussycatMinions.meters2Pixels(targetWidth)/(float)image.getWidth();
		//pos = new PointF(centerX-(image.getWidth()/2)*scale, centerY-(image.getHeight()/2)*scale);
	    
		this.centerX = centerX;
        this.centerY = centerY;
	}
	
	public Image getImage(){
		return image;
	}
	
	public float getX() {
		return (float) (centerX - scale*(image.getWidth()*Math.abs(Math.cos(SharedVariables.getInstance().getMiddleAngle())) + image.getHeight()*Math.abs(Math.sin(SharedVariables.getInstance().getMiddleAngle())))/2);
	}

	public float getY() {
		return (float) (centerY - scale*(image.getHeight()*Math.abs(Math.cos(SharedVariables.getInstance().getMiddleAngle())) + image.getWidth()*Math.abs(Math.sin(SharedVariables.getInstance().getMiddleAngle())))/2);
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

	public void drawTarget(Graphics graphics) {
		graphics.drawScaledImage(image, (int)getX(), (int)getY(), (int)getPixelWidth(), (int)getPixelHeight(), 0, 0, (int)getImageWidth(), (int)getImageHeight(), SharedVariables.getInstance().getMiddleAngle());
		
	}
}
