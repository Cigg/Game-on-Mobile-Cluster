package com.pussycat.minions;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import android.graphics.Color;
import android.util.Log;

import com.pussycat.framework.Graphics;
import com.pussycat.minions.Animation.INTERPOLATION;
import com.pussycat.minions.Animation.TYPE;

public class PointsWidget implements Widget {
	
	private final int COLORS[] = {
			Color.rgb(255, 0, 0),
			Color.rgb(0, 255, 0),
			Color.rgb(0, 255, 0),
			Color.rgb((int)(Math.random()*255),(int)(Math.random()*255), (int)(Math.random()*255)),
			Color.rgb((int)(Math.random()*255),(int)(Math.random()*255), (int)(Math.random()*255)),
			Color.rgb((int)(Math.random()*255),(int)(Math.random()*255), (int)(Math.random()*255)),
			Color.rgb((int)(Math.random()*255),(int)(Math.random()*255), (int)(Math.random()*255)),
			Color.rgb((int)(Math.random()*255),(int)(Math.random()*255), (int)(Math.random()*255)),
			Color.rgb((int)(Math.random()*255),(int)(Math.random()*255), (int)(Math.random()*255)),
			Color.rgb((int)(Math.random()*255),(int)(Math.random()*255), (int)(Math.random()*255))
	};
	
	public class Player {
		private final int id;
		private int color;
		private int width;
		
		private AnimatedValue begin;
		private AnimatedValue end;
		
		public Player(final int id, final int begin, final int end, final int color) {
			this.id = id;
			this.begin = new AnimatedValue(begin);
			this.end = new AnimatedValue(end);
			this.color = COLORS[color];
		}
	}
	
	private final int OFFSET_RIGHT; // pixels
	private final int OFFSET_DOWN =  PussycatMinions.meters2Pixels(0.1f / 100.0f); // pixels
	
	private final float SHIFT_TIME = 1.0f; // seconds
	private final Animation.INTERPOLATION INTER = Animation.INTERPOLATION.COSINE;
	private final Animation.TYPE TYPE = Animation.TYPE.POINT_TO_POINT;
	private final float WIDGET_HEIGHT = 1.0f; // cm
	
	private final int nPlayers;
	private Player[] players;

	private int screenWidth = PussycatMinions.getScreenWidth(); // pixels
	private int screenHeight = PussycatMinions.getScreenHeight(); // pixels
	private int widgetHeight = PussycatMinions.meters2Pixels(WIDGET_HEIGHT / 100.0f); // pixels
	private final int dist;	// pixels
	
	private int devicePoints = 0;
	private PointsNotificationWidget pointsNotificationWidget;
	
	public PointsWidget(PointsNotificationWidget pointsNotificationWidget) {
		this.pointsNotificationWidget = pointsNotificationWidget;
		
		nPlayers = SharedVariables.getInstance().getNumberOfPlayers();
		Log.d("SOON", "nPlayers = " + nPlayers);
		players = new Player[nPlayers];
		dist = screenWidth / nPlayers;
		OFFSET_RIGHT = nPlayers;
		
		initializePlayers();
	}
	
	
	public void initializePlayers() {
		
		/*
		for(int i=0; i<nPlayers; i++) {
			players[i] = new Player(i, i*dist, (i+1)*dist, (short)(Math.random()*5));
		}
		players[nPlayers-1].end.setValue(screenWidth + OFFSET_RIGHT);
		
		*/
		ConcurrentHashMap<Integer, Integer> idAndColors = SharedVariables.getInstance().getIdAndColors();
		Log.d("SOON", "idAndColors.size(): " + idAndColors.size());
		Log.d("SOON", "nPlayers: " + nPlayers);
		
		
		Enumeration<Integer> enumKey = idAndColors.keys();
		int i = 0;
		while( enumKey.hasMoreElements() ) {
		    int key = enumKey.nextElement();
		    Integer color = idAndColors.get(key);		 
		    Log.d("SOON", "HAS ELEMENT: " + i + ",  " + color);
		    players[i] = new Player(i, i*dist, (i+1)*dist, color);
		    i++;
		}
		players[i-1].end.setValue(screenWidth + OFFSET_RIGHT);		
	}
	

	public void update() {
		if( SharedVariables.getInstance().pointsIsUpdated() ) {
			ConcurrentHashMap<Integer, Integer> points = SharedVariables.getInstance().getPoints();
			AtomicInteger totalPoints = SharedVariables.getInstance().getTotalPoints();
			
			if(totalPoints.get() == 0) {
				return;
			}
			
			final int temp = SharedVariables.getInstance().getMyPoints();
			final int diff = temp - devicePoints;
			if(diff != 0) {
				pointsNotificationWidget.addNotification(diff*100);
				devicePoints = temp;
			}
			
			Enumeration<Integer> enumKey = points.keys();
			int p = 0;
			while( enumKey.hasMoreElements() ) {
			    int key = enumKey.nextElement();
			    Integer thePoints = points.get(key);		 
			    
			    players[p].width = (int) ((thePoints / (double)totalPoints.get()) * screenWidth);
			    p++;
			}
			
			/*
			for(int i=0; i<points.length; i++) {
				players[i].width = (int) ((points[i].get() / (double)totalPoints.get()) * screenWidth);
				Log.d("POINTS", "points[i].get() = " + points[i].get());
				Log.d("POINTS", "players[i].width = " + players[i].width);
			}
			*/

			Log.d("POINTS", "totalPoints.get() = " + totalPoints.get());
			
			int width = 0;
			AnimationHandler.getInstance().addAnimation(new Animation(players[0].end, players[0].end.getValue(), width + players[0].width, System.nanoTime(), SHIFT_TIME, INTER, TYPE));
			width += players[0].width;
			
			Enumeration<Integer> enumKey2 = points.keys();
			for(int i=1; i<nPlayers-1; i++) {		    
				AnimationHandler.getInstance().addAnimation(new Animation(players[i].begin, players[i].begin.getValue(), width, System.nanoTime(), SHIFT_TIME, INTER, TYPE));
				AnimationHandler.getInstance().addAnimation(new Animation(players[i].end, players[i].end.getValue(), width + players[i].width, System.nanoTime(), SHIFT_TIME, INTER, TYPE));
				width += players[i].width;
			}
			AnimationHandler.getInstance().addAnimation(new Animation(players[points.size()-1].begin, players[nPlayers-1].begin.getValue(), width, System.nanoTime(), SHIFT_TIME, INTER, TYPE));
			
			/*
			for(int i=1; i<points.length-1; i++) {
				AnimationHandler.getInstance().addAnimation(new Animation(players[i].begin, players[i].begin.getValue(), width, System.nanoTime(), SHIFT_TIME, INTER, TYPE));
				AnimationHandler.getInstance().addAnimation(new Animation(players[i].end, players[i].end.getValue(), width + players[i].width, System.nanoTime(), SHIFT_TIME, INTER, TYPE));
				width += players[i].width;
			} */
			//AnimationHandler.getInstance().addAnimation(new Animation(players[points.length-1].begin, players[points.length-1].begin.getValue(), width, System.nanoTime(), SHIFT_TIME, INTER, TYPE));
			
			SharedVariables.getInstance().setPointsIsUpdated(false);
		}
	}
	
	
	public void draw(Graphics graphics) {
		for(int i=0; i<nPlayers; i++) {
			graphics.drawRect((int)players[i].begin.getValue(), OFFSET_DOWN + screenHeight - widgetHeight, (int)(players[i].end.getValue() - players[i].begin.getValue()) + OFFSET_RIGHT, widgetHeight, players[i].color);
		}		
	}
}
