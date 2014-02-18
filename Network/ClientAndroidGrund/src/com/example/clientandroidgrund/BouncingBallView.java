package com.example.clientandroidgrund;

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
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			//System.out.println(currentX + " " + currentY);
			if(tcpClient != null) {
				tcpClient.sendMessage(currentX + " " + currentY);
			}
		}
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
		ballX = currentX;
		ballY = currentY;
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
				}
			});
			tcpClient.run();
			return null;
		}
	}

	
}//End of BouncingBallView