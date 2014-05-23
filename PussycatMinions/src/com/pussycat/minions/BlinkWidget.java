package com.pussycat.minions;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.pussycat.framework.Graphics;

public class BlinkWidget implements Widget {
	
	private Paint paint;
	
	private ConcurrentHashMap<Integer, Integer> lastPoints;
	private AnimatedValue alpha = new AnimatedValue(100);
	
	public Bitmap bitmap = null;
	public Canvas bitmapCanvas = null;	 
	
	public BlinkWidget() {
		
		Log.d("BLINK", "new BlinkWidget");
		
		paint = new Paint();
		paint.setAlpha(100);
		
		lastPoints = SharedVariables.getInstance().getPoints();
	
		 bitmap = Bitmap.createBitmap(PussycatMinions.getScreenWidth(), PussycatMinions.getScreenHeight(), Bitmap.Config.ARGB_8888);
		 bitmapCanvas = new Canvas(bitmap);
		    
		initializeLastPoints();
	}

	
	public void initializeLastPoints() {
		lastPoints = new ConcurrentHashMap<Integer, Integer>(SharedVariables.getInstance().getPoints());
	}
	
	
	public void update() {
		Log.d("BLINK", "update");
		if( SharedVariables.getInstance().pointsIsUpdated() ) {
			Log.d("BLINK", "pointsIsUpdated");
			ConcurrentHashMap<Integer, Integer> points = SharedVariables.getInstance().getPoints();
			
			Enumeration<Integer> enumKey = points.keys();
			while( enumKey.hasMoreElements() ) {
				Log.d("BLINK", "loop");
			    int key = enumKey.nextElement();
			    Integer pointsNow = points.get(key);		
			    Integer pointsLast = lastPoints.get(key);		 
			    
			    if(pointsNow != pointsLast) {
			    	AnimationHandler.getInstance().addAnimation(new Animation(alpha, 100, 0, System.nanoTime(), 0.5f, Animation.INTERPOLATION.COSINE, Animation.TYPE.POINT_TO_POINT));
			    	paint.setColor(SharedVariables.getInstance().getColor(key));
			    	Log.d("BLINK", "BLINKA");
			    	break;
			    }
			}
			
			
			lastPoints = new ConcurrentHashMap<Integer, Integer>(points);
			SharedVariables.getInstance().setPointsIsUpdated(false);
		}
		
	}

	public void draw(Graphics graphics) {
		if(alpha.getValue() != 0) {
			paint.setAlpha((int) alpha.getValue());
			bitmap = Bitmap.createBitmap(PussycatMinions.getScreenWidth(), PussycatMinions.getScreenHeight(), Bitmap.Config.ARGB_8888);
			graphics.getCanvas().drawBitmap(bitmap, 0, 0, paint);
		}
	}
	

}
