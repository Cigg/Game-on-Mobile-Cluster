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
		
		int sum = 0;
		
		for(int i = 0; i < (layers+1); i++){
			sum += i;
			Log.d("Debug Pussycat", "Sum: " + sum);
		}
		
		numberOfBalls = (6*sum) + 1;
		
		balls = new Ball[numberOfBalls];
		
		Log.d("Debug Pussycat", "NUmberOfBalls: " + numberOfBalls);
		
		int ballCount = 0;
		
		if(numberOfBalls == 1){
			balls[0] = new Ball(screenWidth/2, screenHeight/2);
		}
		else{
			for(int row = 0; row < (1+layers); row++){
				if(row == 0){ //Första raden
					for(int j = 0; j < (1+2*layers); j++){
						balls[j] = new Ball(j*ballDistance + (screenWidth/2)-(ballDistance*(1+2*layers))/2, (screenHeight/2)-ballDistance/2);
						ballCount++;
						Log.d("Debug Pussycat", "Ballhandler första raden: " + ballCount);
					}
				}
				else{ // Rad ovan och under
					// Jämn
					if(row%2 == 0){
						for(int j = 0; j < (1+2*layers - row); j++){
							balls[ballCount] = new Ball((int)((j+row*0.5)*ballDistance)+(screenWidth/2)-(ballDistance*(1+2*layers))/2, row*ballDistance+(screenHeight/2)-ballDistance/2);
							ballCount++;
							Log.d("Debug Pussycat", "Ballhandler jämn ovan: " + ballCount);
							
							balls[ballCount] = new Ball((int)((j+row*0.5)*ballDistance)+(screenWidth/2)-(ballDistance*(1+2*layers))/2, -row*ballDistance+(screenHeight/2)-ballDistance/2);
							ballCount++;
							
							Log.d("Debug Pussycat", "Ballhandler jämn under: " + ballCount);
						}
					}
					else // Ojämn
					{
						for(int j = 0; j < (1 + 2*layers - row); j++){
							balls[ballCount] = new Ball((int)((j+row*0.5)*ballDistance)+(screenWidth/2)-(ballDistance*(1+2*layers))/2, row*ballDistance+(screenHeight/2)-ballDistance/2);
							ballCount++;
							
							Log.d("Debug Pussycat", "Ballhandler ojämn ovan: " + ballCount);
							
							balls[ballCount] = new Ball((int)((j+row*0.5)*ballDistance)+(screenWidth/2)-(ballDistance*(1+2*layers))/2, -row*ballDistance+(screenHeight/2)-ballDistance/2);
							ballCount++;
							Log.d("Debug Pussycat", "Ballhandler ojämn under: " + ballCount);
						}
					}
				}
			}
			/*
			for(int i=0; i<numberOfBalls; i++){
				int slots = screenWidth*1/((1+2*i)*2);
				
				//if even number
				if(i % 2 == 0){
					balls[i] = new Ball(slots*i, screenHeight/2); //villkor 2 ska vara row istället
				}
				
				//if uneven number
				else{
					
				}
			}
			*/
		}
		
        Log.d("Debug Pussycat", "Ballhandler constructor end");
	}
}
