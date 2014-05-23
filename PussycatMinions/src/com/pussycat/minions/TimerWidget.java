package com.pussycat.minions;

import java.io.File;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.pussycat.framework.Graphics;
import com.pussycat.framework.implementation.AndroidGraphics;

public class TimerWidget implements Widget {
	
	private final String FONT = "DS-DIGIB.TTF";
	private final int DECIMALS = 2;
	private final String SEPERATOR = ":";
	private final int SIZE = PussycatMinions.meters2Pixels( 1.0f / 100.0f);
	private final int Y_MARGIN = PussycatMinions.meters2Pixels( 0.2f / 100.0f);
	private int X_MARGIN = - PussycatMinions.meters2Pixels( 0.2f / 100.0f);
	
	private int seconds = 60; 
	private long startTime ;
	private long totalTime = seconds * 1000;
	
	private Paint paint;
	private String text = "60:00";
	Rect bounds;
	
	public TimerWidget() {
		//Typeface typeface = Typeface.create("Helvetica", Typeface.BOLD);
		AssetManager assets = AndroidGraphics.getAssets();
		Typeface typeface = Typeface.createFromAsset(assets, "fonts" + File.separator + FONT);

		paint = new Paint();
		paint.setTypeface(typeface);
		paint.setTextSize(SIZE);
		bounds = new Rect();
		String abc = "02:20:000";
		paint.setColor(Color.BLUE);
		paint.getTextBounds(abc, 0, 6 + DECIMALS, bounds);
		
		seconds = SharedVariables.getInstance().getGameTimeInSeconds(); 
		totalTime = seconds * 1000;
		setTime(totalTime);
	}
	
	
	public void start() {
		startTime = System.currentTimeMillis();
		update();
	}
	
	
	public void update() {
		if(SharedVariables.getInstance().isRunning()) {
			long elapsedTime = System.currentTimeMillis() - startTime;
			long timeLeft = totalTime - elapsedTime;
			if(timeLeft > 0) {
				setTime(timeLeft);
			} else {
				text = "00" + SEPERATOR + "00" + SEPERATOR + "00";
			}
		}
	}

	
	public void setTime(float timeLeft) {
		text = "";
		
		int min = (int) Math.floor(timeLeft / (60*1000.0f));
		timeLeft -= min * 60*1000;	
		
		if(min > 0) {
			if(min < 10) {
				text = text.concat("0");
			}
			text = text.concat(String.valueOf(min));
			text = text.concat(SEPERATOR);
		} else {
			text = text.concat("00");
			text = text.concat(SEPERATOR);
		}
		
		int sec = (int) (timeLeft / 1000);
		timeLeft -= sec * 1000;
		
		if(sec > 0) {
			if(sec < 10) {
				text = text.concat("0");
			}
			text = text.concat(String.valueOf(sec));
			text = text.concat(SEPERATOR);
		} else {
			text = text.concat("00");
			text = text.concat(SEPERATOR);
		}
		
		int mil = (int) Math.floor(timeLeft / 10.0f);
		
		if(mil > 0) {
			if(mil < 10) {
				text = text.concat("0");
			}
			text = text.concat(String.valueOf(mil));
		} else {
			text = text.concat("00");
		}
	}

	
	
	public void draw(Graphics graphics) {
		graphics.getCanvas().drawText(text.substring(0, Math.min(text.length(), 6 + DECIMALS)), PussycatMinions.getScreenWidth() + X_MARGIN - bounds.width(), Y_MARGIN + bounds.height(), paint);
	}

}
