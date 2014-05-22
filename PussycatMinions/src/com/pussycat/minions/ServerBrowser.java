package com.pussycat.minions;

import java.util.ArrayList;
import java.util.List;

import com.pussycat.framework.Game;
import com.pussycat.framework.Graphics;
import com.pussycat.framework.Screen;
import com.pussycat.framework.Input.TouchEvent;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class ServerBrowser extends Screen {
	
	Button[] buttons;
	Paint paint;
	
	public ServerBrowser(Game game) {
        super(game);
        
		paint = new Paint();
		paint.setTextSize(40);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		
		initializeServers();
	}
	
	
	public void initializeServers() {
		ArrayList<Server> servers = SharedVariables.getInstance().getServers();
		buttons = new Button[servers.size()];
		
		int width = PussycatMinions.getScreenWidth()/2 - Assets.button.getWidth()/2;
		for(int i=0; i<servers.size(); i++) {
			Log.d("BROWSE", "ADDED BUTTON");
			buttons[i] = new Button(Assets.button, Assets.button_pressed, width, PussycatMinions.getScreenHeight()/2 + 300, paint);
			buttons[i].setText(servers.get(i).name);
		}
	}
	
	
	public void refresh() {
		ServerScanner scanner = new ServerScanner();
		scanner.scanForServers();
		
		initializeServers();
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
   			} else if(event.type == TouchEvent.TOUCH_DRAGGED){
   				for(int j=0; j<buttons.length; j++) {
            		if(!buttons[j].inBounds(event.x, event.y)){
            			buttons[j].setPressed(false);
       				}
            	}
   			} else if(event.type == TouchEvent.TOUCH_UP) {
   				for(int j=0; j<buttons.length; j++) {
            		if(buttons[j].inBounds(event.x, event.y)){
            			ArrayList<Server> servers = SharedVariables.getInstance().getServers();
            			SharedVariables.getInstance().setServer(servers.get(j));
            			game.setScreen(new MainMenuScreen(game));
       				}
            		buttons[j].setPressed(false);
            	}
   			}
   		}
		        
	}

	@Override
	public void paint(float deltaTime) {
		// TODO Auto-generated method stub

		Graphics graphics = game.getGraphics();
		
		if(buttons != null) {
			for(int i=0; i<buttons.length; i++) {
				//Log.d("BROWSE", "DRAW BUTTON");
				buttons[i].drawButton(graphics);
			}
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
