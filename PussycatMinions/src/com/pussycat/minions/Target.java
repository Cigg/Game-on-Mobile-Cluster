package com.pussycat.minions;

import com.pussycat.framework.Graphics;
import com.pussycat.framework.Image;
import com.pussycat.framework.implementation.AndroidImage;

import android.R.bool;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

public class Target {
	
	Image image;
	private PointF pos;
	private float scale;
	private float centerX, centerY;
	
	private int screenHeight = PussycatMinions.getScreenHeight();
	private int screenWidth = PussycatMinions.getScreenWidth();
	private int xdpi = PussycatMinions.getXDPI();
	private int ydpi = PussycatMinions.getYDPI();
	private Bitmap octo;
	
	// Height in meters
	public Target(float centerX, float centerY, float targetWidth, Graphics graphics){
		image = Assets.octopus;
		scale = (float)PussycatMinions.meters2Pixels(targetWidth)/(float)image.getWidth();
		//pos = new PointF(centerX-(image.getWidth()/2)*scale, centerY-(image.getHeight()/2)*scale);
	    
		this.centerX = centerX;
        this.centerY = centerY;
        
        createOptimizedBitmap(graphics);
	}
	
	
	public void createOptimizedBitmap(Graphics graphics) {
		Canvas canvas = graphics.getCanvas();

		octo = drawScaledImage(image, (int)getX(), (int)getY(), (int)getPixelWidth(), (int)getPixelHeight(), 0, 0, (int)getImageWidth(), (int)getImageHeight(), SharedVariables.getInstance().getMiddleAngle(), graphics);
	}
	
	
    public Bitmap drawScaledImage(Image Image, int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight, float angle, Graphics graphics){
    	Bitmap bitmap = ((AndroidImage) Image).bitmap;
    	Canvas canvas = graphics.getCanvas();
    	Paint paint = new Paint();
   	 	Matrix matrix = new Matrix();
   	 	
        // calculate the scale
        float scaleWidth = ((float) width) / srcWidth;
        float scaleHeight = ((float) height) / srcHeight;
       
        // create a matrix for the manipulation
        matrix.reset();
        // resize the Bitmap
        matrix.postScale(scaleWidth, scaleHeight);
        // rotate the Bitmap
        float angleInDegrees = angle*(180.0f/3.14f);
        matrix.postRotate(angleInDegrees);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, srcX, srcY, srcWidth, srcHeight, matrix, true);
        
        // x and y are top left coordinates
        // x and y must be calculated if the image isn't square
        // Question: should x and y be center of image instead?
        //canvas.drawBitmap(resizedBitmap, x, y, paint);
        
        return resizedBitmap;
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
		Canvas canvas = graphics.getCanvas();
		canvas.save();
			
		canvas.translate(screenWidth/2, screenHeight/2);
		canvas.rotate(SharedVariables.getInstance().getMiddleAngle()*(180.0f/3.14f));
		canvas.translate(-screenWidth/2, -screenHeight/2);
		
		canvas.drawBitmap(octo, screenWidth/2 -octo.getWidth()/2, screenHeight/2 -octo.getHeight()/2, new Paint());
		canvas.restore();
		
		//graphics.drawScaledImage(image, (int)getX(), (int)getY(), (int)getPixelWidth(), (int)getPixelHeight(), 0, 0, (int)getImageWidth(), (int)getImageHeight(), SharedVariables.getInstance().getMiddleAngle());
	}
}
