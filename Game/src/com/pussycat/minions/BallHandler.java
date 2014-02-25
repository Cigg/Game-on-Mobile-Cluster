package com.pussycat.minions;

import android.util.Log;

public class BallHandler {
	
	Ball[] balls;
	int ballDistance;
	int screenWidth;
	int screenHeight;
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
	public BallHandler(int layers, int ballRadius){		
		screenWidth = PussycatMinions.getScreenWidth();
		screenHeight = PussycatMinions.getScreenHeight();
		
		// Distance between balls = ballRadius + padding
		ballDistance = ballRadius+10;
		
		numberOfBalls = calculateNumberOfBalls(layers);
		
		balls = new Ball[numberOfBalls];
		
		Log.d("Debug Pussycat", "NUmberOfBalls: " + numberOfBalls);
		
		int ballCount = 0;
		
		if(numberOfBalls == 1){
			// Create and position one ball in the middle of the screen
			balls[0] = new Ball(screenWidth/2, screenHeight/2);
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
						balls[ballsOnRow] = new Ball(ballsOnRow*ballDistance + (screenWidth/2)-(ballDistance*(1+2*layers))/2, (screenHeight/2)-ballDistance/2);
						ballCount++;
					}
				}
				// Row above and underneath the middle
				else{
					// Even
					if(row%2 == 0){
						for(int ballsOnRow = 0; ballsOnRow < (1+2*layers - row); ballsOnRow++){
							// Create ball above the middle
							balls[ballCount] = new Ball((int)((ballsOnRow+row*0.5)*ballDistance)+(screenWidth/2)-(ballDistance*(1+2*layers))/2, row*ballDistance+(screenHeight/2)-ballDistance/2);
							ballCount++;
							
							// Create ball below the middle
							balls[ballCount] = new Ball((int)((ballsOnRow+row*0.5)*ballDistance)+(screenWidth/2)-(ballDistance*(1+2*layers))/2, -row*ballDistance+(screenHeight/2)-ballDistance/2);
							ballCount++;
						}
					}
					// Odd (offset 0.5*ballDistance in the x direction)
					else 
					{
						for(int ballsOnRow = 0; ballsOnRow < (1 + 2*layers - row); ballsOnRow++){
							// Create ball above the middle
							balls[ballCount] = new Ball((int)((ballsOnRow+row*0.5)*ballDistance)+(screenWidth/2)-(ballDistance*(1+2*layers))/2, row*ballDistance+(screenHeight/2)-ballDistance/2);
							ballCount++;
							
							// Create ball below the middle
							balls[ballCount] = new Ball((int)((ballsOnRow+row*0.5)*ballDistance)+(screenWidth/2)-(ballDistance*(1+2*layers))/2, -row*ballDistance+(screenHeight/2)-ballDistance/2);
							ballCount++;
						}
					}
				}
			}
		}
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