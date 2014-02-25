package com.example.androidclient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class SetDeviceCoordinates  extends View {
	private int xMin = 0;
	private int yMin = 0;
	private int xMax;
	private int yMax;
	
	private float ballRadius = 80;
	private float ballX = ballRadius + 20; 
	private float ballY = ballRadius + 40;
	private float ballSpeedX = 0;
	private float ballSpeedY = 0;
	
	private RectF ballBounds;
	private Paint paint;
	
	private float previousX;
	private float previousY;
	private float deltaX;
	private float deltaY;
	
	private float timeStart;
	private float timeStop;
	
	private float downX;
	private float downY;
	private float upX;
	private float upY;
	
	private String data;
	
	private TCPClient tcpClient;
	
	public SetDeviceCoordinates(Context context) {
		super(context);
		ballBounds = new RectF();
		paint = new Paint();
		this.setFocusableInTouchMode(true);
		new connectTask().execute("");
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);
		int width = displayMetrics.widthPixels;
		int height = displayMetrics.heightPixels;
		int densityDpi = displayMetrics.densityDpi;
		int density = (int) displayMetrics.density;
		int xdpi = (int) displayMetrics.xdpi;
		int ydpi = (int) displayMetrics.ydpi;
				
		Log.d("PussycatAndroidClient", "Height: " + height + "\nWidth: "+ width + "\nDpi: "+ densityDpi + "\nDensty: "+ density + "\nxdpi: "+ xdpi + "\nydpi: "+ ydpi);
		
		if(tcpClient != null) {
			// 0 = ADD_DEVICE on the server 
			tcpClient.sendMessage(0 + " " + xdpi + " " + width + " " + height);
		}		
	}
	
	@Override
	public  boolean onTouchEvent(MotionEvent event) {
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			//System.out.println(currentX + " " + currentY);
			upX = event.getX();
			upY = event.getY();
			timeStop = SystemClock.uptimeMillis();
			
			float deltaT = timeStop - timeStart;
			if(tcpClient != null) {
				// 1 = MAP_DEVICE on the server 
				tcpClient.sendMessage(1 + " " + downX + " " + downY + " " + upX + " " + upY + " " + deltaT);
			}
			break;
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			downY = event.getY();
			timeStart = SystemClock.uptimeMillis();
			break;
		}		
		
		return true;
	}
	
	/**
	 * Draws how the devices are positioned compared to each other.
	 * The main device (tablet) is drawn with one corner in (0,0) while
	 * the other devices are translated and rotated based on global coordinates. 
	 * @param canvas
	 */
	@Override
	public void onDraw(Canvas canvas)
	{
		if(data != null){
			// x0, y0, height, width, alpha
			String[] parts = data.split(" ");
			
			// For the first test we only need two screens.  
			// parts must contain 10 arguments = two devices for this to work
			int SCREENS = 2;
			
			paint.setColor(Color.BLUE);
			for(int i = 0; i < (parts.length)/SCREENS; i += 5 ){
						
				float X1 = Float.parseFloat(parts[i]);
				float Y1 = Float.parseFloat(parts[i + 1]);
				float X2 = Float.parseFloat(parts[i] + parts[i + 2]);
				float Y2 = Float.parseFloat(parts[i] + parts[i + 3]);			
				float alpha = Float.parseFloat(parts[i + 4]);
				
				canvas.rotate(alpha);
				canvas.drawRect(X1, Y1, X2, Y2, paint);
				canvas.restore();			
			}
			
			//update();
			invalidate();			
		}
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldW, int oldH) {
		xMax = w-1;
		yMax = h-1;
	}
	
	
	public class connectTask extends AsyncTask<String,String,TCPClient> {
		@Override
		protected TCPClient doInBackground(String... message) {
			tcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
				@Override
				public void messageReceived(String message) {
					// TODO Auto-generated method stub
					publishProgress(message);
				}
			});
			tcpClient.run();
			return null;
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			data = values[0];
			System.out.println("Data is: " + data);
		}
	}

	
}//End of BouncingBallView
