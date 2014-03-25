package com.example.androidclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

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
	}
	
	private Ball[] balls;
	
	public BouncingBallView(Context context) {
		super(context);
		ballBounds = new RectF();
		paint = new Paint();
		
		this.setFocusable(true);
		new connectTask().execute("");
		
		ballCount = 0;
		balls = new Ball[10];
	}
	
	public  boolean onTouchEvent(MotionEvent event) {
		currentX = event.getX();
		currentY = event.getY();

		if(event.getAction() == MotionEvent.ACTION_DOWN){
			ballCount++;
			balls[ballCount-1] = new Ball(ballCount,currentX, currentY);
			tcpClient.sendMessage(ballCount + " " + currentX + " " + currentY + " " + 0 + " " + 0);
		}
		
		if(event.getAction() == MotionEvent.ACTION_MOVE) {
			deltaX = currentX - previousX;
			deltaY = currentY - previousY;
			if(tcpClient != null) {
				tcpClient.sendMessage(ballCount + " " + currentX + " " + currentY + " " + 0 + " " + 0);
				//System.out.println(ballCount + currentX + " " + currentY + " " + 0 + " " + 0);
			}
		}
		
		if(event.getAction() == MotionEvent.ACTION_UP) {
			tcpClient.sendMessage(ballCount + " "+ currentX + " " + currentY + " " + deltaX + " " + deltaY);
		}
		previousX = currentX;
		previousY = currentY;
		return true;
	}
	

	
	@Override
	protected void onDraw (Canvas canvas) {
		tcpClient.getData();
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
				for(int i = 0; i < ballCount; i++) {
					ballBounds.set( balls[i].posX-ballRadius, balls[i].posY-ballRadius,
									balls[i].posX+ballRadius, balls[i].posY+ballRadius);
					canvas.drawOval(ballBounds,paint);
				}

		}
		update();
		invalidate();
	}
	
	private void update() {
		int ballId;
		if(data != "" && data != null && ballCount > 0){
			//System.out.println(data);
			String[] lines = data.split(" n ");
	
			for(int i = 0; i<lines.length; i++){
				String[] parts = lines[i].split(" ");
				if(parts.length>1) {
					ballId = Integer.parseInt(parts[0]);
					balls[ballId].posX = Float.parseFloat(parts[1]);
					balls[ballId].posY = Float.parseFloat(parts[2]);
					System.out.println("0: " + parts[0]);
					System.out.println("1: " + parts[1]);
					System.out.println("2: " + parts[2]);
				}
			}
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
	
}//End of BouncingBallView
