package com.pussycat.minions;

import android.util.Log;

import com.pussycat.framework.Graphics;
import com.pussycat.framework.Image;

public class CountDownWidget implements Widget {
	
	private float startTime = 0;
	private boolean isFinished = true;
	private boolean isRunning = false;
	private Image image = Assets.countDownImage_3;
	
	private int screenWidth = PussycatMinions.getScreenWidth();
	private int screenHeight = PussycatMinions.getScreenHeight();
	
	private TimerWidget timerWidget = null;
	private MusicWidget musicWidget = null;
	
	public CountDownWidget(TimerWidget timerWidget) {
		this.timerWidget = timerWidget;
		start();
	}
	
	public CountDownWidget(MusicWidget musicWidget) {
		this.musicWidget = musicWidget;
		start();
	}
	
	public void start() {
		startTime = System.nanoTime();	
		isFinished = false;
		isRunning = true;
	}
	
	public void update() {
		if(isRunning) {
			final float durationTime = System.nanoTime() - startTime;
			if(durationTime < 1 * Math.pow(10, 9)) {
				image = Assets.countDownImage_3;
			} else if(durationTime < 2 * Math.pow(10, 9)) {
				image = Assets.countDownImage_2;
			} else if(durationTime < 3 * Math.pow(10, 9)) {
				image = Assets.countDownImage_1;
			} else if(durationTime < 3.5f * Math.pow(10, 9)) {
				image = Assets.countDownImage_GO;
			} else  {
				isRunning = false;
				SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.RUN_DEVICE);
	    		SharedVariables.getInstance().setIsRunning(true);
	    		
	    		if(timerWidget != null) {
	    			timerWidget.start();
	    		} 
	    		
	    		if(musicWidget != null) {
	    			musicWidget.play();
	    		}
			}
		}
	}
	
	public void draw(Graphics graphics) {
		if(isRunning) {
			int targetWidth = (int)((float)screenWidth*0.5f);
			int targetHeight = (int)(((float)targetWidth/(float)image.getWidth())*(float)image.getHeight());
			
			graphics.drawScaledImage(image, screenWidth/2 - targetWidth/2, screenHeight/2 - targetHeight/2, targetWidth, targetHeight, 0, 0, image.getWidth(), image.getHeight(), 0);
		}
	}
	
	public boolean isFinished() {
		return isFinished;
	}

}
