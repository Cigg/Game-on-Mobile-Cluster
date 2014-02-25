package com.pussycat.minions;

import java.util.ArrayList;

import android.graphics.PointF;
import android.util.Log;

public class BallHandler {
	
	ArrayList<Ball> balls = new ArrayList<Ball>();
	float ballDistance;
	int screenWidth;
	int screenHeight;
	float ppi;
	int numberOfBalls;
	
	/**
	 * BallHandler constructor
	 * Creates balls and adds to the array balls
	 * The balls are positioned in a "circle" formation
	 * Example:
	 * 
	 * 	 	  o o o			
	 *		 o o o o		
	 *		o o o o o		
	 *		 o o o o		
	 *		  o o o	
	 *  
	 * @param 	layers		number of layers (like an onion). Zero layers will generate one ball		
	 * @param	ballRadius
	 */
	public BallHandler(int layers, float diameterCentimeters){		
		screenWidth = PussycatMinions.getScreenWidth();
		screenHeight = PussycatMinions.getScreenHeight();
		ppi = PussycatMinions.getPpi();
		
		// Distance between balls = ballRadius + padding
		float ballDiameter = ppi*(diameterCentimeters*0.3937f);
		ballDistance = ballDiameter+10;
		
		numberOfBalls = calculateNumberOfBalls(layers);
		
		balls = new ArrayList<Ball>();
		
		Log.d("Debug Pussycat", "NUmberOfBalls: " + numberOfBalls);
		
		int ballCount = 0;
		
		if(numberOfBalls == 1){
			// Create and position one ball in the middle of the screen
			balls.add(new Ball(screenWidth/2, screenHeight/2, ballDiameter));
		}
		else{
			
			// Iterates through each row
			// Example 2 layers:
			//
			//	  * * *			Row 2 (even)
			//   * * * *		Row 1 (odd)
			//	* * * * *		Row 0 (even)
			//	 * * * *		Row 1 (odd)
			//    * * *			Row 2 (even)
			//
			//
			for(int row = 0; row < (1+layers); row++){
				// First row
				if(row == 0){
					for(int ballsOnRow = 0; ballsOnRow < (1+2*layers); ballsOnRow++){
						balls.add(new Ball(ballsOnRow*ballDistance + (screenWidth/2)-(ballDistance*(2*layers))/2, (screenHeight/2), ballDiameter));
					}
				}
				// Row above and underneath the middle
				else{
					// Even
					if(row%2 == 0){
						for(int ballsOnRow = 0; ballsOnRow < (1+2*layers - row); ballsOnRow++){
							// Create ball above the middle
							balls.add(new Ball((int)((ballsOnRow+row*0.5)*ballDistance)+(screenWidth/2)-(ballDistance*(2*layers))/2, row*ballDistance+(screenHeight/2), ballDiameter));

							// Create ball below the middle
							balls.add(new Ball((int)((ballsOnRow+row*0.5)*ballDistance)+(screenWidth/2)-(ballDistance*(2*layers))/2, -row*ballDistance+(screenHeight/2), ballDiameter));
						}
					}
					// Odd (offset 0.5*ballDistance in the x direction)
					else 
					{
						for(int ballsOnRow = 0; ballsOnRow < (1 + 2*layers - row); ballsOnRow++){
							// Create ball above the middle
							balls.add(new Ball((int)((ballsOnRow+row*0.5)*ballDistance)+(screenWidth/2)-(ballDistance*(2*layers))/2, row*ballDistance+(screenHeight/2), ballDiameter));
							
							// Create ball below the middle
							balls.add(new Ball((int)((ballsOnRow+row*0.5)*ballDistance)+(screenWidth/2)-(ballDistance*(2*layers))/2, -row*ballDistance+(screenHeight/2), ballDiameter));
						}
					}
				}
			}
		}
	}
	
	public void update(){
		
		// Collision detection
		for(int i=0; i<balls.size(); i++) {
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
		}
	}

	public void addBall(float x, float y, float ballDiameterCentimeters){
		float ballDiameter = ppi*(ballDiameterCentimeters*0.3937f);
		balls.add(new Ball(x, y, ballDiameter));
		Log.d("Debug Pussycat", "ball added");
	}
	
	public void addBall(float x, float y, float ballDiameterCentimeters, float speedX, float speedY){
		float ballDiameter = ppi*(ballDiameterCentimeters*0.3937f);
		balls.add(new Ball(x, y, ballDiameter, speedX, speedY));
		Log.d("Debug Pussycat", "ball added with speedX: " + speedX + "speedY: " + speedY);
	}
	
	public void removeBall(int index){
		balls.remove(index);
		Log.d("Debug Pussycat", "ball removed");
	}
	
	private boolean isBallOutside(int index){
		return (	balls.get(index).getX() - balls.get(index).getDiameter()/2 > (screenWidth) || 
					balls.get(index).getX() + balls.get(index).getDiameter()/2 < 0 ||
					balls.get(index).getY() - balls.get(index).getDiameter()/2 > (screenHeight) ||
					balls.get(index).getY() + balls.get(index).getDiameter()/2< (0));
	}
	
	private boolean checkCollision(PointF p1, float r1, PointF p2, float r2) {
		// Code example from http://stackoverflow.com/questions/697188/fast-circle-collision-detection
		final float a = r1 + r2;
	    final float dx = p1.x - p2.x;
	    final float dy = p1.y - p2.y;
	    return a * a > (dx * dx + dy * dy);
	}
	
	/**
	 * calculates the number of balls that will be created using the specified number of layers
	 * mathematically: 6*(0 + 1 + 2 + .. + n), n = number of layers
	 * 
	 * @param 	layers	the number of layers
	 * @return 			the number of balls that will be created
	 * 					
	 */
	private int calculateNumberOfBalls(int layers) {
		int sum = 0;
		
		for(int i = 0; i < (layers+1); i++){
			sum += i;
		}
		
		return (6*sum) + 1;
	}
}