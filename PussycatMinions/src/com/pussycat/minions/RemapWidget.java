package com.pussycat.minions;

import java.io.File;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import com.pussycat.framework.Graphics;
import com.pussycat.framework.implementation.AndroidGraphics;

public class RemapWidget implements Widget {
	
	private final String FONT = "DS-DIGIB.TTF";
	private final int SIZE = PussycatMinions.meters2Pixels( 1.0f / 100.0f);
	
	private final int X_MARGIN = PussycatMinions.getScreenWidth() / 2;
	private final int Y_MARGIN = PussycatMinions.getScreenHeight() / 2;

	
	private Paint paint;
	private String text = "Remap";
	private Rect bounds;
	
	private AnimatedValue alpha;
	private Animation animation;
	
	public RemapWidget() {
		Typeface typeface = Typeface.create("Helvetica", Typeface.BOLD);
		//AssetManager assets = AndroidGraphics.getAssets();
		//Typeface typeface = Typeface.createFromAsset(assets, "fonts" + File.separator + FONT);
		
		paint = new Paint();
		paint.setTypeface(typeface);
		paint.setTextSize(SIZE);
		bounds = new Rect();
		paint.setColor(Color.RED);
		paint.getTextBounds(text, 0, text.length(), bounds);
		
		alpha = new AnimatedValue(0);
		start();
	}
	
	public void stop() {
		animation.setIsFinished(true);
	}
	
	public void start() {
		animation = new Animation(alpha, 0, 255, System.nanoTime(), 0.4f, Animation.INTERPOLATION.FLIP, Animation.TYPE.PING_PONG);
		AnimationHandler.getInstance().addAnimation(animation);
	}
	
	public void update() {

	}
	
	public void draw(Graphics graphics) {

		if(SharedVariables.getInstance().isRemapping()) {
			Log.d("REM", "REMDRAW");
			paint.setAlpha((int)alpha.getValue());
			graphics.getCanvas().drawText(text, X_MARGIN - bounds.width()/2, Y_MARGIN - bounds.height()/2, paint);
		}
	}

}
