package com.pussycat.minions;

import java.util.List;

import android.graphics.Color;
import android.graphics.Paint;

import com.pussycat.framework.Game;
import com.pussycat.framework.Graphics;
import com.pussycat.framework.Input.TouchEvent;
import com.pussycat.framework.Screen;
import com.pussycat.minions.GameScreenPlayer.GameState;

public class SetupScreenPlayer extends Screen {
    enum GameState {
        Ready, Running
    }
    
    private Button skipButton;
    
    private GameState state = GameState.Ready;

	Paint paint;
	
    public SetupScreenPlayer(Game game) {
        super(game);
        
        // Defining a paint object
		paint = new Paint();
		paint.setTextSize(30);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		
		skipButton = new Button(Assets.button_skip, Assets.button_skip_pressed, PussycatMinions.getScreenWidth()/2 - Assets.button_skip.getWidth()/2, PussycatMinions.getScreenHeight() - 2*Assets.button_skip.getHeight());
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
    	int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            
            if(event.type == TouchEvent.TOUCH_DOWN){
            	if(skipButton.inBounds(event.x, event.y)){
            		skipButton.setPressed(true);
            	}
            }
            
            if(event.type == TouchEvent.TOUCH_DRAGGED){
            	if(!skipButton.inBounds(event.x, event.y)){
            		skipButton.setPressed(false);
            	}
            }
            
            if (event.type == TouchEvent.TOUCH_UP) {
            	skipButton.setPressed(false);
            	
            	if(skipButton.inBounds(event.x, event.y)){
            		game.setScreen(new GameScreenPlayer(game));
            	}
            }
        }
    }
   
    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();
        
        g.drawARGB(255,100,100,100);
        
        // Secondly, draw the UI above the game elements.
        if (state == GameState.Ready)
            drawReadyUI();
        if (state == GameState.Running)
            drawRunningUI();
    }
    
    private void drawReadyUI() {
        Graphics g = game.getGraphics();
        
        g.drawARGB(155, 0, 0, 0);
        g.drawString("Drag your finger with a constant speed from",
                PussycatMinions.getScreenWidth()/2, 300, paint);
        
        g.drawString("this device to the middle one!",
        		PussycatMinions.getScreenWidth()/2, 350, paint);
    }

    private void drawRunningUI() {
        Graphics g = game.getGraphics();
        
        skipButton.drawButton(g);
        
    }


    @Override
    public void pause() {
    }


    @Override
    public void resume() {


    }


    @Override
    public void dispose() {


    }


    @Override
    public void backButton() {
        //Display "Exit Game?" Box


    }
}