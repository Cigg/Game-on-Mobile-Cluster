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
import com.pussycat.minions.GameScreenMiddle.GameState;


public class GameScreenPlayer extends Screen {
    enum GameState {
        //Ready, Running, Paused, GameOver, AddDevice, NotMapped, Wait, MappedWait, Remap, //, Mapped
    	Mapping, MappingDone, Ready, Running, Paused, GameOver, //, Mapped    
    }

    private GameState state = GameState.Mapping; //We don't need to start at GameState.Ready now. Changed to Running instead.

    private Paint paint;
    private Paint paint2;
    
    Context context;
    
    Button readyButton;
    
	private float currentX, currentY;
	private float downX, downY;

	private float draggedX, draggedY;
	private boolean dragged = false;
	
	private float downTime, previousTime;
	
	private float freezeStart;
	private final float FREEZE_DURATION = (float) (0.2 * Math.pow(10, 9));
	
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
	public boolean drawTraceAfter = false;

	
	private AnimationHandler animationHandler = AnimationHandler.getInstance();	
	
	private TCPClient comm;
	private BallHandler ballHandler;
	private BallsWidget ballsWidget;
	private PointsWidget pointsWidget;
	private TimerWidget timerWidget;
	private PointsNotificationWidget pointsNotificationsWidget;
	private RemapWidget remapWidget;
	private CountDownWidget countDownWidget;
	
	private ArrayList<Widget> widgets = new ArrayList<Widget>();

	
    public GameScreenPlayer(Game game) {
        super(game);        
        
        Log.d("BROWSE", "AT GAMESCREEN");
        
		comm = new TCPClient();
		comm.start();

		ballHandler = new BallHandler(PussycatMinions.getScreenWidth(), PussycatMinions.getScreenHeight());
		ServerCommunication serverComm = new ServerCommunication(comm, ballHandler, null);
		serverComm.start();
		
		
		paint2 = new Paint();
		paint = new Paint();
		
		paint.setTextSize(40);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);

		readyButton = new Button(Assets.button, Assets.button_pressed, PussycatMinions.getScreenWidth(), PussycatMinions.getScreenHeight()/2-100, paint);
        readyButton.setText("READY");
		
		previousTime = 0;		
		SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.MAP_DEVICE);		

	
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
    
    public void setIsReady() {
		ByteBuffer buffer;
		buffer = ByteBuffer.allocate(1*2 + 1*4);
		buffer.putShort((short) GLOBAL_STATE__.IS_READY.ordinal());	// State: IS_READY
		buffer.putInt(1);
		comm.sendData(buffer.array());
		
		SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.REG);
		//state = GameState.Ready;
    }
    
    public boolean isFrozen() {
    	return (freezeStart + FREEZE_DURATION - System.nanoTime()) > 0; 
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
    	
        /*
        if(SharedVariables.getInstance().getInternalState() == GLOBAL_STATE__.IS_READY) {
       	 	setIsReady();
        }
        */
        
        if(state != GameState.Running) {
        	updateReady(touchEvents);
        }

    	if(SharedVariables.getInstance().shouldStartGame()) {
    		SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.REG);
    		SharedVariables.getInstance().setIsRemapping(false);
    		
    		SharedVariables.getInstance().setStartGame(false);
        	ballsWidget = new BallsWidget();
        	pointsNotificationsWidget = new PointsNotificationWidget(game.getAudio());
    		pointsWidget = new PointsWidget(pointsNotificationsWidget);
    		timerWidget = new TimerWidget();
    		remapWidget = new RemapWidget();
    		countDownWidget = new CountDownWidget(timerWidget);
    		
    		widgets.add(ballsWidget);
    		widgets.add(pointsWidget);
    		widgets.add(timerWidget);
    		widgets.add(pointsNotificationsWidget);	
    		widgets.add(remapWidget);
    		widgets.add(countDownWidget);
			
    		syncDevice();    
    		
    		state = GameState.Running;
    	}

    	ballHandler.updateBalls(deltaTime);
    	ballHandler.removeBallsNotWanted();
    	
    	for(Widget widget : widgets) {
    		widget.update();
    	}
    	
    	if(animationHandler != null) {
    		animationHandler.updateAnimations(System.nanoTime());
    	}
    	
    	up = false;
    	
        for (int i = 0; i < touchEvents.size(); i++) {
            TouchEvent event = touchEvents.get(i);
            
        	currentX = event.x;
    		currentY = event.y;
    		final float currentTime = event.time;
    		
    		if( !isFrozen() ) {
    		  if(event.pointer >= 2) {
     			remapDevice();
     			freezeStart = System.nanoTime();
    		  } else if(event.type == TouchEvent.TOUCH_DRAGGED) {
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
    				//	state = GameState.MappingDone;
    					Log.d("STATEZ", "PLAYER MAP_DEVICE");
    					mapDevice(deltaTimeDragged, currentTime);
    					SharedVariables.getInstance().setIsRemapping(false);
    					if(SharedVariables.getInstance().isRunning()) {
    						state = GameState.Running;
							SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.RUN_DEVICE);
						} else {
							setIsReady();
							remapDevice();
							//SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.IS_READY);
    					} 
    				}
    				break;
    				
    				case IS_READY:
    				{
    				}
    				break;

					case REG: {
						Log.d("STATEZ", "PLAYER REG");
    				}
    				break;
    				
					case START_GAME: {
					
					}
    				break;
    				
    				case RUN_DEVICE:
    				{
    					Log.d("STATEZ", "PLAYER RUN_DEVICE");
    					if( ballsWidget.pop() ) {
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
		buffer.putShort((short) 1);										// type, 0 är hårdkodat till main-device - sätt 1 för alla andra devices
		buffer.putInt(PussycatMinions.getXDPI());						// XDPI
		buffer.putInt(PussycatMinions.getYDPI());						// YDPI
		buffer.putInt(PussycatMinions.getScreenWidth());				// ResX
		buffer.putInt(PussycatMinions.getScreenHeight());				// ResY
		
 		comm.sendData(buffer.array());
    }
    
    private void mapDevice(final float deltaTimeDragged, final float currentTime) {
    	ballHandler.removeAllBalls();
    	
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
    
    private void remapDevice() {    	
		ByteBuffer buffer = ByteBuffer.allocate(2*2);
		
		buffer.putShort((short) GLOBAL_STATE__.SET_STATE.ordinal());	// State: SET_STATE
		buffer.putShort((short) GLOBAL_STATE__.MAP_MAIN.ordinal());		// New state: MAP_MAIN
		
		comm.sendData(buffer.array());
		
		SharedVariables.getInstance().setIsRemapping(true);
		SharedVariables.getInstance().setMapDone(false);
		SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.MAP_DEVICE);
    }
    
//    private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {   
//    	
//    	
//                
//    }
//
//    
//    private void updatePaused(List<TouchEvent> touchEvents) {
//        int len = touchEvents.size();
//        for (int i = 0; i < len; i++) {
//            TouchEvent event = touchEvents.get(i);
//            if (event.type == TouchEvent.TOUCH_UP) {
//
//            }
//        }
//    }

//    
//    private void updateGameOver(List<TouchEvent> touchEvents) {
//        int len = touchEvents.size();
//        for (int i = 0; i < len; i++) {
//            TouchEvent event = touchEvents.get(i);
//            if (event.type == TouchEvent.TOUCH_UP) {
//                if (event.x > 300 && event.x < 980 && event.y > 100 && event.y < 500) {
//                    nullify();
//                    game.setScreen(new MainMenuScreen(game));
//                    return;
//                }
//            }
//        }
//
//    }

    
    private float linearInterpolation(float x1, float x2, float scale) {
		return x1 * (1 - scale) + x2 * scale;
	}
	
    
    private float cosineInterpolation(float x1, float x2, float scale) {
		float scaleModified = (float) ((1 - Math.cos(scale * Math.PI)) / 2);
		return x1 * (1 - scaleModified) + x2 * scaleModified;
	}
	
    
	private float cubicInterpolation(float x0, float x1, float x2, float x3, float scale) {
		float a0, a1, a2, a3, scale2;
		scale2 = scale * scale;
		a0 = x3 - x2 - x0 + x1;
		a1 = x0 - x1 - a0;
		a2 = x2 - x0;
		a3 = x1;
		return (a0*scale*scale2 + a1*scale2 + a2*scale + a3); 
	}
	

	
	
	// Fï¿½r trace/trigger - ska fixa fint
	// TODO: ska fixa fint och stï¿½da
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
	
 	public int lastAlpha = 255;
 	
 	Device device = new Device();
 	
 	
    @Override
    public void paint(float deltaTime) {
    	
        Graphics graphics = game.getGraphics();
        Canvas canvas = graphics.getCanvas();
        
        //graphics.drawImage(Assets.background, 0, 0);
       // graphics.drawRect(0, 0, PussycatMinions.getScreenWidth(),  PussycatMinions.getScreenHeight(), Color.BLACK);
       
       // device.drawBackground(graphics);
       
       /*
        switch (SharedVariables.getInstance().getInternalState()) {
        	case REG:
        		device.drawBackground(graphics);
        	break;
        	
        	default:
        		graphics.drawImage(Assets.background, 0, 0);
        	break;
        }
        */
    	//.drawBackground(graphics);
       //graphics.drawImage(Assets.background, 0, 0);
        device.drawBackground(graphics);
       
        
        
        
        // TODO: OPTIMERA!!
        boolean tail = true;
        if( dragged && tail) {

  			paint2.setColor(Color.YELLOW);
               
        	int jump = 4;
        	for(int i = beginIndex ; i<index ; i += jump) {
        		
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
	        			
	        			scale = j / (float) steps;  // Kan rï¿½knas ut i fï¿½rvï¿½g
	            	    ix = cubicInterpolation(xk, xz, x1, x2, scale);
	            	    iy = cubicInterpolation(yk, yz, y1, y2, scale);
	            	    
	        			//float iv = cubicInterpolation(vk, vz, v1, v2, scale);
	        			
	        			//float ix = cosineInterpolation(x1, x2, scale);
	        			//float iy = cosineInterpolation(y1, y2, scale);
	            	    iv = linearInterpolation(v1, v2, scale);
	        			
	        			//float iv = cosineInterpolation(v1, v2, scale);
	            	    
	            	    // Gï¿½r att fï¿½renkla
	            	    iv = (float) (iv * Math.pow(10, 6));
	            	    iv = Math.max(iv, 1);
	            	    iv = Math.max(PussycatMinions.meters2Pixels( 0.05f / 100.0f ), PussycatMinions.meters2Pixels(25 / (iv*20000.0f)));
	            	    
	            	    paint2.setStrokeWidth( iv ) ;
	      
	            	    bitmapCanvas.drawPoint(ix, iy, paint2);
	  	            	    
	        		}
	        		
	 
	        		
	        		beginIndex = i + jump;
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
        	
        	
	        
        }
        
        
       
    	//Log.d("deltaTime", "deltaTime = " + deltaTime * Math.pow(10, -7));
    	
    	
    	if( drawTraceAfter ) {
    		//lastAlpha = Math.max(0, lastAlpha - (int)(0.5 * deltaTime * Math.pow(10, -6)));
        	//Log.d("NANO", "System.nanoTime() - tms[index] = " + (System.nanoTime() - tms[index]));
        	
        	//Log.d("NANO","index = " + index);
        	
        	float timeElapsed = (float) ((System.nanoTime() - tms[Math.max(0, index-1)]) *  Math.pow(10, -9));
        	//Log.d("timeElapsed", "timeElapsed = " + timeElapsed);
        	float seconds = 1.0f;
        	
        	int alpha = Math.max(0, (int) cosineInterpolation(255, 0 , (float) (timeElapsed / seconds )));
        		        	
        	paint2.setAlpha( alpha );
        	
        	if( alpha == 0 ) {
        		drawTraceAfter = false;
        	}
    	}
    	
    	if (bitmap == null) {
		    bitmap = Bitmap.createBitmap(PussycatMinions.getScreenWidth(), PussycatMinions.getScreenHeight(), Bitmap.Config.ARGB_8888);
		    bitmapCanvas = new Canvas(bitmap);
		}
    	
	
        canvas.drawBitmap(bitmap, 0, 0, paint2);
        
        int beginIndexBefore = beginIndex;
    	float x1b = x1;
    	float y1b = y1;
    	float t1b = t1;
    	float v1b = v1;

    	

    	float xkb = xk;
    	float ykb = yk;
    	
    	float xzb = xz;
    	float yzb = yz;
    	
    	float vkb = vk;
    	float vzb = vz;
     	
     	boolean tail2 = true;
        if( (dragged || up ) && tail2 && beginIndex > 7) {
        	
        	beginIndex = beginIndex - 2 ;

        	paint2.setColor(Color.YELLOW);
               
        	int jump = 1;
        	for(int i = beginIndex ; i<index ; i += jump) {
        		
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
	        			
	        			scale = j / (float) steps;  // Kan rï¿½knas ut i fï¿½rvï¿½g
	            	    ix = cubicInterpolation(xk, xz, x1, x2, scale);
	            	    iy = cubicInterpolation(yk, yz, y1, y2, scale);
	            	    
	        			//float iv = cubicInterpolation(vk, vz, v1, v2, scale);
	        			
	        			//float ix = cosineInterpolation(x1, x2, scale);
	        			//float iy = cosineInterpolation(y1, y2, scale);
	            	    iv = linearInterpolation(v1, v2, scale);
	        			
	        			//float iv = cosineInterpolation(v1, v2, scale);
	            	    
	            	    // Gï¿½r att fï¿½renkla
	            	    iv = (float) (iv * Math.pow(10, 6));
	            	    iv = Math.max(iv, 1);
	            	   // iv = Math.max(3, 25 / iv);
	            	    iv = Math.max(PussycatMinions.meters2Pixels( 0.05f / 100.0f ), PussycatMinions.meters2Pixels(25 / (iv*20000.0f)));
	            	    
	            	    paint2.setStrokeWidth( iv ) ;
	            	    	
	            	    if(up) {
	            	    	bitmapCanvas.drawPoint(ix, iy, paint2);
	            	    } else {
	            	    	canvas.drawPoint(ix, iy, paint2);
	            	    }
	        		}	
	 
	        		//beginIndex = i + jump;
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
        	 
        }
        
        beginIndex = beginIndexBefore;
        x1 = x1b;
        y1 = y1b;
        t1 = t1b;
        v1 = v1b;

        xk = xkb;
        yk = ykb;
    	
        xz = xzb;
        yz = yzb;
    	
        vk = vkb;
        vz = vzb;
    	
        if( dragged ) {
        	graphics.drawScaledImage(	Assets.ball, 
				 	(int)(draggedX - PussycatMinions.meters2Pixels(0.0075f)), 
				 	(int)(draggedY - PussycatMinions.meters2Pixels(0.0075f)), 
				 	(int)(PussycatMinions.meters2Pixels(0.0075f*2)), 
				 	(int)(PussycatMinions.meters2Pixels(0.0075f*2)), 
				 	0, 
				 	0, 
				 	Assets.ball.getWidth(), 
				 	Assets.ball.getHeight(),
				 	0.0f	);
        }
        
        
       
        
        ballHandler.drawBalls(graphics);
        
        for(Widget widget : widgets) {
    		widget.draw(graphics);
    	}
        
        //ballsWidget.draw(graphics);
       // pointsWidget.draw(graphics);
       // timerWidget.draw(graphics);
       // pointsNotificationsWidget.draw(graphics);
   
        drawUI();
        
    }
    
    private void drawUI() {
    	
    	Log.d("UI STATEZ", "STATE: " + state);
    	Graphics g = game.getGraphics();    	    	
    	
    	int textX = PussycatMinions.getScreenWidth()/2;
    	int textY = PussycatMinions.getScreenHeight()/2;
    	
    	paint.setColor(Color.WHITE);
    	
//    	//AddDevice, NotMapped, Mapped, Wait, MappedWait, Remap, 
//    	if (state == GameState.AddDevice) {
//    		Log.d("UI STATEZ", "AddDevice");
//    		g.drawImage(Assets.ball, imageX, imageY);
//    		g.drawString("Klicka fï¿½r att gï¿½ med i spelet", textX, textY, paint);
//    	} else if (state == GameState.NotMapped) {
//    		Log.d("UI STATEZ", "NotMapped");
//    		g.drawImage(Assets.ball, imageX, imageY);
//    		g.drawString("Lyckost! Det ï¿½r din tur att mappa nu.", textX, textY, paint);
//    	} else if (state == GameState.Wait) {
//    		Log.d("UI STATEZ", "Wait");
//    		g.drawImage(Assets.ball, imageX, imageY);
//    		g.drawString("Vï¿½nta! Nï¿½gon annan mappar.", textX, textY, paint);
//    	} else if (state == GameState.MappedWait) {
//    		Log.d("UI STATEZ", "MappedWait");
//    		g.drawImage(Assets.ball, imageX, imageY);
//    		g.drawString("Vï¿½nta medans de andra mappar sina enheter", textX, textY, paint);
//    	} else if (state == GameState.Remap) {
//    		Log.d("UI STATEZ", "Remap");
//    		g.drawImage(Assets.ball, imageX, imageY);
//    		g.drawString("Klicka om du vill mappa om din enhet", textX, textY, paint);
//    	}
    	readyButton.drawButton(g); // TODO DEL
    	//Mapping, MappingDone, Ready, Running, 
    	if (state == GameState.Mapping) {
    		Log.d("UI STATEZ", "Mapping");
    		g.drawARGB(155, 0, 0, 0);
    		readyButton.drawButton(g);
    		g.drawString("...to this device", textX, textY, paint);
    	} else if (state == GameState.MappingDone) {
    		Log.d("UI STATEZ", "MappingDone");
    		g.drawARGB(155, 0, 0, 0);
    		readyButton.drawButton(g);
    		g.drawString("Tap when you are ready", textX, textY, paint);
    	} else if (state == GameState.Ready) {
    		Log.d("UI STATEZ", "Ready! Waiting...");
    		g.drawARGB(155, 0, 0, 0);
    		readyButton.drawButton(g);
    		//g.drawString("Ready!", textX, textY, paint);
    	} else if (state == GameState.Running) {
    		Log.d("UI STATEZ", "Running");
    	}
    }

//    private void drawReadyUI() {
//        Graphics g = game.getGraphics();
//        
//        g.drawARGB(155, 0, 0, 0);
//        g.drawString("Tap to create a ball", 640, 300, paint);
//    }
//
//    
//    private void drawRunningUI() {
//        Graphics g = game.getGraphics();
//    }
//
//    
//    private void drawPausedUI() {
//        Graphics g = game.getGraphics();
//        // Darken the entire screen so you can display the Paused screen.
//        g.drawARGB(155, 0, 0, 0);
//    }
//
//    
//    private void drawGameOverUI() {
//        Graphics g = game.getGraphics();
//        g.drawRect(0, 0, 1281, 801, Color.BLACK);
//        g.drawString("GAME OVER.", 640, 300, paint);
//    }
    

    private void updateReady(List<TouchEvent> touchEvents) {
   	 Graphics g = game.getGraphics();
   	 
   	 int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_DOWN){
   				if(readyButton.inBounds(event.x, event.y)){
   					readyButton.setPressed(true);
   				}
   			}
   			
   			if(event.type == TouchEvent.TOUCH_DRAGGED){
   				if(!readyButton.inBounds(event.x, event.y)){
   					readyButton.setPressed(false);
   				}
   			}
   			
   			if (event.type == TouchEvent.TOUCH_UP) {
   				
   				readyButton.setPressed(false);
   			
   			//TODO: Should go to SetupScreen instead
   			if(readyButton.inBounds(event.x, event.y)){
   				//game.setScreen(new GameScreenPlayer(game));
   				SharedVariables.getInstance().setInternalState(GLOBAL_STATE__.IS_READY);
   			
   			}
   		}
        }
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