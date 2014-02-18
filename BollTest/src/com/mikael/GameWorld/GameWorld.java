package com.mikael.GameWorld;

import com.mikael.GameObjects.Boll;

public class GameWorld {

    private Boll boll;

    public GameWorld() {
    	boll = new Boll(33, 5, 10);
    }

    public void update(float delta) {
    	boll.update(delta);
    }

    public Boll getBoll() {
        return boll;

    }
}