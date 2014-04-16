package com.pussycat.minions;


import java.util.ArrayList;


import java.nio.ByteBuffer;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import com.pussycat.framework.FPSCounter;
import com.pussycat.framework.Game;
import com.pussycat.framework.Graphics;


import com.pussycat.framework.Input.TouchEvent;
import com.pussycat.framework.Screen;

public class GameScreenMiddle extends Screen {
    enum GameState {
        Ready, Running, Paused, GameOver
    }

    private GameState state = GameState.Running; //used to be ready, but we want to start immediately

    // Variable Setup
    private Paint paint;
    private FPSCounter fpsCounter;
    Context context;

    private GLOBAL_STATE__ internalState;
    private int nClocks;
    private float reciveDelay, sendDelay;
    
	private float currentX, currentY;
	private float downX, downY;
	private float downTime;
	private float timeBegin, timeEnd, timeDelta;
	
	Button pauseButton;
	Button resumeButton;
	Button restartButton;
	Button remapButton;
	Button addButton;
	Button exitButton;
	
	private TCPClient comm;

	// Constructor
    public GameScreenMiddle(Game game) {
        super(game);

        // Initialize game objects here
        
        // Defining a paint object
		paint = new Paint();
		paint.setTextSize(30);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		fpsCounter = new FPSCounter();
		Log.d("Debug Pussycat", "GameScreen constructor");
		
		internalState = GLOBAL_STATE__.SYNCHRONIZE_DEVICE;
		nClocks = 0;
		reciveDelay = 0;
		sendDelay = 0;
		
		
		// TODO: Fixa snyggt n�gonstans
		Thread t = new Thread() {
			public void run() {
				comm = new TCPClient();
				comm.run();
			}
		};
		t.setName("TCPCLIENT");
		t.start();
		

		Thread t2 = new Thread() {
			public void run() {
				
				while(true) {
					
					if(comm != null){
					DataPackage data = (DataPackage) comm.messages.popFront();

		        	if(data != null) {
		        		ByteBuffer buffer = ByteBuffer.wrap(data.getData());
		        		short state = buffer.getShort();
						
		        		GLOBAL_STATE__ actualState;
						
						try {
							actualState = GLOBAL_STATE__.values()[state];
						} catch(Exception e) {
							// do nothing
							actualState = GLOBAL_STATE__.REG;
						}
						
			    		data.getIp();
			    		
			    		switch(actualState) {
			    		
				    		case SYNCHRONIZE_DEVICE:
			    			{		    				
			    				float t1 = buffer.getFloat();
			    				float t2 = buffer.getFloat();
			    				float t3 = data.getSendTime();
			    				float t4 = data.getReciveTime();
			    									
			    				nClocks ++;
			    				sendDelay = t2 - t1;
			    				reciveDelay = -(t4 - t3);
			    				Log.d("CLOCK", "DelayDifference = " + (reciveDelay - sendDelay)* Math.pow(10, -9));
			    			    				
			    				
			    				Log.d("CLOCK", "reciveDelay = " + reciveDelay* Math.pow(10, -9) + " sendDelay = " + sendDelay* Math.pow(10, -9));
			    				Log.d("CLOCK", "CLock: " + nClocks + " = " + reciveDelay + " = " + reciveDelay * Math.pow(10, -9));
			    				Log.d("CLOCK", "CLOCK ==== " + (System.nanoTime() + reciveDelay) * Math.pow(10, -9) );
			    			}
			    			break;
			    			
			    			case ADD_BALLS:
			    			{
			    				final short nBalls = buffer.getShort();
					        			
			    				for(int i=0; i<nBalls; i++) {
			    					int id = buffer.getInt();
			    					float xPos = buffer.getFloat();
						        	float yPos = buffer.getFloat();	
				        			float xVel = buffer.getFloat();	
				        			float yVel = buffer.getFloat();					        			
	
				        		//	ballHandler.addBall(id, xPos, yPos, 1, xVel, yVel, false, /*System.nanoTime() + reciveDelay - data.getSendTime()*/ reciveDelay , data.getSendTime());
			    				}
			    			}
			    			break;
			    			
			    			case SET_STATE:
			    			{
			    				short newState = buffer.getShort();
			    				
			    				Log.d("FINGERS", "GOT: " + newState);
			    				Log.d("GOT", "NEW STATE: " + newState);
			    				
			    				GLOBAL_STATE__ newInternalState;
			    				
		        				try {
		        					newInternalState = GLOBAL_STATE__.values()[newState];
								} catch(Exception e) {
									newInternalState = internalState;
									System.out.println("ERROR: Invalid state: " + newState);
								}
		        				
		        				 internalState = newInternalState;
			    			}
			    			break;
			    			
			    			case REG:
			    			break;
			    			
			    			default:
			    			break;
			    		}
		        	} else {
		    			synchronized (comm.messages) {
							try {
								comm.messages.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						};
		        	}
				}
		        }
			
			}
		};
		t2.setName("COMMUNICATION");
		t2.start();
		
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
        
        //if (touchEvents.size() > 0)
            state = GameState.Running;
             
            
    }

    private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {
    	timeBegin = System.nanoTime();
    	timeDelta = timeBegin - timeEnd;
    	
    	Log.d("TIME", "deltaTime = " + deltaTime);
    	Log.d("TIME2", "(float) Math.pow(10, 9) / 60 = " + (float) Math.pow(10, 9) / 60);
    	Log.d("TIME", "timeDelta = " + timeDelta);
    	Log.d("FPS", "FPS: " +  Math.pow(10, 9) /timeDelta);
    	 
    	 
       	// Update balls
    //	ballHandler.update(reciveDelay);
        
        // All touch input is handled here:
       // Update touch events
    	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
        	
            TouchEvent event = touchEvents.get(i);
        	currentX = event.x;
    		currentY = event.y;
    		final float currentTime = event.time;
    		
    		//Log.d("FINGERS", "FINGERS: " + event.pointer);
    		if(event.pointer >= 2) {
    			Log.d("AppStates", "SEND SET_STATE");
    			
    			ByteBuffer buffer = ByteBuffer.allocate(2*2);
    			
    			buffer.putShort((short) GLOBAL_STATE__.SET_STATE.ordinal());	// State: SET_STATE
    			buffer.putShort((short) 6);										// New state: MAP_MAIN
    			
    			comm.sendData(buffer.array());
    			
    			internalState = GLOBAL_STATE__.MAP_DEVICE;
    			
    		} else if(event.type == TouchEvent.TOUCH_DOWN) {
    			downTime = currentTime;
    			downX = currentX;
    			downY = currentY;

    		} else if(event.type == TouchEvent.TOUCH_DRAGGED) {
    			
    		} else if(event.type == TouchEvent.TOUCH_UP) {
    			
    			final float deltaTimeT = currentTime - downTime;

    			ByteBuffer buffer; 
    			
    			// TODO: Fixa race condition p� internalState
    			// Flytta ut all kommunikationskod till n�got annat st�lle?
    			switch(internalState) {
    			
	    			case SYNCHRONIZE_DEVICE:
	    			{
	    				Log.d("AppStates", "SYNCHRONZE_DEVICE");
	    				
	    				buffer = ByteBuffer.allocate(1*2);
	    				buffer.clear();
	    				
	    				buffer.putShort((short) GLOBAL_STATE__.SYNCHRONIZE_DEVICE.ordinal()); 	// State: SYNCHRONZE_DEVICE
	    				
	    				comm.sendData(buffer.array());
	    				
	    				internalState = GLOBAL_STATE__.ADD_DEVICE;
	    			}
	    			//break;
    			
    				case ADD_DEVICE:
    				{
    					Log.d("AppStates", "ADD_DEVICE");
    					
    					buffer = ByteBuffer.allocate(2*2 + 4*4);
    		    		buffer.clear();
    		    		
    		    		buffer.putShort((short) GLOBAL_STATE__.ADD_DEVICE.ordinal());	// State: ADD_DEVICE

    		    		buffer.putShort((short) 0);										// type, 0 �r h�rdkodat till main-device - s�tt 1 f�r alla andra devices
    		    		buffer.putInt(PussycatMinions.getXDPI());						// XDPI
    		    		buffer.putInt(PussycatMinions.getYDPI());						// YDPI
    		    		buffer.putInt(PussycatMinions.getScreenWidth());				// ResX
    		    		buffer.putInt(PussycatMinions.getScreenHeight());				// ResY
    		    		
    		     		comm.sendData(buffer.array());
    		    		
    					internalState = GLOBAL_STATE__.MAP_DEVICE;
    					
    				}
    				break;
    				
    				case MAP_DEVICE:
    				{
    					Log.d("AppStates", "MAP_DEVICE");
    					
    					buffer = ByteBuffer.allocate(1*2 + 6*4);
    		    		buffer.clear();
    					    		
    		    		buffer.putShort((short) GLOBAL_STATE__.MAP_DEVICE.ordinal());	// State: MAP_DEVICE
    		    		
    		    		buffer.putFloat(downX);											// x1
    		    		buffer.putFloat(downY); 										// y1
    		    		buffer.putFloat(currentX);										// x2
    		    		buffer.putFloat(currentY);										// y2
    		    		buffer.putFloat(deltaTimeT);    								// t
    		    		buffer.putFloat(currentTime + sendDelay);	

    		    		comm.sendData(buffer.array());

    		    		internalState = GLOBAL_STATE__.RUN_DEVICE;
    				}
    				break;
    				
    				case RUN_DEVICE:
    				{
    					Log.d("AppStates", "RUN_DEVICE");
    				}
    				break;
    				
    				default:
    				break;
    			}
    			
    		
    		}
    		
    		
        }  
        
        timeEnd = timeBegin;
        
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
//        Canvas c = g.getCanvas();
//        Log.d("Debug Pussycat", "canvas height: " + c.getHeight());
//        Log.d("Debug Pussycat", "canvas width: " + c.getWidth());

        g.drawARGB(255,100,100,100);
        // First draw the game elements.

        // Example:
        //g.drawImage(Assets.menu, 0, 0);
        g.drawImage(Assets.background, 0, 0);
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
    	
    	//Log.d("Debug Pussycat", "balls.size(): " + ballHandler.balls.size());s
    	//fpsCounter.logFrame();
    	
    	/*
    	if(mobileBall != null){
    		g.drawScaledImage(mobileBall.getImage(), (int)(mobileBall.getX()-mobileBall.getDiameter()/2), (int)(mobileBall.getY()-mobileBall.getDiameter()/2), (int)mobileBall.getDiameter(), (int)mobileBall.getDiameter(), 0, 0, 128, 128);
    	}
    	*/
    	
    	// Needs optimmiiiziziiziizzeing!
    }
  

    private void drawReadyUI() {
        Graphics g = game.getGraphics();
       /* state = GameState.Running;
        
        g.drawARGB(155, 0, 0, 0);
        g.drawString("Tap to create a ball",
                640, 300, paint); */
        
        /*
        // Draw frog image outline
        g.drawLine(4,0, 2,1, Color.RED);
        g.drawLine(2,1, 7,0, Color.RED);
        g.drawLine(6,1, 6,6, Color.RED);
        g.drawLine(6,7, 7,8, Color.RED);
        g.drawLine(7,8, 2,1, Color.RED);
        g.drawLine(2,1, 4,0, Color.RED);
        */
    }

    private void drawRunningUI() {
        game.getGraphics();
        
        pauseButton = resumeButton = new Button(Assets.button_player, Assets.button_player_pressed, PussycatMinions.getScreenWidth() - 100, PussycatMinions.getScreenHeight()-30);
        
    }

    private void drawPausedUI() {
        Graphics g = game.getGraphics();
        // Darken the entire screen so you can display the Paused screen.
        g.drawARGB(155, 0, 0, 0);
        int width = PussycatMinions.getScreenWidth()/2 - Assets.button_middle.getWidth()/2;
        resumeButton = new Button(Assets.button_player, Assets.button_player_pressed, width, PussycatMinions.getScreenHeight()/2-100);
        restartButton = new Button(Assets.button_player, Assets.button_player_pressed, width, PussycatMinions.getScreenHeight()/2);
        remapButton = new Button(Assets.button_player, Assets.button_player_pressed, width, PussycatMinions.getScreenHeight()/2+100);
        addButton = new Button(Assets.button_player, Assets.button_player_pressed, width, PussycatMinions.getScreenHeight()/2+200);
        exitButton = new Button(Assets.button_player, Assets.button_player_pressed, width, PussycatMinions.getScreenHeight()/2+300);
        

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
    
    public FPSCounter getFPSCounter() {
    	return fpsCounter;
    }
    
}