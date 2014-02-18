package com.mikael.GameObjects;

import com.badlogic.gdx.math.Vector2;

public class Boll {
	private Vector2 prevPosition;
	private Vector2 position;
	private Vector2 velocity;
	private Vector2 acceleration;

	private int width;
	private int height;
	
	private boolean isPressed;
	
	 public Boll(float x, float y, int radius) {
		isPressed = false;
        this.width = radius*2;
        this.height = radius*2;
        prevPosition = new Vector2(x,y);
        position = new Vector2(x, y);
        velocity = new Vector2(prevPosition.x - position.x, prevPosition.y - position.y);
        acceleration = new Vector2(0, 0);
    }
	    public void update(float delta) {
	        position.add(velocity.cpy().scl(delta));
	    }

    public void onPressed() {
        isPressed = true;
    }
	    
    public void onDragged(int pressedX, int pressedY) {
    	System.out.println("onDragged");
    	System.out.println("position.x: " + position.x);
    	System.out.println("prevPosition.x: " + prevPosition.x);
    	prevPosition = position.cpy();
        position.x = pressedX;
        position.y = pressedY;
    }
    
    public void onReleased(int pressedX, int pressedY)
    {
    	System.out.println("onReleased");
    	//System.out.println("position.x: " + position.x);
    	//System.out.println("prevPosition.x: " + prevPosition.x);
    	velocity.x = 10*(position.x - prevPosition.x);
    	velocity.y = 10*(position.y - prevPosition.y);
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
