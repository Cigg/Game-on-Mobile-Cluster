package com.pussycat.minions;

import com.pussycat.framework.Graphics;
import com.pussycat.framework.Image;

public class Button {

	private Image imageNormal;
	private Image imagePressed;
	private int posX, posY;
	private int width, height;
	private boolean visible;
	private boolean enabled;
	private boolean pressed;
	
	public Button(Image imageNormal, Image imagePressed,int posX, int posY){
		this.imageNormal = imageNormal;
		this.imagePressed = imagePressed;
		
		this.posX = posX;
		this.posY = posY;
		this.width = imageNormal.getWidth();
		this.height = imageNormal.getHeight();
		this.visible = true;
		this.enabled = true;
		this.pressed = false;
	}
	
	public void drawButton(Graphics g){
		g.drawImage(getButtonImage(), posX, posY);
	}

	private Image getButtonImage(){
		if(pressed)
			return imagePressed;
		else
			return imageNormal;
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
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
