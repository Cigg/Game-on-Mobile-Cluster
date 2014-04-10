package com.pussycat.minions;


import java.nio.ByteBuffer;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.pussycat.framework.Game;
import com.pussycat.framework.Graphics;


import com.pussycat.framework.Input.TouchEvent;
import com.pussycat.framework.Screen;



public class GameScreenPlayer extends Screen {
    enum GameState {
        Ready, Running, Paused, GameOver
    }

    private GameState state = GameState.Ready;
    
    private Paint paint;
    Context context;
    
    
	private float currentX, currentY;

	private float downX, downY;
	private float draggedX, draggedY;
	private boolean dragged = false;
	
	private float downTime, previousTime;
	
	float[] pts = new float[2048];
	int index = 0;

	
	private TCPClient comm;
	private BallzHandler ballzHandler;


    public GameScreenPlayer(Game game) {
        super(game);
        
        
		paint = new Paint();
		paint.setTextSize(30);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		
		
		previousTime = 0;		
		SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.SYNCHRONIZE_DEVICE);		

		
		comm = new TCPClient();
		comm.start();

		ballzHandler = new BallzHandler();
		ServerCommunication t4 = new ServerCommunication(comm, ballzHandler);
		t4.start();

		
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        
        if (state == GameState.Running) {
            updateRunning(touchEvents, deltaTime);
        } else if (state == GameState.Ready) {
            updateReady(touchEvents);
    	} else if (state == GameState.Paused) {
            updatePaused(touchEvents);
		} else if (state == GameState.GameOver) {
            updateGameOver(touchEvents);
		}
        
    }
    
    private void updateReady(List<TouchEvent> touchEvents) {
        if (touchEvents.size() > 0) {
            state = GameState.Running;
        }
    }

    private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {   
    	
    	// Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    	
    	ballzHandler.updateBalls(deltaTime);
        
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
    			
    			SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.MAP_DEVICE);
    			
    		} else if(event.type == TouchEvent.TOUCH_DOWN) {
    			downTime = currentTime;
    			
    			downX = currentX;
    			downY = currentY;
    			
    			if(index < pts.length - 2) {
	    			pts[index++] = currentX;
	    			pts[index++] = currentY;
    			}
    			
    		} else if(event.type == TouchEvent.TOUCH_DRAGGED) {
    			draggedX = currentX;
    			draggedY = currentY;  		
    			dragged = true;
    			
    			if(index < pts.length - 2) {
	    			pts[index++] = currentX;
	    			pts[index++] = currentY;
    			}
    			
    		} else if(event.type == TouchEvent.TOUCH_UP) {
    			
    			final float deltaTimeDragged = currentTime - downTime;

    			ByteBuffer buffer; 
    			
    			switch(SharedVariables.getInstance().getInternalState()) {
    			
	    			case SYNCHRONIZE_DEVICE:
	    			{
	    				Log.d("AppStates", "SYNCHRONZE_DEVICE");
	    				
	    				buffer = ByteBuffer.allocate(1*2);
	    				buffer.clear();
	    				
	    				buffer.putShort((short) GLOBAL_STATE__.SYNCHRONIZE_DEVICE.ordinal()); 	// State: SYNCHRONZE_DEVICE
	    				
	    				comm.sendData(buffer.array());
	    				SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.ADD_DEVICE);
	    			}
	    			break;
    			
    				case ADD_DEVICE:
    				{
    					Log.d("AppStates", "ADD_DEVICE");
    					
    					buffer = ByteBuffer.allocate(2*2 + 4*4);
    		    		buffer.clear();
    		    		
    		    		buffer.putShort((short) GLOBAL_STATE__.ADD_DEVICE.ordinal());	// State: ADD_DEVICE

    		    		buffer.putShort((short) 1);										// type, 0 är hårdkodat till main-device - sätt 1 för alla andra devices
    		    		buffer.putInt(PussycatMinions.getXDPI());						// XDPI
    		    		buffer.putInt(PussycatMinions.getYDPI());						// YDPI
    		    		buffer.putInt(PussycatMinions.getScreenWidth());				// ResX
    		    		buffer.putInt(PussycatMinions.getScreenHeight());				// ResY
    		    		
    		     		comm.sendData(buffer.array());
    		     		SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.MAP_DEVICE);
    					
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
    		    		buffer.putFloat(deltaTimeDragged);    							// t
    		    		buffer.putFloat(currentTime + SharedVariables.getInstance().getSendDelay());	

    		    		comm.sendData(buffer.array());

    		    		SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.RUN_DEVICE);
    				}
    				break;
    				
    				case RUN_DEVICE:
    				{
    					Log.d("AppStates", "RUN_DEVICE");
    					
    					buffer = ByteBuffer.allocate(1*2 + 5*4);
    		    		buffer.clear();
    		    		
    		    		buffer.putShort((short) GLOBAL_STATE__.RUN_DEVICE.ordinal());	// State: RUN_DEVICE

    		    		buffer.putFloat(downX);											// x1
    		    		buffer.putFloat(downY); 										// y1
    		    		buffer.putFloat(currentX);										// x2
    		    		buffer.putFloat(currentY);										// y2
    		    		buffer.putFloat(deltaTimeDragged);								// t	
    		    		
    		    		comm.sendData(buffer.array());
    		    		
    		    		Log.d("CLOCK", "RUNDEVICE ==== " + (System.nanoTime() + SharedVariables.getInstance().getSendDelay()) * Math.pow(10, -9));
    				}
    				break;
    				
    				default:
    				break;
    			}
    			
    			dragged = false;
    			index = 0;
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
                if (event.x > 300 && event.x < 980 && event.y > 100 && event.y < 500) {
                    nullify();
                    game.setScreen(new MainMenuScreen(game));
                    return;
                }
            }
        }

    }

    
    @Override
    public void paint(float deltaTime) {
    	
        Graphics graphics = game.getGraphics();
        
        graphics.drawImage(Assets.background, 0, 0);

        if( dragged ) {
        	
        	Canvas canvas = graphics.getCanvas();
        	
        	int radious = 50;
        	
        	//paint.setColor(Color.BLUE);
        	//g.drawLine((int)downX, (int)downY, (int)currentX, (int)currentY, Color.RED);
        	
        	//paint.setStrokeWidth(3);
        	
        	paint.setColor(Color.YELLOW);

        	canvas.drawLines(pts, 0, index, paint);
        	canvas.drawLines(pts, 2, index - 2, paint);
        	
        	//c.drawCircle(downX, downY, 5, paint);
       
        	graphics.drawScaledImage(	Assets.ball, 
									 	(int)draggedX - radious, 
									 	(int)draggedY - radious, 
									 	100, 
									 	100, 
									 	0, 
									 	0, 
									 	128, 
									 	128		);
	        
        }
        
        BallzHandler.drawBalls(graphics);
   
        if (state == GameState.Running) {
            drawRunningUI();
    	} else if (state == GameState.Ready) {
            drawReadyUI();
        } else if (state == GameState.Paused) {
            drawPausedUI();
		} else if (state == GameState.GameOver) {
			drawGameOverUI();
		}
        
    }


    private void drawReadyUI() {
        Graphics g = game.getGraphics();
        
        g.drawARGB(155, 0, 0, 0);
        g.drawString("Tap to create a ball", 640, 300, paint);
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
    
    
    private void nullify() {
        // Set all variables to null. You will be recreating them in the constructor.
        paint = null;

        // Call garbage collector to clean up memory.
        System.gc();
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