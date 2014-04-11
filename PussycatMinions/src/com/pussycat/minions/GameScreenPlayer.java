package com.pussycat.minions;


import java.nio.ByteBuffer;
import java.util.ArrayList;
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
	float[] tms = new float[1024];
	float[] vel = new float[1024];
	float[] ptx = new float[1024];
	float[] pty = new float[1024];
	int index = 0;
	int beginIndex = 1;
	public Bitmap bitmap = null;
	public Canvas bitmapCanvas = null;	  

	
	private TCPClient comm;
	private BallzHandler ballzHandler;

	// Constructor
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
    			
    			if(index < ptx.length) {
    				//tms[(int)Math.ceil(index / 2)] = currentTime;
	    			//pts[index++] = currentX;
	    			//pts[index++] = currentY;
    				tms[index] = currentTime;
    				ptx[index] = currentX;
    				pty[index] = currentY;
    				index++;
    				
	    			//points.add(new Point(currentX, currentY, currentTime));
    			}
    			
    		} else if(event.type == TouchEvent.TOUCH_DRAGGED) {
    			draggedX = currentX;
    			draggedY = currentY;  		
    			dragged = true;
    			
    			if(index < ptx.length) {
    				//tms[(int)Math.ceil(index / 2)] = currentTime;
	    			//pts[index++] = currentX;
	    			//pts[index++] = currentY;
    				tms[index] = currentTime;
    				ptx[index] = currentX;
    				pty[index] = currentY;
    				index++;
    				
	    			//addPoint(new Point(currentX, currentY, currentTime));
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

    		    		buffer.putShort((short) 0);										// type, 0 är hårdkodat till main-device - sätt 1 för alla andra devices
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
    		    			
    		    		if(index < 8) {
	    		    		buffer.putFloat(downX);											// x1
	    		    		buffer.putFloat(downY); 										// y1
	    		    		buffer.putFloat(currentX);										// x2
	    		    		buffer.putFloat(currentY);										// y2
	    		    		buffer.putFloat(deltaTimeDragged);								// t	
    		    		} else {
    		    			buffer.putFloat(ptx[index-8]);											// x1
	    		    		buffer.putFloat(pty[index-8]); 										// y1
	    		    		buffer.putFloat(currentX);										// x2
	    		    		buffer.putFloat(currentY);										// y2
	    		    		buffer.putFloat(currentTime - tms[index-8]);								// t	
    		    		}
    		    		
    		    		comm.sendData(buffer.array());
    		    		
    		    		Log.d("CLOCK", "RUNDEVICE ==== " + (System.nanoTime() + SharedVariables.getInstance().getSendDelay()) * Math.pow(10, -9));
    				}
    				break;
    				
    				default:
    				break;
    			}
    			
    			dragged = false;
    			index = 0;
    			beginIndex = 1;
    			bitmap = null;
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

    
    
    float linearInterpolation(float x1, float x2, float scale) {
		return x1 * (1 - scale) + x2 * scale;
	}
	
	float cosineInterpolation(float x1, float x2, float scale) {
		float scaleModified = (float) ((1 - Math.cos(scale * Math.PI)) / 2);
		return x1 * (1 - scaleModified) + x2 * scaleModified;
	}
	
	float cubicInterpolation(float x0, float x1, float x2, float x3, float scale) {
		float a0, a1, a2, a3, scale2;
		scale2 = scale * scale;
		a0 = x3 - x2 - x0 + x1;
		a1 = x0 - x1 - a0;
		a2 = x2 - x0;
		a3 = x1;
		return (a0*scale*scale2 + a1*scale2 + a2*scale + a3); 
	}
	
	

	public float x1 = ptx[0];
	public float y1 = pty[0];
	public float t1 = tms[0];
	public float v1 = 0;

	

 	public float xk = 0;
 	public float yk = 0;
	
 	public float xz = 0;
 	public float yz = 0;
	
 	public float vk = 0;
 	public float vz = 0;
	
 	
    @Override
    public void paint(float deltaTime) {
    	
        Graphics graphics = game.getGraphics();
        Canvas canvas = graphics.getCanvas();
        
        
        graphics.drawImage(Assets.background, 0, 0);
       
        
        
        // TODO: OPTIMERA!!
        boolean tail = false;
        if( dragged && tail) {
        	
        	  if (bitmap == null) {
        		    bitmap = Bitmap.createBitmap(PussycatMinions.getScreenWidth(), PussycatMinions.getScreenHeight(), Bitmap.Config.ARGB_8888);
        		    bitmapCanvas = new Canvas(bitmap);
        		  }
        	
        	
  			paint.setColor(Color.YELLOW);
        	
        	
        	int radious = 50;
        	paint.setColor(Color.YELLOW);
        
        	
        	for(int i = beginIndex ; i<index ; i += 4) {
        		
        		float x2 = ptx[i];
        		float y2 = pty[i];        		
        		float t2 = tms[i];
        		
        		float dx = x2 - x1;
        		float dy = y2 - y1;
        		float dt = t2 - t1;
        		
        		vel[i] = (float) (Math.sqrt( Math.pow(dx, 2) + Math.pow(dy, 2) ) / dt);
        		
        		
        		float v2 = vel[i];

        		
        		if( i > 7 ) {
	        		int steps = 512;
	        		
	        		float scale;
	        		float ix;
	        		float iy;
	        		float iv;
	        		
	        		for(int j=0 ; j<steps; j++) {
	        			
	        			scale = j / (float) steps;  // Kan räknas ut i förväg
	            	    ix = cubicInterpolation(xk, xz, x1, x2, scale);
	            	    iy = cubicInterpolation(yk, yz, y1, y2, scale);
	        			//float iv = cubicInterpolation(vk, vz, v1, v2, scale);
	        			
	        			//float ix = cosineInterpolation(x1, x2, scale);
	        			//float iy = cosineInterpolation(y1, y2, scale);
	            	    iv = linearInterpolation(v1, v2, scale);
	        			
	        			//float iv = cosineInterpolation(v1, v2, scale);
	            	    
	            	    // Går att förenkla
	            	    iv = (float) (iv * Math.pow(10, 6));
	            	    iv = Math.max(iv, 1);
	            	    iv = Math.max(1, 6 / iv);
	            	    
	            	    paint.setStrokeWidth( iv ) ;
	      
	        			bitmapCanvas.drawPoint(ix, iy, paint);
	        			
	        		}
	        		
	        		beginIndex = i + 4;
        		}
        		
        		
        		
        		/*
        		paint.setStrokeWidth(10);
        		paint.setColor(Color.RED);
        		canvas.drawPoint(ptx[i], pty[i], paint);
        		*/
        		
        		t1 = t2;
        		
        		vk = vz;
        		vz = v1;
        		v1 = v2;
        		
        		xk = xz;
        		yk = yz;
        		
        		xz = x1;
        		yz = y1;
        		
        		x1 = x2;
        		y1 = y2;
	
        		
        	}
        	
        	canvas.drawBitmap(bitmap, 0, 0, paint);
       
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