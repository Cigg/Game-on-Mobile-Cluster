package com.pussycat.minions.test;

import java.util.Random;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.SurfaceView;

import com.pussycat.framework.FPSCounter;
import com.pussycat.framework.Game;
import com.pussycat.framework.Screen;
import com.pussycat.minions.BallHandler;
import com.pussycat.minions.GameScreen;
import com.pussycat.minions.PussycatMinions;
import com.pussycat.minions.TCPClient;

public class PussycatMinionsTest extends
		ActivityInstrumentationTestCase2<PussycatMinions> {

	PussycatMinions pussycatMinions;
	SurfaceView view;
	Screen screen;
	GameScreen gameScreen;
	Game game;
	FPSCounter fpsCounter;
	BallHandler ballHandler;
	TCPClient tcpClient;
	Random r;
	
	public PussycatMinionsTest() {
		super(PussycatMinions.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		//setActivityInitialTouchMode(false);
		pussycatMinions = getActivity();
	}
	
	public void testFPS() {
		pussycatMinions.runOnUiThread(
				new Runnable() {
					public void run() {
						r = new Random();
						int numberOfBalls = 300;
						int maxX = pussycatMinions.getScreenHeight();
						int maxY = pussycatMinions.getScreenHeight();
						
						screen = pussycatMinions.getCurrentScreen();
						game = screen.getCurrentGame();
						game.setScreen(new GameScreen(game));
						GameScreen gameScreen = (GameScreen) game.getCurrentScreen();
						fpsCounter = gameScreen.getFPSCounter();
						ballHandler = gameScreen.getBallHandler();
						while(tcpClient == null){
							tcpClient = ballHandler.getTCPClient();
						}
						if(tcpClient != null) {
							for(int i = 0; i < numberOfBalls; i++) {
								int x = r.nextInt(maxX);
								int y = r.nextInt(maxY);
								float dx = r.nextFloat()/10;
								float dy = r.nextFloat()/10;
								Log.d("TEST", x + " " + y);
								tcpClient.sendMessage(x + " " + y + " " + dx + " " + dy);
							}
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				});
	}
	
	@Override 
	public void tearDown() throws InterruptedException {
	    while (true) { Thread.sleep(2000); }
	}
}