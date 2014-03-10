package com.pussycat.minions;

import java.util.ArrayList;

import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

public class GoalieHandler {
	
	/*
	 * As of now, this is more or less only a copy from class BallHandler.
	 */
	
	ArrayList<Goalie> goalies = new ArrayList<Goalie>();
	float goalieDistance;
	int screenWidth;
	int screenHeight;
	float ppi;
	int numberOfGoalies;
	TCPClient tcpClient = null;
	String data;
	
	/**
	 * GoalieHandler constructor
	 *  	
	 * @param	goalieDiameter diameter of a goalie given in centimeters
	 */
	public GoalieHandler(float diameterCentimeters){		
		screenWidth = PussycatMinions.getScreenWidth();
		screenHeight = PussycatMinions.getScreenHeight();
		ppi = PussycatMinions.getPpi();
		
		float goalieDiameter = ppi*(diameterCentimeters*0.3937f);
		goalieDistance = goalieDiameter+10; //so that the goalies have space between them
		
		goalies = new ArrayList<Goalie>();
		
		//Looper.prepare();
		new connectTask().execute("");
		
		Log.d("Debug Pussycat", "ADDING CONNECTION");
		
		goalies.add(new Goalie(screenWidth/2, screenHeight/2, goalieDiameter));
	}
	
	/**
	 * Update function. Gets data from server.
	 */
	public void update(){
		
		if(tcpClient != null){
			tcpClient.getData();
			goalies.clear();
			goalies.add(new Goalie(screenWidth/2, screenHeight/2, 1.5f));
			if(data != "" && data != null) {
				System.out.println(data);
				String[] lines = data.split(" n ");
				for(int i=0; i<lines.length; i++) {
					String[] parts = lines[i].split(" ");
					if(parts.length>1) {
						int id = Integer.parseInt(parts[0]);
						float x = Float.parseFloat(parts[1]);
						float y = Float.parseFloat(parts[2]);
						this.addGoalie(x,y,1.5f);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param goalieDiameterCentimeters
	 */
	public void addGoalie(float x, float y, float goalieDiameterCentimeters){
		float goalieDiameter = ppi*(goalieDiameterCentimeters*0.3937f);
		goalies.add(new Goalie(x, y, goalieDiameter));
	}
	
	/**
	 * 
	 * @param index
	 * @param x
	 * @param y
	 * @param goalieDiameterCentimeters
	 */
	public void addGoalie(int index, float x, float y, float goalieDiameterCentimeters) {
		goalies.add(index,new Goalie(x,y,goalieDiameterCentimeters));
	}
	
	/**
	 * 
	 * @param index
	 * @param x
	 * @param y
	 */
	public void updateGoaliePos(int index, float x, float y) {
		goalies.get(index).setX(x);
		goalies.get(index).setY(y);
	}

	/**
	 * 
	 * @param index
	 */
	public void removeGoalie(int index){
		goalies.remove(index);
		Log.d("Debug Pussycat", "goalie removed");
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	private boolean isGoalieOutside(int index){
		return (	goalies.get(index).getX() - goalies.get(index).getDiameter()/2 > (screenWidth) || 
					goalies.get(index).getX() + goalies.get(index).getDiameter()/2 < 0 ||
					goalies.get(index).getY() - goalies.get(index).getDiameter()/2 > (screenHeight) ||
					goalies.get(index).getY() + goalies.get(index).getDiameter()/2< (0));
	}
	
	/**
	 * 
	 * @param p1
	 * @param r1
	 * @param p2
	 * @param r2
	 * @return
	 */
	private boolean checkCollision(PointF p1, float r1, PointF p2, float r2) {
		final float a = r1 + r2;
	    final float dx = p1.x - p2.x;
	    final float dy = p1.y - p2.y;
	    return a * a > (dx * dx + dy * dy);
	}
	
	public class connectTask extends AsyncTask<String,String,TCPClient> {
		@Override
		protected TCPClient doInBackground(String... message) {
			tcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
				@Override
				public void messageReceived(String message) {
					// TODO Auto-generated method stub
					publishProgress(message);
				}
			});
			tcpClient.run();
			return null;
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			data = values[0];
			//System.out.println("Data is: " + data);
		}
	}
	
	public TCPClient getTCPClient() {
		return tcpClient;
	}
}