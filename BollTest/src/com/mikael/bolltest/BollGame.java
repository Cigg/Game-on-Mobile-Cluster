package com.mikael.bolltest;

import com.badlogic.gdx.Game;
import com.mikael.Screens.GameScreen;
import com.mikael.GameHelpers.AssetLoader;

public class BollGame extends Game {

    @Override
    public void create() {
        System.out.println("BollGame Created!");
        AssetLoader.load();
        setScreen(new GameScreen());
    }

    @Override
    public void dispose() {
        super.dispose();
        AssetLoader.dispose();
    }

}