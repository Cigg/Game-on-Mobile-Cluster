package com.pussycat.minions;


import java.nio.ByteBuffer;
import java.util.List;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.pussycat.framework.Game;
import com.pussycat.framework.Graphics;
import com.pussycat.framework.Screen;
import com.pussycat.framework.Input.TouchEvent;

public class GameScreen extends Screen {
    enum GameState {
        Ready, Running, Paused, GameOver
    }

    GameState state = GameState.Ready;
    
    // Variable Setup
    // You would create game objects here.
    private BallHandler ballHandler;
    
    float ballSpeed = 20.0f;
    int screenWidth;
    int screenHeight;
    Paint paint;
    
    
    // New
    private GLOBAL_STATE__ internalState;
    
	private float currentX, currentY;

	private float downX, downY;
	private float downTime, previousTime;
	
	private TCPClient comm;


    public GameScreen(Game game) {
        super(game);
        
        // Get screen dimensions
        screenWidth = PussycatMinions.getScreenWidth();
        screenHeight = PussycatMinions.getScreenHeight();

        // Initialize game objects here
        ballHandler = new BallHandler(1.5f);
        
        // Defining a paint object
		paint = new Paint();
		paint.setTextSize(30);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		
		previousTime = 0;
		internalState = GLOBAL_STATE__.ADD_DEVICE;
		
		// Jocke ska fixa, lugnt
		Thread t = new Thread() {
			public void run() {
				comm = new TCPClient();
				comm.run();
			}
		};
		t.start();
		
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

        // We have four separate update methods in this example.
        // Depending on the state of the game, we call different update methods.
        // Refer to Unit 3's code. We did a similar thing without separating the
        // update methods.

        if (state == GameState.Ready)
            updateReady(touchEvents);
        if (state == GameState.Running)
            updateRunning(touchEvents, deltaTime);
        if (state == GameState.Paused)
            updatePaused(touchEvents);
        if (state == GameState.GameOver)
            updateGameOver(touchEvents);
    }
    
    private void updateReady(List<TouchEvent> touchEvents) {
        
        // This example starts with a "Ready" screen.
        // When the user touches the screen, the game begins. 
        // state now becomes GameState.Running.
        // Now the updateRunning() method will be called!
        
        if (touchEvents.size() > 0)
            state = GameState.Running;
    }

    private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {
        
        //This is identical to the update() method from our Unit 2/3 game.
    	
    	// Update balls
    	//ballHandler.update();
    	
        
       // Update touch events
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            /*
            if(event.type == TouchEvent.TOUCH_DOWN){
            	//ballHandler.addBall(0,event.x,event.y,1.5f);
            	System.out.println(event.x + " " + event.y);
            }
            if(event.type == TouchEvent.TOUCH_DRAGGED) {
            	ballHandler.addBall(0,event.x,event.y,1.5f);
            }
            if (event.type == TouchEvent.TOUCH_UP) {
            	Log.d("Debug Pussycat", "touch event x: " + event.x + ": " + event.y);
            	float speedX = (screenWidth/2 - event.x);
            	float speedY = (screenHeight/2 - event.y);
            	float tempDist = (float)Math.sqrt(speedX*speedX + speedY*speedY);
            	speedX = ballSpeed*speedX/tempDist;
            	speedY = ballSpeed*speedY/tempDist;
            	//ballHandler.addBall(event.x, event.y, 1.5f, speedX, speedY);
            	if(ballHandler.tcpClient != null){
            		ballHandler.tcpClient.sendMessage(event.x + " " + event.y + " " + speedX + " " + speedY);
            	}
            }
            
            */
        	currentX = event.x;
    		currentY = event.y;
    		final float currentTime = event.time;
    	
    		
    		if(event.type == TouchEvent.TOUCH_DOWN) {
    			downTime = currentTime;
    			downX = currentX;
    			downY = currentY;

    		} else if(event.type == TouchEvent.TOUCH_DRAGGED) {
    			
    		} else if(event.type == TouchEvent.TOUCH_UP) {
    			
    			final float deltaTimeT = currentTime - downTime;

    			ByteBuffer buffer; 
    			
    			switch(internalState) {
    				case ADD_DEVICE:
    					Log.d("AppStates", "ADD_DEVICE");
    					
    					buffer = ByteBuffer.allocate(1*2 + 4*4);
    		    		buffer.clear();
    		    		
    		    		buffer.putShort((short) 0);							// State: ADD_DEVICE

    		    		buffer.putInt(PussycatMinions.getXDPI());			// XDPI
    		    		buffer.putInt(PussycatMinions.getYDPI());			// YDPI
    		    		buffer.putInt(PussycatMinions.getScreenWidth());	// ResX
    		    		buffer.putInt(PussycatMinions.getScreenHeight());	// ResY
    		    		
    		     		comm.sendData(buffer.array());
    		    		
    					internalState = GLOBAL_STATE__.MAP_DEVICE;
    					//internalState = GLOBAL_STATE__.RUN_DEVICE;
    				break;
    				
    				
    				case MAP_DEVICE:
    					Log.d("AppStates", "MAP_DEVICE");
    					buffer = ByteBuffer.allocate(2*2 + 5*4);
    		    		buffer.clear();
    					    		
    		    		buffer.putShort((short) 1);		// State: MAP_DEVICE
    		    		
    		    		buffer.putFloat(downX);			// x1
    		    		buffer.putFloat(downY); 		// y1
    		    		buffer.putFloat(currentX);		// x2
    		    		buffer.putFloat(currentY);		// y2
    		    		buffer.putFloat(deltaTimeT);    // t

    		    		comm.sendData(buffer.array());

    		    		internalState = GLOBAL_STATE__.RUN_DEVICE;
    				break;
    				
    				case RUN_DEVICE:
    					Log.d("AppStates", "RUN_DEVICE");
    					
    					buffer = ByteBuffer.allocate(1*2 + 5*4);
    		    		buffer.clear();
    		    		
    		    		buffer.putShort((short) 2);		// State: RUN_DEVICE

    		    		buffer.putFloat(downX);			// x1
    		    		buffer.putFloat(downY); 		// y1
    		    		buffer.putFloat(currentX);		// x2
    		    		buffer.putFloat(currentY);		// y2
    		    		buffer.putFloat(deltaTimeT);	// t
    		    		
    		    		comm.sendData(buffer.array());
    				break;
    				
    				
    				default:
    				break;
    			}
    			
    			previousTime = currentTime;
    		}
        }  
        
        // Update communication
        ballHandler.clearBalls();
        while(!comm.messages.isEmpty()){
        	DataPackage data = comm.messages.poll();
        	
        	if(data != null) {
        		ByteBuffer buffer = ByteBuffer.wrap(data.getData());
        		short state = buffer.getShort();
				
        		GLOBAL_STATE__ actualState;
				
				try {
					actualState = GLOBAL_STATE__.values()[state];
				} catch(Exception e) {
					actualState = GLOBAL_STATE__.ADD_BALL;
				}
				
	    		String ip = data.getIp();
	    		
	    		switch(actualState) {
	    			case ADD_BALL:
	        			float xPos = buffer.getInt();
			        	float yPos = buffer.getInt();	
	        			float xVel = buffer.getFloat();	
	        			float yVel = buffer.getFloat();		
	        			Log.d("GOT", "GOT from " + ip + "  :   " + xPos + ", " + yPos + "   " + xVel + ", " + yVel);
	        			
	        			ballHandler.addBall(xPos, yPos, 1, xVel, yVel);
	    			break;
	    			
	    			case SET_STATE:
	    				short newState = buffer.getShort();
	    				Log.d("GOT", "NEW STATE: " + newState);
	    			break;
	    			
	    			default:
	    			break;
	    		}
        	}
        }
        
        
    }

    private void updatePaused(List<TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {

            }
        }
    }

    private void updateGameOver(List<TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if (event.x > 300 && event.x < 980 && event.y > 100
                        && event.y < 500) {
                    nullify();
                    game.setScreen(new MainMenuScreen(game));
                    return;
                }
            }
        }

    }

    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();

        g.drawARGB(255,100,100,100);
        // First draw the game elements.

        // Example:
        //g.drawImage(Assets.menu, 0, 0);
        drawBalls();
     
        // Secondly, draw the UI above the game elements.
        if (state == GameState.Ready)
            drawReadyUI();
        if (state == GameState.Running)
            drawRunningUI();
        if (state == GameState.Paused)
            drawPausedUI();
        if (state == GameState.GameOver)
            drawGameOverUI();

    }

    private void nullify() {

        // Set all variables to null. You will be recreating them in the
        // constructor.
        paint = null;

        // Call garbage collector to clean up memory.
        System.gc();
    }
    
    private void drawBalls(){
    	Graphics g = game.getGraphics();
    	
    	Log.d("Debug Pussycat", "balls.size(): " + ballHandler.balls.size());
    	
    	for(int i = 0; i < ballHandler.balls.size(); i++){
        	g.drawScaledImage(ballHandler.balls.get(i).getImage(), (int)(ballHandler.balls.get(i).getX()-ballHandler.balls.get(i).getDiameter()/2), (int)(ballHandler.balls.get(i).getY()-ballHandler.balls.get(i).getDiameter()/2), (int)ballHandler.balls.get(i).getDiameter(), (int)ballHandler.balls.get(i).getDiameter(), 0, 0, 128, 128);
    	}
    }

    private void drawReadyUI() {
        Graphics g = game.getGraphics();
        
        g.drawARGB(155, 0, 0, 0);
        g.drawString("Tap to create a ball",
                640, 300, paint);

    }

    private void drawRunningUI() {
        Graphics g = game.getGraphics();
        
    }

    private void drawPausedUI() {
        Graphics g = game.getGraphics();
        // Darken the entire screen so you can display the Paused screen.
        g.drawARGB(155, 0, 0, 0);

    }

    private void drawGameOverUI() {
        Graphics g = game.getGraphics();
        g.drawRect(0, 0, 1281, 801, Color.BLACK);
        g.drawString("GAME OVER.", 640, 300, paint);

    }

    @Override
    public void pause() {
        if (state == GameState.Running)
            state = GameState.Paused;

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void backButton() {
        pause();
    }
}