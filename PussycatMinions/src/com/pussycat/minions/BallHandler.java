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
		
		
	}
	
	public void update(final float timeDelta){
		
		Log.d("NBALLS", "balls.size() = " + balls.size());
		for(int i=0; i<balls.size(); i++){
			balls.get(i).update(timeDelta);
		}
		
	}

	
	public void addBall(float x, float y, float ballDiameterCentimeters){
		float ballDiameter = ppi*(ballDiameterCentimeters*0.3937f);
		balls.add(new Ball(x, y, ballDiameter));
	}
	
	
	public void addBall(float x, float y, float ballDiameterCentimeters, float speedX, float speedY, boolean type){
		float ballDiameter = ppi*(ballDiameterCentimeters*0.3937f);
		balls.add(new Ball(x, y, ballDiameter, speedX, speedY, type));
	}
	
	
	public void addBall(int id, float x, float y, float ballDiameterCentimeters, float speedX, float speedY, boolean type, float reciveDelay, float time){
		boolean found = false;
		for(int i = 0; i < idList.size(); i++){
			if(id == idList.get(i)){
				found = true;
				break;
			}
		}
		
		if(found){
			for(int i=0; i < balls.size(); i++){
				if(balls.get(i).getId() == id){
					balls.get(i).updatePos(x, y, speedX, speedY, time);
					
					//balls.get(i).updateBall(x + speedX * reciveDelay, y + speedY * reciveDelay, speedX, speedY);					
				
				}
			}
		}else {
			float ballDiameter = ppi*(ballDiameterCentimeters*0.3937f);
			Log.d("UPDATE", speedX +" "+ speedY);
			balls.add(new Ball(id, x, y, ballDiameter, speedX, speedY, type, time));
			idList.add(id);
		}

	}

	public TCPClient getTCPClient() {
		return tcpClient;
	}

	public void clearBalls() {
		balls.clear();
	}
	

}
