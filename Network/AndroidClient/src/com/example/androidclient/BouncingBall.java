package com.example.androidclient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class BouncingBall extends View{
	private int id;
	
	private float speedX;
	private float speedY;
	
	private float ballRadius = 80;
	private float posX = ballRadius + 20;
	private float posY = ballRadius + 40;
	public RectF ballBounds;
	public Paint paint;
	
	private float previousX;
	private float previousY;
	private float deltaX;
	private float deltaY;
	
	private float currentX;
	private float currentY;
	
	private String data;
	

	public BouncingBall(Context context) {
		super(context);
		ballBounds = new RectF();
		paint = new Paint();
		this.setFocusableInTouchMode(true);
	}
	
	@Override
	public  boolean onTouchEvent(MotionEvent event) {
		posX = event.getX();
		posY = event.getY();
		if(event.getAction() == MotionEvent.ACTION_MOVE) {
			deltaX = currentX - previousX;
			deltaY = currentY - previousY;
		}
		previousX = currentX;
		previousY = currentY;
		return true;
	}
	
	@Override
	protected void onDraw (Canvas canvas) {
		ballBounds.set( posX-ballRadius, posY-ballRadius,
						posX+ballRadius, posY+ballRadius);
		paint.setColor(Color.GREEN);
		canvas.drawOval(ballBounds, paint);
		invalidate();
	}
}