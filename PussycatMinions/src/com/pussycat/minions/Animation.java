package com.pussycat.minions;

import android.util.Log;


public class Animation {
	   
	public static enum INTERPOLATION {
		LINEAR,
		COSINE,
		FLIP
	}
	
	public static enum TYPE {
		ENDLESS,
		POINT_TO_POINT,
		PING_PONG
	}
	
	private static final float EPSILON = 0.1f;
	private boolean isAnimating;
    private boolean isFinished;
	private AnimatedValue animatedValue;
	private double from;
	private double to;
	private float startTime;
	private float durationTime;
	private INTERPOLATION interpolation;
	private TYPE type;
	
	
	public Animation(AnimatedValue animatedValue, final double from, final double to, final float startTime, final float durationTime, final INTERPOLATION interpolation, final TYPE type) {
		isAnimating = true;
		isFinished = false;
		this.animatedValue = animatedValue;
		this.from = from;
		this.to = to;
		this.startTime = startTime;
		this.durationTime = durationTime * (float)Math.pow(10, 9);
		this.interpolation = interpolation;
		this.type = type;
	}
		
	
	public void setIsAnimating(final boolean isAnimating) {
		this.isAnimating = isAnimating;
	}
	
	
	public void start() {
		if( startTime + durationTime - System.nanoTime() <= EPSILON ) {
			animatedValue.setValue(from);
			startTime = System.nanoTime();
			isAnimating = true;
		}
	}
	
	
	public boolean isFinished() {
		return isFinished;
	}
	
	
	public void setIsFinished(final boolean isFinished) {
		this.isFinished = isFinished;
	}
	
	
	public AnimatedValue getAnimatedValue() {
		return animatedValue;
	}
	
	
	public void update(final float time) {
		if(isAnimating) {
			animate(time);
		}
	}
	
	
	public void animate(final float time) {
		final double scale = (double) (time - startTime) / (double)durationTime;
		switch(interpolation) {
			case LINEAR:
			{
				animatedValue.setValue(linearInterpolation(from, to, scale));
				if( startTime + durationTime - time <= EPSILON ) {
					if( type == TYPE.ENDLESS ) {
							startTime = time;
							animatedValue.setValue(from);
				    } else if( type == TYPE.POINT_TO_POINT ) {
				    	isFinished = true;
				    }	else if( type == TYPE.PING_PONG ) {
						startTime = time;
						double temp = from;
						from = to;
						to = temp;
						animatedValue.setValue(from);
				    }
				}
			} break;
			
			case COSINE:
			{
				animatedValue.setValue(cosineInterpolation(from, to, scale));
				if( startTime + durationTime - time <= EPSILON ) {
					if( type == TYPE.POINT_TO_POINT ) {
						animatedValue.setValue(to);
				    	isAnimating = false;
				    	isFinished = true;
				    }	
				}
			} break;
			
			case FLIP:
			{
				if( startTime + durationTime - time <= EPSILON ) {
					if( type == TYPE.ENDLESS ) {
						animatedValue.setValue(to);
						isAnimating = false;
						//Log.d("WID", "TYPE_ENDLESS - set value to: " + to);
					} else if( type == TYPE.POINT_TO_POINT ) {
						animatedValue.setValue(to);
						isFinished = true;
					}else if( type == TYPE.PING_PONG ) {
						animatedValue.setValue(to);
						startTime = time;
						double temp = from;
						from = to;
						to = temp;
					}
				}
			} break;
		}
	}
	
	
    private double linearInterpolation(double x1, double x2, double scale) {
		return x1 * (1 - scale) + x2 * scale;
	}
	
    
    private double cosineInterpolation(double x1, double x2, double scale) {
		float scaleModified = (float) ((1 - Math.cos(scale * Math.PI)) / 2);
		return x1 * (1 - scaleModified) + x2 * scaleModified;
	}
	
}