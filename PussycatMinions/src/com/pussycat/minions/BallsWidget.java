package com.pussycat.minions;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import android.util.Log;

import com.pussycat.framework.Graphics;

public class BallsWidget implements Widget {
	
	private final int maxNBalls = 5;
	private Ball[] balls = new Ball[maxNBalls];
	private AnimatedValue[] animatedYs = new AnimatedValue[maxNBalls];
	private int activeBalls = maxNBalls;
	
	private final float COOL_TIME = 1.0f; // seconds
	private final float SHIFT_TIME = 1.0f; // seconds
	private final float RADIUS = 0.0075f; // meters
	private final float X_MARIGN = 0.05f; // cm
	private final float Y_MARIGN = 0.05f; // cm
	private final float BALL_MARGIN = 0.1f; // cm
	private final float OFFSET = PussycatMinions.meters2Pixels(- RADIUS - (BALL_MARGIN / 100.0f) + (Y_MARIGN / 100.0f)); // pixels


	private int x = PussycatMinions.meters2Pixels(RADIUS + (X_MARIGN / 100.0f));
	private int maxY = PussycatMinions.meters2Pixels((2 * RADIUS + (BALL_MARGIN / 100.0f)) * maxNBalls);
	private int dist = maxY/maxNBalls;
	
	private AnimatedValue adder = new AnimatedValue(0.0f);
	private Animation adderAnimation;
	
	private Random rand = new Random();
	
	public BallsWidget() {
		
		adderAnimation = new Animation(adder, 0.0f, 1.0f, System.nanoTime(), COOL_TIME, Animation.INTERPOLATION.FLIP, Animation.TYPE.ENDLESS);
		AnimationHandler.getInstance().addAnimation(adderAnimation);		
		
		for(int i=0; i<activeBalls; i++) {
			int type = rand.nextInt(3)+1;
			Log.d("Queue", "New type1: " + type);
			balls[i] = new BallRegular(i, SharedVariables.getInstance().getDeviceId(), x, OFFSET + maxY - i*dist, 0, 0,type, PussycatMinions.meters2Pixels(0.0025f));
			animatedYs[i] = new AnimatedValue(balls[i].getY());
		}
	}
	
	
	public int pop() {
		if(!isEmpty()) {
			int type = balls[0].type;
			for(int i=0; i<maxNBalls-1; i++) {
				balls[i] = balls[i+1];
				animatedYs[i] = animatedYs[i+1];
			}
			activeBalls--;
			animateBalls();
			adderAnimation.start();
			
			return type;
		}
		return 0;
	}
	
	
	private void animateBalls() {
		for(int i=0; i<activeBalls; i++) {			
			AnimationHandler.getInstance().addAnimation(new Animation(animatedYs[i],animatedYs[i].getValue(), OFFSET + maxY - i*dist, System.nanoTime(), SHIFT_TIME, Animation.INTERPOLATION.COSINE, Animation.TYPE.POINT_TO_POINT));		
		}
	}	

	
	private void addBall(Ball ball) {
		if(!isFull()) {
			balls[activeBalls++] = ball;
			animatedYs[activeBalls-1] = new AnimatedValue(balls[activeBalls-1].getY());
			AnimationHandler.getInstance().addAnimation(new Animation(animatedYs[activeBalls-1], animatedYs[activeBalls-1].getValue(), OFFSET + maxY - (activeBalls-1)*dist, System.nanoTime(), SHIFT_TIME, Animation.INTERPOLATION.COSINE, Animation.TYPE.POINT_TO_POINT));
		} 
	}

	
	private boolean isFull() {
		return activeBalls >= maxNBalls;
	}
	
	
	public boolean isEmpty() {
		return activeBalls == 0;
	}
	
	
	public void update() {
		for(int i=0; i<activeBalls; i++) {
			balls[i].setY((float)animatedYs[i].getValue());
		}
		
		if(adder.getValue() == 1.0f) {
			Log.d("WID", "ADD BALL");
			adder.setValue(0.0f);
			int type = rand.nextInt(3)+1;
			Log.d("Queue", "New type2: " + type);
			addBall(new BallRegular(0, SharedVariables.getInstance().getDeviceId(), x, OFFSET + maxY - (maxNBalls-1) * dist, 0, 0,type, PussycatMinions.meters2Pixels(0.0025f)));
			if(activeBalls < maxNBalls) {
				adderAnimation.start();
			}
		}
	}
	
	
	public void draw(Graphics graphics) {
		for(int i=0; i<activeBalls; i++) {
			balls[i].draw(graphics);
		}
	}
	
}
