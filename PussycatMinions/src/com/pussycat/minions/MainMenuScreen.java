package com.pussycat.minions;

import java.util.List;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.pussycat.framework.Game;
import com.pussycat.framework.Graphics;
import com.pussycat.framework.Screen;
import com.pussycat.framework.Input.TouchEvent;


public class MainMenuScreen extends Screen {
	
	Button playerButton;
	Button middleButton;
	Paint paint;
	
    public MainMenuScreen(Game game) {
        super(game);
        
        // Defining a paint object
		paint = new Paint();
		paint.setTextSize(30);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		
		playerButton = new Button(Assets.button_player, Assets.button_player_pressed, PussycatMinions.getScreenWidth()/2 - Assets.button_middle.getWidth()/2, PussycatMinions.getScreenHeight()/2-100);
		middleButton = new Button(Assets.button_middle, Assets.button_middle_pressed, PussycatMinions.getScreenWidth()/2 - Assets.button_middle.getWidth()/2, PussycatMinions.getScreenHeight()/2+100);
        
    }
    
    @Override
    public void update(float deltaTime) {
        Graphics g = game.getGraphics();
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            
            if(event.type == TouchEvent.TOUCH_DOWN){
            	if(playerButton.inBounds(event.x, event.y)){
            		playerButton.setPressed(true);
            	}
            	if(middleButton.inBounds(event.x, event.y)){
            		middleButton.setPressed(true);
            	}
            }
            
            if(event.type == TouchEvent.TOUCH_DRAGGED){
            	if(!playerButton.inBounds(event.x, event.y)){
            		playerButton.setPressed(false);
            	}
            	if(!middleButton.inBounds(event.x, event.y)){
            		middleButton.setPressed(false);
            	}
            }
            
            if (event.type == TouchEvent.TOUCH_UP) {
            	playerButton.setPressed(false);
            	middleButton.setPressed(false);
            	
            	if(playerButton.inBounds(event.x, event.y)){
            		game.setScreen(new GameScreen(game));
            	}
            	if(middleButton.inBounds(event.x, event.y)){
            		game.setScreen(new GameScreenMiddle(game));
            	}
            	//START GAME
//            	if(inBounds(event, 0,0, PussycatMinions.getScreenWidth(), PussycatMinions.getScreenHeight()/2))
//            		game.setScreen(new GameScreen(game));
//            	if(inBounds(event, 0,PussycatMinions.getScreenHeight()/2, PussycatMinions.getScreenWidth(), PussycatMinions.getScreenHeight()))
//            		game.setScreen(new GameScreenMiddle(game));
            }
        }
    }
    
    private boolean inBounds(TouchEvent event, int x, int y, int width,
            int height) {
        if (event.x > x && event.x < x + width - 1 && event.y > y
                && event.y < y + height - 1)
            return true;
        else
            return false;
    }


    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();
        
        drawUI();
    }
    
    private void drawUI() {
        Graphics g = game.getGraphics();
        
        playerButton.drawButton(g);
        middleButton.drawButton(g);
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