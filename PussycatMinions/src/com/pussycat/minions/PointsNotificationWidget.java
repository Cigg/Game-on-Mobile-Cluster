package com.pussycat.minions;

import java.io.File;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.pussycat.framework.Audio;
import com.pussycat.framework.Graphics;
import com.pussycat.framework.Music;
import com.pussycat.framework.Sound;
import com.pussycat.framework.implementation.AndroidAudio;
import com.pussycat.framework.implementation.AndroidGame;
import com.pussycat.framework.implementation.AndroidGraphics;
import com.pussycat.framework.implementation.AndroidMusic;

public class PointsNotificationWidget {
	
	private final int X_MARGIN = PussycatMinions.meters2Pixels(0.8f / 100.0f);
	private final int Y_MARGIN = PussycatMinions.meters2Pixels(1.0f / 100.0f);
	
	private final int Y_DIST = PussycatMinions.meters2Pixels(0.8f / 100.0f);
	private final int X_DIFF = PussycatMinions.meters2Pixels(0.5f / 100.0f);
	
	private final int screenWidth = PussycatMinions.getScreenWidth();
	
	private class Notifcation {
		
		final int points;
		AnimatedValue alpha;
		AnimatedValue x;
		AnimatedValue y;
		Animation xAnimation;
		
		private Notifcation(final int points) {
			this.points = points;
			alpha = new AnimatedValue(255);
			x = new AnimatedValue(screenWidth - X_MARGIN);
			y = new AnimatedValue(Y_MARGIN);
			AnimationHandler.getInstance().addAnimation(new Animation(alpha, 255, 0, System.nanoTime(), 1, Animation.INTERPOLATION.COSINE, Animation.TYPE.POINT_TO_POINT));
			xAnimation = new Animation(x, x.getValue(), x.getValue() + Math.random() * X_DIFF, System.nanoTime(), 0.25f, Animation.INTERPOLATION.COSINE, Animation.TYPE.ENDLESS);
			AnimationHandler.getInstance().addAnimation(xAnimation);
			AnimationHandler.getInstance().addAnimation(new Animation(y, y.getValue(), y.getValue() + Y_DIST, System.nanoTime(), 1.0f, Animation.INTERPOLATION.COSINE, Animation.TYPE.POINT_TO_POINT));
		}
	}
	
	ConcurrentHashMap<Integer, Notifcation> notifications;
	private final String FONT = "Death in the Shadow.ttf";
	private final int SIZE = PussycatMinions.meters2Pixels( 1.0f / 100.0f);
	private Paint paint;
	Rect bounds;
	private Sound sound;
	
	public PointsNotificationWidget(Audio audio) {
		notifications = new ConcurrentHashMap<Integer, Notifcation>();
			
		AssetManager assets = AndroidGraphics.getAssets();
		Typeface typeface = Typeface.createFromAsset(assets, "fonts" + File.separator + FONT);

		paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setTypeface(typeface);
		paint.setTextSize(SIZE);
		
		bounds = new Rect();
		String abc = "1356";
		paint.getTextBounds(abc, 0, abc.length(), bounds);

		sound = audio.createSound("sounds" + File.separator + "pling.wav");
	}
	
	
	public void addNotification(final int points) {
		Notifcation notification = new Notifcation(points);
		notifications.put(notification.hashCode(), notification);
		sound.play(1.0f);
	}
	
	
	public void update() {
		Enumeration<Integer> enumKey = notifications.keys();
		while( enumKey.hasMoreElements() ) {
		    int key = enumKey.nextElement();
		    Notifcation notification = notifications.get(key);		 
	
			if(notification.alpha.getValue() == 0) {
				notifications.remove(notification.hashCode());
				notification.xAnimation.setIsFinished(true);
			}
		}
	}
	
	
	public void draw(Graphics graphics) {
		Enumeration<Integer> enumKey = notifications.keys();
		while( enumKey.hasMoreElements() ) {
		    int key = enumKey.nextElement();
		    Notifcation notification = notifications.get(key);	
		    paint.setAlpha((int) notification.alpha.getValue());
			graphics.getCanvas().drawText("+ " + String.valueOf(notification.points) , (float)(notification.x.getValue() - bounds.width()), (float)(notification.y.getValue() + bounds.height()), paint);
		}
	}
	
	
}
