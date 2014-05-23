package com.pussycat.minions;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

import com.pussycat.framework.Game;
import com.pussycat.framework.Graphics;
import com.pussycat.framework.Image;
import com.pussycat.framework.Screen;
import com.pussycat.framework.Graphics.ImageFormat;
import com.pussycat.framework.implementation.AndroidGraphics;
import com.pussycat.framework.implementation.AndroidImage;
import com.pussycat.minions.Device.IndexPair;


public class LoadingScreen extends Screen {
	
	private float startTime;
	private float durationTime = (float) (1 * Math.pow(10, 9));
	private LoadingBar loadingbar;
	
	private AtomicBoolean doneLoading = new AtomicBoolean(false);
	private AtomicBoolean doneScanning = new AtomicBoolean(false);
	
	
    public LoadingScreen(final Game game) {
        super(game);
        
        Graphics g = game.getGraphics();

        // Preload the Assets which are used by LoadingScreen
        Assets.splash = g.newImage("mainpage.png", ImageFormat.RGB565);
        Assets.splash = g.newScaledImage(Assets.splash, PussycatMinions.getScreenWidth(), PussycatMinions.getScreenHeight());
        Assets.localBall = g.newImage("baby.png", ImageFormat.RGB565);
        Assets.ball1 = g.newImage("Bug_1.png", ImageFormat.RGB565);
        Assets.ball2 = g.newImage("Bug_2.png", ImageFormat.RGB565);
        Assets.ball3 = g.newImage("Bug_3.png", ImageFormat.RGB565);
         
        Thread loadingThread = new Thread(new Runnable() {
        	public void run() {
        		Graphics g = game.getGraphics();
        		
        		// Load the Assets with largest size first - otherwise out of memory error will occur
        	    //Assets.background = g.newBackground("colorspectrum.jpg", ImageFormat.RGB565);
        		Assets.mainMenuBackground = g.newImage("mainpage.png", ImageFormat.RGB565);
     	        Assets.mainMenuBackground = g.newScaledImage(Assets.mainMenuBackground, PussycatMinions.getScreenWidth(), PussycatMinions.getScreenHeight());
     	        Assets.octopus = g.newImage("Spider_target.png", ImageFormat.RGB565);
     	       
     	        //Assets.click = game.getAudio().createSound("explosion.ogg");
    	        
    	        Assets.button = g.newImage("button.png", ImageFormat.RGB565);
    	        Assets.button_pressed = g.newImage("button_pressed.png", ImageFormat.RGB565);
    	    
    	        Assets.settings = g.newImage("settings.png",  ImageFormat.RGB565);
    	        Assets.settings_pressed = g.newImage("settings_pressed.png", ImageFormat.RGB565);

    	        Assets.countDownImage_1 = g.newImage("countDownImage_1.png", ImageFormat.RGB565);
    	        Assets.countDownImage_2 = g.newImage("countDownImage_2.png", ImageFormat.RGB565);
    	        Assets.countDownImage_3 = g.newImage("countDownImage_3.png", ImageFormat.RGB565);
    	        Assets.countDownImage_GO = g.newImage("countDownImage_GO.png", ImageFormat.RGB565);
    	        
    	        Assets.horizontal_line = g.newImage("horizontalline.png" , ImageFormat.RGB565);
    	        Assets.vertical_line = g.newImage("verticalline.png" , ImageFormat.RGB565);
    	        
    	        Assets.ball = g.newImage("baby.png", ImageFormat.RGB565);
    	        
    	        AssetManager assets = AndroidGraphics.getAssets();
    	        Assets.menu_font = Typeface.createFromAsset(assets, "fonts" + File.separator + "angrybirds-regular.ttf");
      	      
    	        doneLoading.set(true);
        	}
        });
       
        
        Thread scanningThread = new Thread(new Runnable() {
        	public void run() {
        		ServerScanner scanner = new ServerScanner();
        		scanner.scanForServers();
        		doneScanning.set(true);
        	}
        });
        
      
        AnimationHandler.getInstance(); // Needed inorder to initialize
        loadingbar = new LoadingBar();        
        startTime = System.nanoTime();
        
        scanningThread.start();
        loadingThread.start();
    }


    @Override
    public void update(float deltaTime) {
        if(doneScanning.get() && doneLoading.get() && (startTime + durationTime - System.nanoTime() <= 0.0f)) {
        	loadingbar.setFinished(true);
        	//game.setScreen(new MainMenuScreen(game));
        	Assets.splash.dispose();
        	game.setScreen(new ServerBrowser(game));
        }
        AnimationHandler.getInstance().updateAnimations(System.nanoTime());
        loadingbar.update(System.nanoTime());
    }


    @Override
    public void paint(float deltaTime) {
    	Graphics g = game.getGraphics();
        g.drawImage(Assets.splash, 0, 0);
        loadingbar.draw(g);
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


    }
}