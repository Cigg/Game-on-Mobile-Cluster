package com.pussycat.minions;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import android.util.Log;

public class AnimationHandler {

	private ConcurrentHashMap<AnimatedValue, Animation> animations = new ConcurrentHashMap<AnimatedValue, Animation>();
	
	private static AnimationHandler theOnlyInstance;
	private static Object theOnlyInstanceMutex = new Object();
	
	
	public static AnimationHandler getInstance() {
		if( theOnlyInstance == null ) {
			synchronized( theOnlyInstanceMutex ) {
				if( theOnlyInstance == null ) {
					theOnlyInstance = new AnimationHandler();
				}
			}
		}
		return theOnlyInstance;
	}
	
	
	private AnimationHandler() {
		
	}
	
	
	public void addAnimation(final Animation animation) {
		animations.put(animation.getAnimatedValue(), animation);
	}
	
	
	public void removeAnimation(AnimatedValue animatedValue) {
		animations.remove(animatedValue);
	}
	
	
	public void updateAnimations(final float time) {
		Enumeration<AnimatedValue> enumKey = animations.keys();
		while( enumKey.hasMoreElements() ) {
			Object key = enumKey.nextElement();
		    Animation animation = animations.get(key);		

		    animation.update(time);
		    
		    if(animation.isFinished()) {
		    	animations.remove(animation.getAnimatedValue());
		    }
		    
		}	
	}
	
	
}
