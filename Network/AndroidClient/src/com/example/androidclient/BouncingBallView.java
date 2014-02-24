package com.example.androidclient;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.view.MotionEvent;
import android.view.View;

public class BouncingBallView  extends View{
	private int xMin = 0;
	private int yMin = 0;
	private int xMax;
	private int yMax;
	
	private float ballRadius = 80;
	private float ballX = ballRadius + 20; 
	private float ballY = ballRadius + 40;
	
	private RectF ballBounds;
	private Paint paint;
	
	private float previousX;
	private float previousY;
	private float deltaX;
	private float deltaY;
	
	private float currentX;
	private float currentY;
	
	
	private Ball ball;
	private String data;
	private TCPClient tcpClient;
	
	private int ballCount;
	
	private class Ball {
		public int id;
		public float posX;
		public float posY;
		public float speedX;
		public float speedY;
		
		public Ball(int id, float posX, float posY){
			this.id = id;
			this.posX = posX;
			this.posY = posY;
		}
		
		public void update(float posX, float posY){
			this.posX = posX;
			this.posY = posY;
		}
	}
	
	private Ball[] balls;
	
	public BouncingBallView(Context context) {
		super(context);
		ballBounds = new RectF();
		paint = new Paint();
		this.setFocusable(true);
		new connectTask().execute("");
		ballCount = 0;
		//balls = new Ball[10];
		// LayoutParams circleParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
	    //            LayoutParams.WRAP_CONTENT);
		//ball = new BouncingBall(this.getContext());
		//ball.setLayoutParams(circleParams);
		//ball.setId(0);
		//this.addView(ball,0);
		//center = new Point();
	}
	
	public  boolean onTouchEvent(MotionEvent event) {
		currentX = event.getX();
		currentY = event.getY();

		if(event.getAction() == MotionEvent.ACTION_DOWN){
			ballCount++;
			System.out.println(ballCount);
			ball = new Ball(ballCount,currentX, currentY);
		}
		
		if(event.getAction() == MotionEvent.ACTION_MOVE) {
			deltaX = currentX - previousX;
			deltaY = currentY - previousY;
			if(tcpClient != null) {
				tcpClient.sendMessage(currentX + " " + currentY + " " + 0 + " " + 0);
			}
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP) {
			tcpClient.sendMessage(currentX + " " + currentY + " " + deltaX + " " + deltaY);
		}
		previousX = currentX;
		previousY = currentY;
		return true;
	}
	

	
	@Override
	protected void onDraw (Canvas canvas) {
		paint.setColor(Color.GREEN);
		//if(ball != null){
		//ball.draw();
		//if(ball != null)
		if( ballCount == 0){
			ballBounds.set(0,0,0,0);
			paint.setColor(Color.WHITE);
			canvas.drawOval(ballBounds,paint);
		//}
		//System.out.println(ballCount);
		} else {
		
				ballBounds.set( ball.posX-ballRadius, ball.posY-ballRadius,
								ball.posX+ballRadius, ball.posY+ballRadius);
				canvas.drawOval(ballBounds,paint);

		}
		update();
		invalidate();
	}
	
	private void update() {
		tcpClient.getData();
		if(data != null && ball != null){
			String[] parts = data.split(" ");
			//ballX = Float.parseFloat(parts[0]);
			//ballY = Float.parseFloat(parts[1]);
			ball.posX = Float.parseFloat(parts[0]);
			ball.posY = Float.parseFloat(parts[1]);
		}
	}
	
	public float sendX(){
		return ballX;
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
	
    /*@Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int measuredWidth = MeasureSpec.getSize(widthSpec);
        int measuredHeight = MeasureSpec.getSize(heightSpec);
        size = Math.min(measuredWidth, measuredHeight);
        center.x = measuredWidth / 2;
        center.y = measuredWidth / 2;

        int childSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        // left.measure(widthMeasureSpec, heightMeasureSpec);
        // left.measure(25, 25);
        // middle.measure(25, 25);
        // right.measure(25, 25);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }*/
	/*
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.ball.layout((int) Math.floor(ballX), (int) Math.floor(ballX)
        				,(int) Math.ceil(ballX+ballRadius), (int) Math.ceil(ballX+ballRadius));
    }*/

	
}//End of BouncingBallView
