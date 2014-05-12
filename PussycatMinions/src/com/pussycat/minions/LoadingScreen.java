package com.pussycat.minions;


import java.io.File;

import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.Log;

import com.pussycat.framework.Game;
import com.pussycat.framework.Graphics;
import com.pussycat.framework.Screen;
import com.pussycat.framework.Graphics.ImageFormat;
import com.pussycat.framework.implementation.AndroidGraphics;


public class LoadingScreen extends Screen {
	
	private float startTime;
	private float durationTime = (float) (2
			* Math.pow(10, 9));
	private LoadingBar loadingbar;
	
    public LoadingScreen(Game game) {
        super(game);
        
        startTime = System.nanoTime();
        
        Graphics g = game.getGraphics();
        Assets.click = game.getAudio().createSound("explosion.ogg");
        Assets.ball = g.newImage("baby.png", ImageFormat.RGB565);
        Assets.localBall = g.newImage("baby.png", ImageFormat.RGB565);
        Assets.octopus = g.newImage("octopus.png", ImageFormat.RGB565);
        Assets.button = g.newImage("button.png", ImageFormat.RGB565);
        Assets.button_pressed = g.newImage("button_pressed.png", ImageFormat.RGB565);
        //Assets.background = g.newBackground("colorspectrum.jpg", ImageFormat.RGB565);
        Assets.settings = g.newImage("settings.png",  ImageFormat.RGB565);
        Assets.settings_pressed = g.newImage("settings_pressed.png", ImageFormat.RGB565);
        
        AssetManager assets = AndroidGraphics.getAssets();
        Assets.menu_font = Typeface.createFromAsset(assets, "fonts" + File.separator + "angrybirds-regular.ttf");

        AnimationHandler.getInstance();
        loadingbar = new LoadingBar();
    }


    @Override
    public void update(float deltaTime) {
        if(startTime + durationTime - System.nanoTime() <= 0.0f) {
        	loadingbar.setFinished(true);
        	game.setScreen(new MainMenuScreen(game));
        }
        AnimationHandler.getInstance().updateAnimations(System.nanoTime());
        loadingbar.update(System.nanoTime());
    }


    @Override
    public void paint(float deltaTime) {
        Graphics graphics = game.getGraphics();
        Canvas canvas = graphics.getCanvas();
        loadingbar.draw(graphics);
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