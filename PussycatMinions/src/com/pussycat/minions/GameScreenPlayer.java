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
	int index = 0;

	
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
    
    
    /*
    
    float lastVelocity;
    float lastWidth;
    
    
    public class Point {
    	private final float x;
    	private final float y;
    	private final float time;
    	
    	Point(float x, float y, float time) {
    		this.x = x;
    		this.y = y;
    		this.time = time;
    	}
    	
    	private float distanceTo(Point start)  {
    		return (float) Math.sqrt( Math.pow(this.x - start.x, 2) + Math.pow(this.y - start.y, 2) );
    	}
    	
    	public float velocityFrom(Point start) {
    		return distanceTo(start) / (this.time - start.time);
    	}
    }
    
    public class Control {
    	float x;
    	float y;
    	
    	Control(float x, float y) {
    		this.x = x;
    		this.y = y;
    	}
    }
    
    public class Bezier {
    	
    	private final Point startPoint;
    	private final Point endPoint;
    	
    	private Control control1;
    	private Control control2; 
    	
    	Bezier(Point lastPoint, Point newPoint) {
    		this.startPoint = lastPoint;
    		this.endPoint = newPoint;
    		
    		control1 = new Control(0.5f, 0.5f);
    		control2 = new Control(0.5f, 0.5f);
    	}
    	
    	// Draws a variable-width Bezier curve. 
    	public void draw(Canvas canvas, Paint paint, float startWidth, float endWidth) {
    	  float originalWidth = paint.getStrokeWidth();
    	  float widthDelta = endWidth - startWidth;
    	  
    	  int drawSteps = 2;
    	  
    	  
    	  for (int i = 0; i < drawSteps; i++) {
    	    // Calculate the Bezier (x, y) coordinate for this step.
    	    float t = ((float) i) / drawSteps;
    	    float tt = t * t;
    	    float ttt = tt * t;
    	    float u = 1 - t;
    	    float uu = u * u;
    	    float uuu = uu * u;

    	    float x = uuu * startPoint.x;
    	    x += 3 * uu * t * control1.x;
    	    x += 3 * u * tt * control2.x;
    	    x += ttt * endPoint.x;

    	    float y = uuu * startPoint.y;
    	    y += 3 * uu * t * control1.y;
    	    y += 3 * u * tt * control2.y;
    	    y += ttt * endPoint.y;

    	    // Set the incremental stroke width and draw.
    	    paint.setStrokeWidth(startWidth + ttt * widthDelta);
    	    canvas.drawPoint(startPoint.x, startPoint.y, paint);
    	  }

    	  paint.setStrokeWidth(originalWidth);
    	}
    }
    
    ArrayList<Bezier> bezier = new ArrayList<Bezier>();
    
    
    public float strokeWidth(float velocity) {
    	return 5;
    }

    ArrayList<Point> points = new ArrayList<Point>();
    
    float VELOCITY_FILTER_WEIGHT = 0.5f;
    
    Bitmap bitmap = null;
    Canvas bitmapCanvas = null;
    
    private void addBezier(Bezier curve, float startWidth, float endWidth) {
    	if(bitmap == null) {
    		//bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
    		bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
    		bitmapCanvas = new Canvas(bitmap);
    	}
    	curve.draw(game.getGraphics().getCanvas(), paint, startWidth, endWidth);
    }
    
    

    public void addPoint(Point newPoint) {
      points.add(newPoint);
      Point lastPoint = points.get(points.size() - 1);
      Bezier bezier = new Bezier(lastPoint, newPoint);

      float velocity = newPoint.velocityFrom(lastPoint);

      // A simple lowpass filter to mitigate velocity aberrations.
      velocity = VELOCITY_FILTER_WEIGHT * velocity
          + (1 - VELOCITY_FILTER_WEIGHT) * lastVelocity;

      // The new width is a function of the velocity. Higher velocities
      // correspond to thinner strokes.
      float newWidth = strokeWidth(velocity);

      // The Bezier's width starts out as last curve's final width, and
      // gradually changes to the stroke width just calculated. The new
      // width calculation is based on the velocity between the Bezier's
      // start and end points.
      addBezier(bezier, lastWidth, newWidth);

      lastVelocity = velocity;
      lastWidth = newWidth;
    }

    
    
    */
    
    
    
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
    				tms[(int)Math.ceil(index / 2)] = currentTime;
	    			pts[index++] = currentX;
	    			pts[index++] = currentY;
	    			//points.add(new Point(currentX, currentY, currentTime));
    			}
    			
    		} else if(event.type == TouchEvent.TOUCH_DRAGGED) {
    			draggedX = currentX;
    			draggedY = currentY;  		
    			dragged = true;
    			
    			if(index < pts.length - 2) {
    				tms[(int)Math.ceil(index / 2)] = currentTime;
	    			pts[index++] = currentX;
	    			pts[index++] = currentY;
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