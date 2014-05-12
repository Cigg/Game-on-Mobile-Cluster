package com.pussycat.minions;


import java.nio.ByteBuffer;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.pussycat.framework.Game;
import com.pussycat.framework.Graphics;


import com.pussycat.framework.Input.TouchEvent;
import com.pussycat.framework.Screen;



public class GameScreenMiddle extends Screen {
    enum GameState {
        Ready, Running, Paused, GameOver
    }

    private GameState state = GameState.Running; //used to be ready, but we want to start immediately

    private Paint paint;
    Context context;

	private float currentX, currentY;
	private float downX, downY;

	private float draggedX, draggedY;
	private boolean dragged = false;
	
	private float downTime, previousTime;
	
	float[] pts = new float[2048];
	float[] tms = new float[1024];
	float[] vel = new float[1024];
	float[] ptx = new float[1024];
	float[] pty = new float[1024];
	int index = 0;
	int beginIndex = 1;
	boolean up = false;
	public Bitmap bitmap = null;
	public Canvas bitmapCanvas = null;	  
	float lastAlpha;
	public boolean drawTraceAfter = false;

	
	Button menuButton;
	Button resumeButton;
	Button restartButton;
	Button remapButton;
	Button addButton;
	Button exitButton;
	
	private TCPClient comm;
	private BallHandler ballHandler;
	
	// Game objects
	private Target middleTarget;
	private MusicWidget musicWidget;
	private RemapWidget remapWidget;

	// Constructor
    public GameScreenMiddle(Game game) {
        super(game);

		paint = new Paint();
		paint.setTextSize(30);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		
		int width = PussycatMinions.getScreenWidth()/2 - Assets.button.getWidth()/2;
		
		menuButton = new Button(Assets.settings, Assets.settings_pressed, PussycatMinions.getScreenWidth() - Assets.settings.getWidth() - 30, PussycatMinions.getScreenHeight()-Assets.settings.getHeight() - 30, paint);
        //menuButton.setText("MENU");
		menuButton.scaleButton(game.getGraphics(), (int)(PussycatMinions.getScreenWidth()*0.02));
        resumeButton = new Button(Assets.button, Assets.button_pressed, width, PussycatMinions.getScreenHeight()/2-100, paint);
        resumeButton.setText("RESUME");
        restartButton = new Button(Assets.button, Assets.button_pressed, width, PussycatMinions.getScreenHeight()/2, paint);
        restartButton.setText("RESTART");
        remapButton = new Button(Assets.button, Assets.button_pressed, width, PussycatMinions.getScreenHeight()/2+100, paint);
        remapButton.setText("RE-MAP");
        addButton = new Button(Assets.button, Assets.button_pressed, width, PussycatMinions.getScreenHeight()/2+200, paint);
        addButton.setText("ADD");
        exitButton = new Button(Assets.button, Assets.button_pressed, width, PussycatMinions.getScreenHeight()/2+300, paint);
        exitButton.setText("EXIT");

		middleTarget = new Target(PussycatMinions.getScreenWidth()/2, PussycatMinions.getScreenHeight()/2, 0.06f, game.getGraphics());
		
		previousTime = 0;		
		SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.MAP_DEVICE);		
		
		comm = new TCPClient();
		comm.start();
		
		ballHandler = new BallHandler(PussycatMinions.getScreenWidth(), PussycatMinions.getScreenHeight());
		ServerCommunication serverComm = new ServerCommunication(comm, ballHandler, middleTarget);
		serverComm.start();
		
		musicWidget = new MusicWidget(game.getAudio());
		remapWidget = new RemapWidget();
		
		if(!comm.isRunning()) {
			try {
				synchronized(comm) {
					comm.wait();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(!serverComm.isCommunicating()) {
			try {
				synchronized(serverComm) {
					serverComm.wait();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		syncDevice();
		addDevice();

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
        
        // This example starts with a "Ready" screen.
        // When the user touches the screen, the game begins. 
        // state now becomes GameState.Running.
        // Now the updateRunning() method will be called!
        
        //if (touchEvents.size() > 0)
            state = GameState.Running;
    }
    
    private void syncDevice() {
  		ByteBuffer buffer;
  		buffer = ByteBuffer.allocate(1*2);
  		buffer.clear();
  		buffer.putShort((short) GLOBAL_STATE__.SYNCHRONIZE_DEVICE.ordinal()); 	
  		comm.sendData(buffer.array());
      }
      
      private void addDevice() {
      	ByteBuffer buffer;
  		buffer = ByteBuffer.allocate(2*2 + 4*4);
  		buffer.clear();
  		
  		buffer.putShort((short) GLOBAL_STATE__.ADD_DEVICE.ordinal());	// State: ADD_DEVICE
  		buffer.putShort((short) 0);										// type, 0 är hårdkodat till main-device - sätt 1 för alla andra devices
  		buffer.putInt(PussycatMinions.getXDPI());						// XDPI
  		buffer.putInt(PussycatMinions.getYDPI());						// YDPI
  		buffer.putInt(PussycatMinions.getScreenWidth());				// ResX
  		buffer.putInt(PussycatMinions.getScreenHeight());				// ResY
  		
   		comm.sendData(buffer.array());
      }
      
      private void mapDevice(final float deltaTimeDragged, final float currentTime) {
      	ByteBuffer buffer;
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
      }

    private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {   
    	
    	// Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    	if(SharedVariables.getInstance().shouldStartGame()) {
    		SharedVariables.getInstance().setStartGame(false);
    		SharedVariables.getInstance().setIsRunning(true);
    		SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.RUN_DEVICE);
    		musicWidget.play();
    	}
    	
    	ballHandler.updateBalls(deltaTime);
    	ballHandler.removeBallsNotWanted();
    	
    	up = false;
    	
        for (int i = 0; i < touchEvents.size(); i++) {
            TouchEvent event = touchEvents.get(i);
            
        	currentX = event.x;
    		currentY = event.y;
    		final float currentTime = event.time;
    		
 
    		 if(event.type == TouchEvent.TOUCH_DRAGGED) {
    			draggedX = currentX;
    			draggedY = currentY;  	
    		
    			dragged = true;

    			if(index < ptx.length) {
    				tms[index] = currentTime;
    				ptx[index] = currentX;
    				pty[index] = currentY;
    				index++;
    			}
    			
    		} else if(event.type == TouchEvent.TOUCH_UP) {
    			
    			final float deltaTimeDragged = currentTime - downTime;

    			ByteBuffer buffer; 
    		
    			switch(SharedVariables.getInstance().getInternalState()) {    		
    			
	    			case MAP_DEVICE:
					{
						mapDevice(deltaTimeDragged, currentTime);
						if(SharedVariables.getInstance().isRunning()) {
							SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.RUN_DEVICE);
						} else {
							SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.IS_READY);
						}
					}
					break;
	    				
    				case IS_READY:
    				{
    					buffer = ByteBuffer.allocate(1*2 + 1*4);
    					buffer.putShort((short) GLOBAL_STATE__.IS_READY.ordinal());	// State: IS_READY
    					buffer.putInt(1);
    					comm.sendData(buffer.array());
    					
    					SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.REG);
    				}
    				break;

					case REG: {
    					
    				}
    				break;
    				
    				
					case START_GAME: {
					}
    				
					
    				case RUN_DEVICE:
    				{
    					Log.d("AppStates", "RUN_DEVICE");
    					
    					buffer = ByteBuffer.allocate(1*2 + 5*4);
    		    		buffer.clear();
    		    		
    		    		buffer.putShort((short) GLOBAL_STATE__.RUN_DEVICE.ordinal());	// State: RUN_DEVICE
    		    			
    		    		if(index < 8) {
	    		    		buffer.putFloat(downX);											// x1
	    		    		buffer.putFloat(downY); 										// y1
	    		    		buffer.putFloat(currentX);										// x2
	    		    		buffer.putFloat(currentY);										// y2
	    		    		buffer.putFloat(deltaTimeDragged);								// t	
    		    		} else {
    		    			buffer.putFloat(ptx[index-8]);									// x1
	    		    		buffer.putFloat(pty[index-8]); 									// y1
	    		    		buffer.putFloat(currentX);										// x2
	    		    		buffer.putFloat(currentY);										// y2
	    		    		buffer.putFloat(currentTime - tms[index-8]);					// t	
    		    		}
    		    		
    		    		comm.sendData(buffer.array());
    		    		
    		    		Log.d("CLOCK", "RUNDEVICE ==== " + (System.nanoTime() + SharedVariables.getInstance().getSendDelay()) * Math.pow(10, -9));
    		    		
    				}
    				break;
    				
    			    				
    				default:
    				break;
    			}
    			
    			dragged = false;
    			up = true;
    			drawTraceAfter = true;
    			    			
    		} else if(event.type == TouchEvent.TOUCH_DOWN) {
    			downTime = currentTime;
    			
    			downX = currentX;
    			downY = currentY;
    			
    			if(index < ptx.length) {
    				tms[index] = currentTime;
    				ptx[index] = currentX;
    				pty[index] = currentY;
    				index++;
       			}
    			
    			bitmap = null;
				lastAlpha = 255;
				index = 0;
    			beginIndex = 1;
    			drawTraceAfter = false;
    		}
    		
        }  
                
    }

    
    private void updatePaused(List<TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_DOWN){
		     	if(resumeButton.inBounds(event.x, event.y)){
		     		resumeButton.setPressed(true);
		     	} else if (restartButton.inBounds(event.x, event.y)){
		     		restartButton.setPressed(true);
		     	} else if (remapButton.inBounds(event.x, event.y)){
		     		remapButton.setPressed(true);
		     	} else if (addButton.inBounds(event.x, event.y)){
		     		addButton.setPressed(true);
		     	} else if (exitButton.inBounds(event.x, event.y)){
		     		exitButton.setPressed(true);
		     	}
		     }
		     
		     if(event.type == TouchEvent.TOUCH_DRAGGED){
		     	if(!resumeButton.inBounds(event.x, event.y)){
		     		resumeButton.setPressed(false);
		    	} else if (restartButton.inBounds(event.x, event.y)){
		     		restartButton.setPressed(false);
		     	} else if (remapButton.inBounds(event.x, event.y)){
		     		remapButton.setPressed(false);
		     	} else if (addButton.inBounds(event.x, event.y)){
		     		addButton.setPressed(false);
		     	} else if (exitButton.inBounds(event.x, event.y)){
		     		exitButton.setPressed(false);
		     	}
		      }
		     
		     if (event.type == TouchEvent.TOUCH_UP) {
		     	resumeButton.setPressed(false);
		     	restartButton.setPressed(false);
		     	remapButton.setPressed(false);
		     	addButton.setPressed(false);
		     	exitButton.setPressed(false);
		     	
		    	// TODO: Should go to SetupScreen instead
		     	if(resumeButton.inBounds(event.x, event.y)){
		     		//game.setScreen(new GameScreenPlayer(game));
		     		state = GameState.Running;
		     		
		     	} else if (restartButton.inBounds(event.x, event.y)){
		     		
		     	} else if (remapButton.inBounds(event.x, event.y)){
		     		
		     	} else if (addButton.inBounds(event.x, event.y)){
		     		
		     	} else if (exitButton.inBounds(event.x, event.y)){
	     		//finish();
                 System.exit(0);
		     	}
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
	
	
 	Device device = new Device();
 	
 	
    @Override
    public void paint(float deltaTime) {
    	
        Graphics graphics = game.getGraphics();
        Canvas canvas = graphics.getCanvas();
        
    	device.drawBackground(graphics);
        
    	middleTarget.drawTarget(graphics);

        ballHandler.drawBalls(graphics);
        
        remapWidget.draw(graphics);
   
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
       /* state = GameState.Running;
        
        
        
        g.drawARGB(155, 0, 0, 0);
        g.drawString("Tap to create a ball", 640, 300, paint);
        */
    }

    
    private void drawRunningUI() {
    	Graphics g = game.getGraphics();     
    	//TODO: create button on init. use drawButton here
        
        menuButton.drawButton(g);
    }

    
    private void drawPausedUI() {
        Graphics g = game.getGraphics();
        // Darken the entire screen so you can display the Paused screen.
        g.drawARGB(155, 0, 0, 0);
       
        resumeButton.drawButton(g);
        restartButton.drawButton(g);
        remapButton.drawButton(g);
        addButton.drawButton(g);
        exitButton.drawButton(g);
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