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
	Button aboutButton;
	
	Paint paint;
	
    public MainMenuScreen(Game game) {
        super(game);
        
        // Defining a paint object
		paint = new Paint();
		paint.setTypeface(Assets.menu_font);
		paint.setTextSize(42);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		
		Graphics g = game.getGraphics();
		
		playerButton = new Button(Assets.button, Assets.button_pressed, PussycatMinions.getScreenWidth()/2 - Assets.button.getWidth()/2, (int) (PussycatMinions.getScreenHeight()*0.58f), paint);
		playerButton.setText("PLAYER");
		playerButton.scaleButton(g, (int)(PussycatMinions.getScreenWidth()*0.45));
		playerButton.setX(PussycatMinions.getScreenWidth()/2 - playerButton.getWidth()/2);
		playerButton.setTextSize(playerButton.getHeight()/3);
		middleButton = new Button(Assets.button, Assets.button_pressed, PussycatMinions.getScreenWidth()/2 - Assets.button.getWidth()/2, (int) (PussycatMinions.getScreenHeight()*0.7f), paint);
		middleButton.setText("MIDDLE");
		middleButton.scaleButton(g, (int)(PussycatMinions.getScreenWidth()*0.45));
		middleButton.setX(PussycatMinions.getScreenWidth()/2 - playerButton.getWidth()/2);
		middleButton.setTextSize(middleButton.getHeight()/3);
        aboutButton = new Button(Assets.button, Assets.button_pressed, PussycatMinions.getScreenWidth()/2 - Assets.button.getWidth()/2,  (int) (PussycatMinions.getScreenHeight()*0.82f), paint);
        aboutButton.setText("ABOUT");
        aboutButton.scaleButton(g, (int)(PussycatMinions.getScreenWidth()*0.45));
        aboutButton.setX(PussycatMinions.getScreenWidth()/2 - playerButton.getWidth()/2);
        aboutButton.setTextSize(aboutButton.getHeight()/3);
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
            	if(aboutButton.inBounds(event.x, event.y)){
            		aboutButton.setPressed(true);
            	}
            }
            
            if(event.type == TouchEvent.TOUCH_DRAGGED){
            	if(!playerButton.inBounds(event.x, event.y)){
            		playerButton.setPressed(false);
            	}
            	if(!middleButton.inBounds(event.x, event.y)){
            		middleButton.setPressed(false);
            	}
            	if(!aboutButton.inBounds(event.x, event.y)){
            		aboutButton.setPressed(false);
            	}
            }
            
            if (event.type == TouchEvent.TOUCH_UP) {
            	// TODO: Should go to SetupScreen instead
            	if(playerButton.isPressed() && playerButton.inBounds(event.x, event.y)){
            		game.setScreen(new GameScreenPlayer(game));
            		playerButton.setPressed(false);
            	} else if(middleButton.isPressed() && middleButton.inBounds(event.x, event.y)){
            		game.setScreen(new GameScreenMiddle(game));
            		middleButton.setPressed(false);
            	} else if(aboutButton.isPressed() && aboutButton.inBounds(event.x, event.y)){
            		game.setScreen(new GameScreenMiddle(game));
            		aboutButton.setPressed(false);
            	} 

            }
        }
    }
    
    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawARGB(255,100,100,100);
        
        drawUI();
    }
    
    private void drawUI() {
        Graphics g = game.getGraphics();
        //g.drawScaledImage(Assets.mainMenuBackground, 0, 0, PussycatMinions.getScreenWidth(), PussycatMinions.getScreenHeight(), 0, 0, Assets.mainMenuBackground.getWidth(), Assets.mainMenuBackground.getHeight(), 0);
        g.drawImage(Assets.mainMenuBackground, 0, 0);
        playerButton.drawButton(g);
        middleButton.drawButton(g);
        aboutButton.drawButton(g);
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