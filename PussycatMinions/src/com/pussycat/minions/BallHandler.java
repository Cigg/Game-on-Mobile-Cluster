package com.pussycat.minions;

import java.util.ArrayList;

import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

public class BallHandler {
	
	ArrayList<Ball> balls = new ArrayList<Ball>();
	float ballDistance;
	int screenWidth;
	int screenHeight;
	float ppi;
	int numberOfBalls;
	TCPClient tcpClient = null;
	String data;
	ArrayList<Integer> idList = new ArrayList<Integer>();
	
	/**
	 * BallHandler constructor
	 *  	
	 * @param	ballDiameter diameter of a ball given in centimeters
	 */
	public BallHandler(float diameterCentimeters){		
		screenWidth = PussycatMinions.getScreenWidth();
		screenHeight = PussycatMinions.getScreenHeight();
		ppi = PussycatMinions.getPpi();
		
		float ballDiameter = ppi*(diameterCentimeters*0.3937f);
		ballDistance = ballDiameter+10; //so that the balls have space between them
		
		balls = new ArrayList<Ball>();
		
		//Looper.prepare();

		//new connectTask().execute("");
		
		Log.d("Debug Pussycat", "ADDING CONNECTION");
		
		Log.d("Debug Pussycat", "NUmberOfBalls: " + numberOfBalls);
		
		//balls.add(new Ball(screenWidth/2, screenHeight/2, ballDiameter));
	}
	
	/**
	 * Update function. Gets data from server.
	 */
	public void update(){
		
		// Collision detection
		/*for(int i=0; i<balls.size(); i++) {
			for (int j=i+1; j<balls.size(); j++) {
				if (checkCollision(balls.get(i).getPoint(), balls.get(i).getRadius(), balls.get(j).getPoint(), balls.get(j).getRadius())) {
					// your code here
					balls.get(i).setLocked(true);
					balls.get(j).setLocked(true);
				}
			}
		}
		
		for(int i = 0; i < balls.size(); i++){
			if (isBallOutside(i)){
				removeBall(i);
			}
			else
				balls.get(i).update();
		}*/
		
		/*if(tcpClient != null){
			tcpClient.getData();
			balls.clear();
			balls.add(new Ball(screenWidth/2, screenHeight/2, 1.5f));
			if(data != "" && data != null) {
				System.out.println(data);
				String[] lines = data.split(" n ");
				for(int i=0; i<lines.length; i++) {
					String[] parts = lines[i].split(" ");
					if(parts.length>1) {
						int id = Integer.parseInt(parts[0]);
						float x = Float.parseFloat(parts[1]);
						float y = Float.parseFloat(parts[2]);
						this.addBall(x,y,1.5f);
					}
				}
			}
		}*/
		
		//Log.d("UPDATE", balls.size()+"");
		for(int i=0; i<balls.size(); i++){
			balls.get(i).update();
		}
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param ballDiameterCentimeters
	 */
	public void addBall(float x, float y, float ballDiameterCentimeters){
		float ballDiameter = ppi*(ballDiameterCentimeters*0.3937f);
		balls.add(new Ball(x, y, ballDiameter));
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param ballDiameterCentimeters
	 * @param speedX
	 * @param speedY
	 */
	public void addBall(float x, float y, float ballDiameterCentimeters, float speedX, float speedY){
		float ballDiameter = ppi*(ballDiameterCentimeters*0.3937f);
		balls.add(new Ball(x, y, ballDiameter, speedX, speedY));
	}
	
	
	public void addBall(int id, float x, float y, float ballDiameterCentimeters, float speedX, float speedY){
		boolean found = false;
		for(int i = 0; i < idList.size(); i++){
			if(id == idList.get(i)){
				found = true;
			}
		}
		
		if(found){
			for(int i=0; i < balls.size(); i++){
				if(balls.get(i).getId() == id){
					balls.get(i).updateBall((int)x,(int)y, speedX, speedY);
				}
			}
		}else {
			float ballDiameter = ppi*(ballDiameterCentimeters*0.3937f);
			Log.d("UPDATE", speedX +" "+ speedY);
			balls.add(new Ball(id, x, y, ballDiameter, speedX, speedY));
			idList.add(id);
		}
	}
	/**
	 * 
	 * @param index
	 * @param x
	 * @param y
	 * @param ballDiameterCentimeters
	 */
	public void addBall(int index, float x, float y, float ballDiameterCentimeters) {
		balls.add(index,new Ball(x,y,ballDiameterCentimeters));
	}
	
	/**
	 * 
	 * @param index
	 * @param x
	 * @param y
	 */
	public void updateBallPos(int index, float x, float y) {
		balls.get(index).setX(x);
		balls.get(index).setY(y);
	}

	/**
	 * 
	 * @param index
	 */
	public void removeBall(int index){
		balls.remove(index);
		Log.d("Debug Pussycat", "ball removed");
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	private boolean isBallOutside(int index){
		return (	balls.get(index).getX() - balls.get(index).getDiameter()/2 > (screenWidth) || 
					balls.get(index).getX() + balls.get(index).getDiameter()/2 < 0 ||
					balls.get(index).getY() - balls.get(index).getDiameter()/2 > (screenHeight) ||
					balls.get(index).getY() + balls.get(index).getDiameter()/2< (0));
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

	public void clearBalls() {
		balls.clear();
	}
	
	public String getData() {
		return data;
	}
}
