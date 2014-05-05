package com.pussycat.minions;

import android.util.Log;

import com.pussycat.framework.Graphics;
import com.pussycat.minions.Animation.INTERPOLATION;
import com.pussycat.minions.Animation.TYPE;

public class BallsWidget {
	
	private final int maxNBalls = 5;
	private Ball[] balls = new Ball[maxNBalls];
	private AnimatedValue[] animatedYs = new AnimatedValue[maxNBalls];
	private int activeBalls = maxNBalls;
	
	
	private float SHIFTTIME = 1.0f;
	private final float RADIUS = 0.0075f; // meters
	private final float XMARIGN = 0.05f; // cm
	private final float YMARIGN = 0.05f; // cm
	private final float BALL_MARGIN = 0.1f; // cm
	private final float OFFSET = PussycatMinions.meters2Pixels(- RADIUS - (BALL_MARGIN / 100.0f) + (YMARIGN / 100.0f)); // meters


	private int x = PussycatMinions.meters2Pixels(RADIUS + (XMARIGN / 100.0f));
	private int maxY = PussycatMinions.meters2Pixels((2 * RADIUS + (BALL_MARGIN / 100.0f)) * maxNBalls);
	private int dist = maxY/maxNBalls;
	
	private AnimatedValue adder = new AnimatedValue(0.0f);
	private Animation adderAnimation;
	
	public BallsWidget() {
		
		adderAnimation = new Animation(adder, 0.0f, 1.0f, System.nanoTime(), 1, Animation.INTERPOLATION.FLIP, Animation.TYPE.ENDLESS);
		AnimationHandler.getInstance().addAnimation(adderAnimation);		
		
		for(int i=0; i<activeBalls; i++) {
			balls[i] = new BallRegular(i, x, OFFSET + maxY - i*dist, 0, 0);
			animatedYs[i] = new AnimatedValue(balls[i].getY());
		}
	}
	
	
	public boolean pop() {
		if(!isEmpty()) {
			for(int i=0; i<maxNBalls-1; i++) {
				balls[i] = balls[i+1];
				animatedYs[i] = animatedYs[i+1];
			}
			activeBalls--;
			animateBalls();
			adderAnimation.start();
			return true;
		}
		return false;
	}
	
	
	private void animateBalls() {
		for(int i=0; i<activeBalls; i++) {			
			AnimationHandler.getInstance().addAnimation(new Animation(animatedYs[i],animatedYs[i].getValue(), OFFSET + maxY - i*dist, System.nanoTime(), SHIFTTIME, Animation.INTERPOLATION.COSINE, Animation.TYPE.POINT_TO_POINT));		
		}
	}	

	
	private void addBall(Ball ball) {
		if(!isFull()) {
			balls[activeBalls++] = ball;
			animatedYs[activeBalls-1] = new AnimatedValue(balls[activeBalls-1].getY());
			AnimationHandler.getInstance().addAnimation(new Animation(animatedYs[activeBalls-1], animatedYs[activeBalls-1].getValue(), OFFSET + maxY - (activeBalls-1)*dist, System.nanoTime(), SHIFTTIME, Animation.INTERPOLATION.COSINE, Animation.TYPE.POINT_TO_POINT));
		} 
	}

	
	private boolean isFull() {
		return activeBalls >= maxNBalls;
	}
	
	
	public boolean isEmpty() {
		return activeBalls == 0;
	}
	
	
	public void updateBalls() {
		for(int i=0; i<activeBalls; i++) {
			balls[i].setY((float)animatedYs[i].getValue());
		}
		
		if(adder.getValue() == 1.0f) {
			Log.d("WID", "ADD BALL");
			adder.setValue(0.0f);
			addBall(new BallRegular(0, x, OFFSET + maxY - (maxNBalls-1) * dist, 0, 0));
			if(activeBalls < maxNBalls) {
				adderAnimation.start();
			}
		}
	}
	
	
	public void drawBalls(Graphics graphics) {
		for(int i=0; i<activeBalls; i++) {
			balls[i].draw(graphics);
		}
	}
	
}
