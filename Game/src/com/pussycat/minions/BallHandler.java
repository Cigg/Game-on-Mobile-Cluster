package com.pussycat.minions;

import android.util.Log;

public class BallHandler {
	
	Ball[] balls;
	int ballDistance;
	int screenWidth;
	int screenHeight;
	int numberOfBalls;
	
	public BallHandler(int layers, int ballRadius){		
		screenWidth = PussycatMinions.getScreenWidth();
		screenHeight = PussycatMinions.getScreenHeight();
		
		ballDistance = ballRadius+10;
		
		numberOfBalls = calculateNumberOfBalls(layers);
		
		balls = new Ball[numberOfBalls];
		
		Log.d("Debug Pussycat", "NUmberOfBalls: " + numberOfBalls);
		
		int ballCount = 0;
		
		if(numberOfBalls == 1){
			balls[0] = new Ball(screenWidth/2, screenHeight/2);
		}
		else{
			for(int row = 0; row < (1+layers); row++){
				if(row == 0){ //Fšrsta raden
					for(int ballsOnRow = 0; ballsOnRow < (1+2*layers); ballsOnRow++){
						balls[ballsOnRow] = new Ball(ballsOnRow*ballDistance + (screenWidth/2)-(ballDistance*(1+2*layers))/2, (screenHeight/2)-ballDistance/2);
						ballCount++;
						Log.d("Debug Pussycat", "Ballhandler första raden: " + ballCount);
					}
				}
				else{ // Rad ovan och under
					// JŠmn
					if(row%2 == 0){
						for(int ballsOnRow = 0; ballsOnRow < (1+2*layers - row); ballsOnRow++){
							balls[ballCount] = new Ball((int)((ballsOnRow+row*0.5)*ballDistance)+(screenWidth/2)-(ballDistance*(1+2*layers))/2, row*ballDistance+(screenHeight/2)-ballDistance/2);
							ballCount++;
							Log.d("Debug Pussycat", "Ballhandler jämn ovan: " + ballCount);
							
							balls[ballCount] = new Ball((int)((ballsOnRow+row*0.5)*ballDistance)+(screenWidth/2)-(ballDistance*(1+2*layers))/2, -row*ballDistance+(screenHeight/2)-ballDistance/2);
							ballCount++;
							
							Log.d("Debug Pussycat", "Ballhandler jämn under: " + ballCount);
						}
					}
					else // OjŠmn
					{
						for(int ballsOnRow = 0; ballsOnRow < (1 + 2*layers - row); ballsOnRow++){
							balls[ballCount] = new Ball((int)((ballsOnRow+row*0.5)*ballDistance)+(screenWidth/2)-(ballDistance*(1+2*layers))/2, row*ballDistance+(screenHeight/2)-ballDistance/2);
							ballCount++;
							
							Log.d("Debug Pussycat", "Ballhandler ojämn ovan: " + ballCount);
							
							balls[ballCount] = new Ball((int)((ballsOnRow+row*0.5)*ballDistance)+(screenWidth/2)-(ballDistance*(1+2*layers))/2, -row*ballDistance+(screenHeight/2)-ballDistance/2);
							ballCount++;
							Log.d("Debug Pussycat", "Ballhandler ojämn under: " + ballCount);
						}
					}
				}
			}
		}
	}

	private int calculateNumberOfBalls(int layers) {
		int sum = 0;
		
		for(int i = 0; i < (layers+1); i++){
			sum += i;
		}
		
		return (6*sum) + 1;
	}
}