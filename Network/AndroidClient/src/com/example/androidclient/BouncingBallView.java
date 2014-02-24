package com.example.androidclient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;

public class BouncingBallView  extends View {
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
	
	private float currentX;
	private float currentY;
	
	private String data;
	
	private TCPClient tcpClient;
	
	public BouncingBallView(Context context) {
		super(context);
		ballBounds = new RectF();
		paint = new Paint();
		this.setFocusableInTouchMode(true);
		new connectTask().execute("");
	}
	
	@Override
	public  boolean onTouchEvent(MotionEvent event) {
		currentX = event.getX();
		currentY = event.getY();
		if(event.getAction() == MotionEvent.ACTION_MOVE) {
			deltaX = currentX - previousX;
			deltaY = currentY - previousY;
			if(tcpClient != null) {
				//System.out.println(currentX + " " + currentY);
				tcpClient.sendMessage(currentX + " " + currentY + " " + deltaX + " " + deltaY);
			}
		}
		previousX = currentX;
		previousY = currentY;
		return true;
	}
	
	@Override
	protected void onDraw (Canvas canvas) {
		ballBounds.set( ballX-ballRadius, ballY-ballRadius,
						ballX+ballRadius, ballY+ballRadius);
		paint.setColor(Color.GREEN);
		canvas.drawOval(ballBounds,paint);
		
		update();
		invalidate();
	}
	
	private void update() {
		tcpClient.getData();
		if(data != null){
			String[] parts = data.split(" ");
			ballX = Float.parseFloat(parts[0]);
			ballY = Float.parseFloat(parts[1]);
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
			//System.out.println("Data is: " + data);
		}
	}

	
}//End of BouncingBallView