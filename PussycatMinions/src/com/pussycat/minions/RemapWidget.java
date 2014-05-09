package com.pussycat.minions;

import java.io.File;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.pussycat.framework.Graphics;
import com.pussycat.framework.implementation.AndroidGraphics;

public class RemapWidget implements Widget {
	
	private final String FONT = "DS-DIGIB.TTF";
	private final int SIZE = PussycatMinions.meters2Pixels( 1.0f / 100.0f);
	
	private final int Y_MARGIN = PussycatMinions.getScreenWidth() / 2;
	private int X_MARGIN = - PussycatMinions.getScreenHeight() / 2;
	
	private Paint paint;
	private String text = "REMAPPING";
	private Rect bounds;
	
	private AnimatedValue alpha;
	private Animation animation;
	
	public RemapWidget() {
		
		AssetManager assets = AndroidGraphics.getAssets();
		Typeface typeface = Typeface.createFromAsset(assets, "fonts" + File.separator + FONT);
		
		paint = new Paint();
		paint.setTypeface(typeface);
		paint.setTextSize(SIZE);
		bounds = new Rect();
		paint.setColor(Color.RED);
		paint.getTextBounds(text, 0, 9, bounds);
		
		start();
	}
	
	public void stop() {
		animation.setIsFinished(true);
	}
	
	public void start() {
		animation = new Animation(alpha, 0, 255, System.nanoTime(), 1, Animation.INTERPOLATION.FLIP, Animation.TYPE.PING_PONG);
	}
	
	public void update() {

	}
	
	public void draw(Graphics graphics) {
		if(SharedVariables.getInstance().isRemapping()) {
			graphics.getCanvas().drawText(text, X_MARGIN - bounds.width() /2, Y_MARGIN - bounds.height()/2, paint);
		}
	}

}
