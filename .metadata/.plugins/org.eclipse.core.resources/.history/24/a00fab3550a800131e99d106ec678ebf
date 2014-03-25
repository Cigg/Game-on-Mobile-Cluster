


import java.nio.ByteBuffer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.os.SystemClock;

//********************************************
// Class for easy testing, no production code
//********************************************

public class DeviceView extends View {

	public static enum GLOBAL_STATE {
		ADD_DEVICE,
		MAP_DEVICE,
		RUN_DEVICE
	}
	volatile static GLOBAL_STATE _internalState;
	
	private final static int RADIOUS = 64;
	private final static int DIAMETER = 2*RADIOUS;
	private final static int MAX_DISTANCE = 16384;
	private final static int LIFETIME = 10;
	
	
	private static class Ball {
		
		private static int _ids = 0;

		public static enum BALL_TYPE {
			PLAYER_BALL,
			MAP_BALL
		}
		
		private float _posX, _posY;
		private float _velX, _velY;
		private float  _mass, _radious, _lifeTime;
		private int _color, _id;
		private static final int _maxNColliders = 64;
		private int[] _justCollidedInto;
		private int _nColliders;
		private BALL_TYPE _type;
		
		public Ball(float posX, float posY, float velX, float velY, BALL_TYPE type) {
			_posX = posX;
			_posY = posY;
			_velX = velX;
			_velY = velY;
			_color = Color.GREEN;
			_mass = 1;
			_radious = RADIOUS;
			_lifeTime = 0;
			_type = type;
			
			_justCollidedInto = new int[_maxNColliders];
			_nColliders = 0;
			
			_id =_ids++;
			
			Log.d("AppBalls", "new: " + _id);
		}
		
		public boolean justCollidedWith(int id) {
			for(int i=0; i<_nColliders; i++) {
				if(_justCollidedInto[i] == id) {
					return true;
				}
			}
			return false;
		}
		
		public void addCollider(int id) {
			_justCollidedInto[_nColliders] = id;
			if(_nColliders + 1 < _maxNColliders) {
				_nColliders ++;
			}
		}
		
		public void removeCollider(int id) {
			for(int i=0; i<_nColliders; i++) {
				if(_justCollidedInto[i] == id) {
					_nColliders --;
					for(int j=i; j<_nColliders; j++) {
						_justCollidedInto[j] = _justCollidedInto[j+1];
					}
					break;
				}
			}
		}
	}
	
	private Paint _paint;
	private float _currentX, _currentY;
	
	private float _downX, _downY;
	private float _downTime, _previousTime;
	
	
	private Ball[] _balls = new Ball[2048];
	private static int nBalls = 0;
	
	UDPClient udp;
	static final int nBytes = 1024;

	
	private volatile int _resX;
	private volatile int _resY;
	int _densityDpi;
	int _density;
	private volatile int _xdpi;
	int _ydpi;
	
	
	public DeviceView(Context context)  {
		super(context);
		this.setFocusable(true);
		
		 
		_paint = new Paint();
		_previousTime = 0;
		_internalState = GLOBAL_STATE.ADD_DEVICE;
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);
		
		_resX = displayMetrics.widthPixels;
		_resY = displayMetrics.heightPixels;
		_densityDpi = displayMetrics.densityDpi;
		_density = (int) displayMetrics.density;
		_xdpi = (int) displayMetrics.xdpi;
		_ydpi = (int) displayMetrics.ydpi;
		Log.d("AppDens", "_densityDpi = " + _densityDpi);
		Log.d("AppDens", "_density = " + _density);
		Log.d("AppDens", "_xdpi = " + _xdpi);
		Log.d("AppDens", "_ydpi = " + _ydpi);
		
		udp = new UDPClient();
		
		/*
		for(int i=0 ; i<500 ; i++) {
			//_balls[nBalls] = new Ball((float)Math.random(), (float)Math.random(), (float)Math.random(), (float)Math.random(), Ball.BALL_TYPE.MAP_BALL);
			_balls[nBalls] = new Ball(i, i, 0, 0, Ball.BALL_TYPE.MAP_BALL);
			nBalls++;
		}*/
		
	}
	

	
	@Override
	public  boolean onTouchEvent(MotionEvent event) {
		_currentX = event.getX();
		_currentY = event.getY();
		final float currentTime = System.nanoTime();
		
		Log.d("AppTime", "AppTime currentTime: " + currentTime * Math.pow(10, -9));
		Log.d("AppTime", "AppTime elapsedRealtime(): " + SystemClock.elapsedRealtime() * Math.pow(10, -3));

		
		/*
		Log.d("AppPressure", "Pressure: " + event.getPressure());
		Log.d("AppPressure", "Size: " + event.getSize());
		Log.d("AppPressure", "Pointers: " + event.getPointerCount());
		Log.d("AppPressure", "getRaw: " + event.getRawX() + " " + event.getRawY());
		Log.d("AppPressure", "get: " + event.getX() + " " + event.getY());
		*/
		
		
		final int nPointers = event.getPointerCount();
		if(nPointers == 3) {
			_internalState = GLOBAL_STATE.ADD_DEVICE;
		}
	
		
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			Log.d("Appd", "ACTION_DOWN");
			_downTime = currentTime;
			_downX = _currentX;
			_downY = _currentY;

		} else if(event.getAction() == MotionEvent.ACTION_MOVE) {
			Log.d("Appd", "ACTION_MOVE");
		} else if(event.getAction() == MotionEvent.ACTION_UP) {
			Log.d("Appd", "ACTION_UP");
			
			final float deltaTime = currentTime - _downTime;
			//_balls.add(new Ball(_currentX, _currentY, (_currentX-_downX)/deltaTime, (_currentY-_downY)/deltaTime));

			
			// Add if no ball at this position
	
			int counter;
			for(counter=0; counter<nBalls; counter++) {
				final float X = Math.abs(_balls[counter]._posX - _currentX);
				final float Y = Math.abs(_balls[counter]._posY - _currentY);
				final float XYLen =  (float) Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2));
				if (XYLen < DIAMETER) {
					// There is collision, don't add ball
					break;
				}
			}
			
			if(counter == nBalls) {
				_balls[nBalls] = new Ball(_currentX, _currentY, (_currentX-_downX)/deltaTime, (_currentY-_downY)/deltaTime, Ball.BALL_TYPE.PLAYER_BALL);
				nBalls ++;
			}
    		
			
			// ------------------------------------------------------------------------

			ByteBuffer buffer; 
			
			switch(_internalState) {
				case ADD_DEVICE:
					
					buffer = ByteBuffer.allocate(2*2 + 4*4);
		    		buffer.clear();
		    		
		    		buffer.putShort((short) 0);		// State: ADD_DEVICE
		    		buffer.putShort((short) 1);		// Id

		    		buffer.putInt(_xdpi);			// XDPI
		    		buffer.putInt(_ydpi);			// YDPI
		    		buffer.putInt(_resX); 			// ResX
		    		buffer.putInt(_resY);			// ResY
		    		
		    		udp.sendData(buffer.array());
		    		
					_internalState = GLOBAL_STATE.MAP_DEVICE;
				break;
				
				
				case MAP_DEVICE:
					
					buffer = ByteBuffer.allocate(2*2 + 5*4);
		    		buffer.clear();
					    		
		    		buffer.putShort((short) 1);		// State: MAP_DEVICE
		    		buffer.putShort((short) 1);		// Id
		    		
		    		buffer.putFloat(_downX);		// x1
		    		buffer.putFloat(_downY); 		// y1
		    		buffer.putFloat(_currentX);		// x2
		    		buffer.putFloat(_currentY);		// y2
		    		buffer.putFloat(deltaTime);     // t
		    		
		    		udp.sendData(buffer.array());
		    		
		    		byte[] arr = udp.getData();
		    		
		    		if(arr != null) {
		    			Log.d("AppClient","arr: !null");
		    			String fromServer = new String(arr);
		    			Log.d("AppClient","From server: '" + fromServer + "'");
		    		} else Log.d("AppClient","arr: null");
		    		
					//_internalState = GLOBAL_STATE.RUN_DEVICE;
				break;
				
				case RUN_DEVICE:
					Log.d("AppStates", "RUN_DEVICE");
				break;
				
				
				default:
				break;
			}
			
			// ------------------------------------------------------------------------
			
			
		}
		
		return true;
	}
	
	



	@Override
	protected void onDraw(Canvas canvas) {
		//Log.d("AppOnDraw", "ONDRAW");
		super.onDraw(canvas);
		
		_paint.setColor(Color.BLUE);
		canvas.drawCircle((int)_currentX, (int)_currentY, RADIOUS, _paint);
		canvas.drawLine((int)_downX, (int)_downY, (int)_currentX, (int)_currentY, _paint);
		
		final float currentTime = System.nanoTime();
		final float deltaTime = currentTime - _previousTime;
		
		Log.d("AppFPS", "FPS: " + Math.pow(10,9) / deltaTime);
		
	
		// Update positions 
		int ballCounter = 0;
		for(int i=0; i<nBalls; i++) {
			
			switch(_balls[i]._type) {
				
				case PLAYER_BALL:
					_balls[i]._lifeTime += deltaTime;
					if(_balls[i]._lifeTime * Math.pow(10, -9) < LIFETIME) {
						// Still alive
						_balls[i]._posX += _balls[i]._velX * deltaTime;
						_balls[i]._posY += _balls[i]._velY * deltaTime;
						if(Math.abs(_balls[i]._posX) < MAX_DISTANCE && Math.abs(_balls[i]._posY) < MAX_DISTANCE) {
							// Not out of bounds
							_balls[ballCounter] = _balls[i];
							ballCounter++;			    		
						}
					}
					
				break;
				
				case MAP_BALL:
					_balls[i]._lifeTime += deltaTime;
					_balls[i]._posX += _balls[i]._velX * deltaTime;
					_balls[i]._posY += _balls[i]._velY * deltaTime;
					_balls[ballCounter] = _balls[i];
					ballCounter++;
				break;
				
				default:
				break;
			}
    		
		}
		nBalls = ballCounter;
		
		

		
		// Collision detection
		
		// Check every object with all the other objects only once
		for(int i=0; i<nBalls; i++) {
			for(int j=i+1; j<nBalls; j++) {
				
				// Check if they are close to each other (Bounding boxes), this in order to optimize
				if(Math.abs(_balls[i]._posX - _balls[j]._posX) <= DIAMETER && Math.abs(_balls[i]._posY - _balls[j]._posY) <= DIAMETER) {
									
					_balls[i]._color = Color.YELLOW;
					_balls[j]._color = Color.YELLOW;
					
					final float X = Math.abs(_balls[i]._posX - _balls[j]._posX);
					final float Y = Math.abs(_balls[i]._posY - _balls[j]._posY);
					final float XYLen =  (float) Math.sqrt(Math.pow(X, 2) + Math.pow(Y, 2));
					
					if (XYLen <= DIAMETER) {
						// There is a collision here
						
						// Make sure these objects don't stick together in a buggy way
						if(!_balls[i].justCollidedWith(_balls[j]._id)) {
							
							_balls[i]._color = Color.RED;
							_balls[j]._color = Color.RED;
							
							// Center vector, vector between balls
							final float centerX = _balls[i]._posX - _balls[j]._posX;
							final float centerY = _balls[i]._posY - _balls[j]._posY;
							
							// Normalize center vector
							final float centerLength =  (float) Math.sqrt(Math.pow(centerX, 2) + Math.pow(centerY, 2));
							final float centerXNormX = centerX / centerLength;
							final float centerXNormY = centerY / centerLength;
		
							// Compute length of velocity tangents
							final float a1Length = _balls[i]._velX * centerXNormX + _balls[i]._velY * centerXNormY;
							final float a2Length = _balls[j]._velX * centerXNormX + _balls[j]._velY * centerXNormY;
		
							// Get mass 
							final float m1 = _balls[i]._mass;
							final float m2 = _balls[j]._mass;
							
							// Compute mutual expression 
							final float optimizedExpression = (float)(2 * (a1Length - a2Length)) / (m1 + m2);
							
							// Set new velocity
							_balls[i]._velX += - optimizedExpression * m2 * centerXNormX;
						    _balls[i]._velY += - optimizedExpression * m2 * centerXNormY;
						    
						    _balls[j]._velX += optimizedExpression * m1 * centerXNormX;
						    _balls[j]._velY += optimizedExpression * m1 * centerXNormY;	
						    
						    // TODO:
						    // Update positions, move the balls back from each other, 
						    // so that the code for Colliders in class Ball no longer is needed
							
							_balls[i].addCollider(_balls[j]._id);
							_balls[j].addCollider(_balls[i]._id);
						}
					 
					} else {
						_balls[i].removeCollider(_balls[j]._id);
						_balls[j].removeCollider(_balls[i]._id);
					}
					
				}
			}
			_paint.setColor(_balls[i]._color);
			canvas.drawCircle((int)_balls[i]._posX, (int)_balls[i]._posY, RADIOUS, _paint);
		}
		
		_previousTime = currentTime;
		invalidate(); // In order to have this function called again
	}
}


