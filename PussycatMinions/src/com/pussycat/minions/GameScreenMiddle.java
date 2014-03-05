package com.pussycat.minions;

import java.util.List;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.pussycat.framework.Game;
import com.pussycat.framework.Graphics;
import com.pussycat.framework.Image;
import com.pussycat.framework.Screen;
import com.pussycat.framework.Input.TouchEvent;

public class GameScreenMiddle extends Screen {
    enum GameState {
        Ready, Running, Paused, GameOver
    }

    GameState state = GameState.Ready;

    // Variable Setup
    // You would create game objects here.
    private BallHandler ballHandler;
    
    float ballSpeed = 20.0f;
    int screenWidth;
    int screenHeight;
    Paint paint;

    public GameScreenMiddle(Game game) {
        super(game);
        
        // Get screen dimensions
        screenWidth = PussycatMinions.getScreenWidth();
        screenHeight = PussycatMinions.getScreenHeight();

        // Initialize game objects here
        ballHandler = new BallHandler(1.5f);
        
        // Defining a paint object
		paint = new Paint();
		paint.setTextSize(30);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		
		Log.d("Debug Pussycat", "GameScreenMiddle constructor");

    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

        // We have four separate update methods in this example.
        // Depending on the state of the game, we call different update methods.
        // Refer to Unit 3's code. We did a similar thing without separating the
        // update methods.

        if (state == GameState.Ready)
            updateReady(touchEvents);
        if (state == GameState.Running)
            updateRunning(touchEvents, deltaTime);
        if (state == GameState.Paused)
            updatePaused(touchEvents);
        if (state == GameState.GameOver)
            updateGameOver(touchEvents);
    }
    
    private void updateReady(List<TouchEvent> touchEvents) {
        
        // This example starts with a "Ready" screen.
        // When the user touches the screen, the game begins. 
        // state now becomes GameState.Running.
        // Now the updateRunning() method will be called!
        
        if (touchEvents.size() > 0)
            state = GameState.Running;
    }

    private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {
       	// Update balls
    	ballHandler.update();
    	
        
        // All touch input is handled here:
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_DOWN){
            	//ballHandler.addBall(0,event.x,event.y,1.5f);
            	System.out.println(event.x + " " + event.y);
            }
//            if(event.type == TouchEvent.TOUCH_DRAGGED) {
//            	ballHandler.addBall(0,event.x,event.y,1.5f);
//            }
//            if (event.type == TouchEvent.TOUCH_UP) {
//            	Log.d("Debug Pussycat", "touch event x: " + event.x + ": " + event.y);
//            	float speedX = (screenWidth/2 - event.x);
//            	float speedY = (screenHeight/2 - event.y);
//            	float tempDist = (float)Math.sqrt(speedX*speedX + speedY*speedY);
//            	speedX = ballSpeed*speedX/tempDist;
//            	speedY = ballSpeed*speedY/tempDist;
//            	//ballHandler.addBall(event.x, event.y, 1.5f, speedX, speedY);
//            	if(ballHandler.tcpClient != null){
//            		ballHandler.tcpClient.sendMessage(event.x + " " + event.y + " " + speedX + " " + speedY);
//            	}
//            }

            
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
                if (event.x > 300 && event.x < 980 && event.y > 100
                        && event.y < 500) {
                    nullify();
                    game.setScreen(new MainMenuScreen(game));
                    return;
                }
            }
        }

    }

    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();

        g.drawARGB(255,100,100,100);
        // First draw the game elements.

        // Example:
        //g.drawImage(Assets.menu, 0, 0);
        drawBalls();
     
        // Secondly, draw the UI above the game elements.
        if (state == GameState.Ready)
            drawReadyUI();
        if (state == GameState.Running)
            drawRunningUI();
        if (state == GameState.Paused)
            drawPausedUI();
        if (state == GameState.GameOver)
            drawGameOverUI();

    }

    private void nullify() {

        // Set all variables to null. You will be recreating them in the
        // constructor.
        paint = null;

        // Call garbage collector to clean up memory.
        System.gc();
    }
    
    private void drawBalls(){
    	Graphics g = game.getGraphics();
    	
    	//Log.d("Debug Pussycat", "balls.size(): " + ballHandler.balls.size());
    	
    	for(int i = 0; i < ballHandler.balls.size(); i++){
        	g.drawScaledImage(ballHandler.balls.get(i).getImage(), (int)(ballHandler.balls.get(i).getX()-ballHandler.balls.get(i).getDiameter()/2), (int)(ballHandler.balls.get(i).getY()-ballHandler.balls.get(i).getDiameter()/2), (int)ballHandler.balls.get(i).getDiameter(), (int)ballHandler.balls.get(i).getDiameter(), 0, 0, 128, 128);
    	}
    }

    private void drawReadyUI() {
        Graphics g = game.getGraphics();
        
        g.drawARGB(155, 0, 0, 0);
        g.drawString("Tap to create a ball",
                640, 300, paint);

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