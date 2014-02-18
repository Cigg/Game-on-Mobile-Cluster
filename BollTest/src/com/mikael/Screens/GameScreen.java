package com.mikael.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.mikael.GameWorld.GameRenderer;
import com.mikael.GameWorld.GameWorld;
import com.mikael.GameHelpers.InputHandler;

public class GameScreen implements Screen {

    private GameWorld world;
    private GameRenderer renderer;
    private float runTime;

    // This is the constructor, not the class declaration
    public GameScreen() {

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float gameWidth = 136;
        float gameHeight = screenHeight / (screenWidth / gameWidth);
        
        float scale = screenWidth / gameWidth;

        world = new GameWorld();
        renderer = new GameRenderer(world, (int)gameHeight);

        Gdx.input.setInputProcessor(new InputHandler(world.getBoll(), scale));

    }

    @Override
    public void render(float delta) {
        runTime += delta;
        world.update(delta);
        renderer.render(runTime);
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("GameScreen - resizing");
    }

    @Override
    public void show() {
        System.out.println("GameScreen - show called");
    }

    @Override
    public void hide() {
        System.out.println("GameScreen - hide called");
    }

    @Override
    public void pause() {
        System.out.println("GameScreen - pause called");
    }

    @Override
    public void resume() {
        System.out.println("GameScreen - resume called");
    }

    @Override
    public void dispose() {
        // Leave blank
    }

}
