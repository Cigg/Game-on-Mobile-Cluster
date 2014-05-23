package com.pussycat.minions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.pussycat.framework.Game;
import com.pussycat.framework.Graphics;
import com.pussycat.framework.Screen;
import com.pussycat.framework.Input.TouchEvent;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class ServerBrowser extends Screen {
	
	private Button[] buttons;
	private Paint paint;
	
	private Button refreshButton;
	
	private final int WIDTH = PussycatMinions.getScreenWidth()/2 - Assets.button.getWidth()/2;
	private final int OFFSET = PussycatMinions.meters2Pixels( 0.5f / 100.0f);
	
	private LoadingBar loadingbar; 
	private AtomicBoolean doneScanning = new AtomicBoolean(true);
	
	private final int TEXT_X = PussycatMinions.getScreenWidth()/2;
	private final int TEXT_Y = PussycatMinions.getScreenHeight()/2;
	
	
	public ServerBrowser(Game game) {
        super(game);
        
		paint = new Paint();
		paint.setTypeface(Assets.menu_font);
		paint.setTextSize(42);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		
		refreshButton = new Button(Assets.button, Assets.button_pressed, WIDTH, PussycatMinions.getScreenHeight() - OFFSET - Assets.button.getHeight(), paint);
		refreshButton.setText("Refresh");
		
		initializeServers();
	}
	
	
	public void initializeServers() {
		ArrayList<Server> servers = SharedVariables.getInstance().getServers();
		buttons = new Button[servers.size()];
	
		for(int i=0; i<servers.size(); i++) {
			Log.d("BROWSE", "ADDED BUTTON");
			buttons[i] = new Button(Assets.button, Assets.button_pressed, WIDTH, OFFSET*(i+1) + Assets.button.getHeight()*i, paint);
			buttons[i].setText(servers.get(i).name + ",  " + servers.get(i).slotsTaken + "/" + servers.get(i).numberOfSlots);
		}
	}
	
	
	public void refresh() {
		if(doneScanning.get()) {
			loadingbar = new LoadingBar();
			doneScanning.set(false);
			Thread scannerThread = new Thread(new Runnable() {
				public void run() {	
					ServerScanner scanner = new ServerScanner();
					scanner.scanForServers();
					doneScanning.set(true);
				}
			});
			scannerThread.start();
		}
	}
	
	
	
	@Override
	public void update(float deltaTime) {
		// TODO Auto-generated method stub
		List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

        for (int i=0; i<touchEvents.size(); i++) {
            TouchEvent event = touchEvents.get(i);
            
            if(event.type == TouchEvent.TOUCH_DOWN){
            	for(int j=0; j<buttons.length; j++) {
            		if(buttons[j].inBounds(event.x, event.y)){
            			buttons[j].setPressed(true);
       				}
            	}
            	
            	if(refreshButton.inBounds(event.x, event.y)){
        			refreshButton.setPressed(true);
        		}
   			} else if(event.type == TouchEvent.TOUCH_DRAGGED){
   				for(int j=0; j<buttons.length; j++) {
            		if(!buttons[j].inBounds(event.x, event.y)){
            			buttons[j].setPressed(false);
       				}
            	}
   				
   				if(!refreshButton.inBounds(event.x, event.y)){
        			refreshButton.setPressed(false);
        		}
   			} else if(event.type == TouchEvent.TOUCH_UP) {
   				for(int j=0; j<buttons.length; j++) {
            		if(buttons[j].isPressed() && buttons[j].inBounds(event.x, event.y)){
            			ArrayList<Server> servers = SharedVariables.getInstance().getServers();
            			SharedVariables.getInstance().setServer(servers.get(j));
            			game.setScreen(new MainMenuScreen(game));
            			buttons[j].setPressed(false);
       				}
            		
            	}
   				
   				if(refreshButton.isPressed() && refreshButton.inBounds(event.x, event.y)){
   					refreshButton.setPressed(false);
   					refresh();
        		}
   			}
   		}
        
        if(doneScanning.get()) {
        	if(loadingbar != null) {
        		initializeServers();
        		loadingbar.setFinished(true);
        		loadingbar = null;
        	}
        } else {
        	if(loadingbar != null) {
	        	 AnimationHandler.getInstance().updateAnimations(System.nanoTime());
	        	 loadingbar.update(System.nanoTime());
        	}
        }       
	}

	@Override
	public void paint(float deltaTime) {
		// TODO Auto-generated method stub

		Graphics graphics = game.getGraphics();
		//graphics.drawImage(Assets.splash, 0, 0);
		// TODO: Fix ServerBrowser Screen
		graphics.clearScreen(Color.WHITE);
		graphics.drawImage(Assets.mainMenuBackground, 0, 0);
		
		if(buttons != null && buttons.length != 0) {
			for(int i=0; i<buttons.length; i++) {
				buttons[i].drawButton(graphics);
			}
		} else {
			paint.setColor(Color.WHITE);
			graphics.drawString("No servers available.", TEXT_X, TEXT_Y, paint);
			paint.setColor(Color.WHITE);
		}
		
		if(refreshButton != null) {
			refreshButton.drawButton(graphics);
		}
		
		if(loadingbar != null) {
			loadingbar.draw(graphics);
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
