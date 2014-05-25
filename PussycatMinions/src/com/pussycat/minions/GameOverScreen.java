package com.pussycat.minions;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.pussycat.framework.Game;
import com.pussycat.framework.Graphics;
import com.pussycat.framework.Screen;
import com.pussycat.framework.Input.TouchEvent;

public class GameOverScreen extends Screen {
	
	private final int TEXT_X = PussycatMinions.getScreenWidth()/2;
	private final int TEXT_Y = 200;
	
	private final int WIDTH = PussycatMinions.getScreenWidth()/2 - Assets.button.getWidth()/2;
	private final int OFFSET = PussycatMinions.meters2Pixels( 0.5f / 100.0f);
	
	private Screen oldScreen;
	private Paint paint;	
	
	private Button replayButton;
	private Button browseButton;
	
	public GameOverScreen(Game game, Screen oldScreen) {
		super(game);
		//oldScreen.pause();
		this.oldScreen = oldScreen;
		
		Log.d("GAMEOVER", "NEW GAMEOVERSCREEN!!");
		paint = new Paint();
		paint.setTypeface(Assets.menu_font);
		paint.setTextSize(42);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		
		
		replayButton = new Button(Assets.button, Assets.button_pressed, WIDTH, PussycatMinions.getScreenHeight() - 2*OFFSET - 2*Assets.button.getHeight(), paint);
		replayButton.setText("Replay");
		
		browseButton = new Button(Assets.button, Assets.button_pressed, WIDTH, PussycatMinions.getScreenHeight() - OFFSET - Assets.button.getHeight(), paint);
		browseButton.setText("Choose Server");
	}

	@Override
	public void update(float deltaTime) {
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

        for (int i=0; i<touchEvents.size(); i++) {
            TouchEvent event = touchEvents.get(i);
            
            if(event.type == TouchEvent.TOUCH_DOWN){
            
        		if(replayButton.inBounds(event.x, event.y)){
        			replayButton.setPressed(true);
   				} else if(browseButton.inBounds(event.x, event.y)){
   					browseButton.setPressed(true);
   				}
         
   			} else if(event.type == TouchEvent.TOUCH_DRAGGED){
   		 		
   				if(!replayButton.inBounds(event.x, event.y)){
        			replayButton.setPressed(false);
   				} 
   		 		
   		 		if(!browseButton.inBounds(event.x, event.y)){
   					browseButton.setPressed(false);
   				}
   				
   			} else if(event.type == TouchEvent.TOUCH_UP) {
   				
   				if(replayButton.inBounds(event.x, event.y)){
   					game.setScreen(oldScreen);
        			replayButton.setPressed(false);
   				} else if(browseButton.inBounds(event.x, event.y)){
   					//oldScreen.pause();
   					//oldScreen.dispose();
   		        
   					SharedVariables.getInstance().release();
   					game.setScreen(new ServerBrowserScreen(game));
   					browseButton.setPressed(false);
   				}
   				
   			}
   		}
        
		
	}

	@Override
	public void paint(float deltaTime) {
		Graphics graphics = game.getGraphics();
		//graphics.getCanvas().drawColor(Color.WHITE);
		graphics.drawImage(Assets.mainMenuBackground, 0, 0);
		
		ArrayList<ShortPair> finalScores = SharedVariables.getInstance().getFinalScores();
		
		paint.setTextSize(62);
		
		for(int i=0; i<finalScores.size() && i<3; i++) {
			ShortPair score = finalScores.get(i);
			paint.setColor(SharedVariables.getInstance().getColor(score.id));
			graphics.drawString( (i+1) + "  :  " + score.second + "  :  " + score.second * 100, TEXT_X, TEXT_Y + i*100, paint);
			//graphics.drawString( (i+1) + " : " + score.second + " : " + score.second * 100, TEXT_X, TEXT_Y + i*100, paint);
		}
		
		paint.setTextSize(42);
		paint.setColor(Color.WHITE);
		
		if(replayButton != null) {
			replayButton.drawButton(graphics);
		}
		
		if(browseButton != null) {
			browseButton.drawButton(graphics);
		}
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void backButton() {
		// TODO Auto-generated method stub
		
	}

}
