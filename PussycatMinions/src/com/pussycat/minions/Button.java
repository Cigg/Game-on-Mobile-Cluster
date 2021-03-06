package com.pussycat.minions;

import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.Log;

import com.pussycat.framework.Graphics;
import com.pussycat.framework.Image;
import com.pussycat.framework.Graphics.ImageFormat;
import com.pussycat.framework.implementation.AndroidImage;

public class Button {

	private Image imageNormal;
	private Image imagePressed;
	private int posX, posY;
	private int width, height;
	private boolean visible;
	private boolean enabled;
	private boolean pressed;
	private String string;
	
	private Paint paint;
	private float textHeight;
	
	public Button(Image imageNormal, Image imagePressed, int posX, int posY, Paint paint){
		this.imageNormal = imageNormal;
		this.imagePressed = imagePressed;
		
		this.paint = paint;
		
		this.posX = posX;
		this.posY = posY;
		this.width = imageNormal.getWidth();
		this.height = imageNormal.getHeight();
		this.visible = true;
		this.enabled = true;
		this.pressed = false;
		this.string = "";
		this.textHeight = paint.getTextSize()/2;
	}
	
	public void drawButton(Graphics g){
		//Log.d("Debug Pussycat", "paint: " + paint.getTextSize());
		g.drawImage(getButtonImage(), posX, posY);
		g.drawString(string, posX + width/2, (int)(posY + height/2 + textHeight/2), paint);
	}

	private Image getButtonImage(){
		if(pressed)
			return imagePressed;
		else
			return imageNormal;
	}
	
	/**
	 * Scale button to input width
	 * @param width Button width in pixels
	 */
	public void scaleButton(Graphics g, int pixelWidth) {
		imageNormal = g.newScaledImage(imageNormal, pixelWidth);
		imagePressed = g.newScaledImage(imagePressed, pixelWidth);
		
		this.width = imageNormal.getWidth();
		this.height = imageNormal.getHeight();
	}
	
	public int getX() {
		return posX;
	}

	public int getY() {
		return posY;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public boolean isVisible() {
		return visible;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean inBounds(int x, int y){
		return (x > posX && x < (posX + width) && y > posY && y < (posY + height));
	}

	public void setX(int x) {
		this.posX = x;
	}

	public void setY(int y) {
		this.posY = y;
	}
	
	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}
	
	public boolean isPressed() {
		return this.pressed;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void setText(String string) {
		this.string = string;
	}
	
	public void setTextSize(float textSize) {
		paint.setTextSize(textSize);
		this.textHeight = paint.getTextSize()/2;
	}
}
