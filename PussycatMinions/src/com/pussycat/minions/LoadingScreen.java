package com.pussycat.minions;


import android.util.Log;

import com.pussycat.framework.Game;
import com.pussycat.framework.Graphics;
import com.pussycat.framework.Screen;
import com.pussycat.framework.Graphics.ImageFormat;


public class LoadingScreen extends Screen {
    public LoadingScreen(Game game) {
        super(game);
    }


    @Override
    public void update(float deltaTime) {
        Graphics g = game.getGraphics();
        Assets.click = game.getAudio().createSound("explosion.ogg");
        Assets.ball = g.newImage("ball.png", ImageFormat.RGB565);
        Assets.localBall = g.newImage("localBall.png", ImageFormat.RGB565);
        Assets.frog = g.newImage("frog.png", ImageFormat.RGB565);
        Assets.button = g.newImage("button.png", ImageFormat.RGB565);
        Assets.button_pressed = g.newImage("button_pressed.png", ImageFormat.RGB565);
        Assets.background = g.newBackground("colorspectrum.jpg", ImageFormat.RGB565);
        
        Log.d("Debug Pussycat", "LoadingScreen update");
        
        game.setScreen(new MainMenuScreen(game));
    }


    @Override
    public void paint(float deltaTime) {


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