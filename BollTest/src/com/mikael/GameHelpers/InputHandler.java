package com.mikael.GameHelpers;

import com.badlogic.gdx.InputProcessor;
import com.mikael.GameObjects.Boll;

public class InputHandler implements InputProcessor {
	
	private Boll myBoll;
	private float scale;
	
    public InputHandler(Boll boll, float scale) {
    	this.scale = scale;
        myBoll = boll;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    	myBoll.onPressed();
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    	myBoll.onReleased((int)(screenX/scale), (int)(screenY/scale));
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
    	myBoll.onDragged((int)(screenX/scale), (int)(screenY/scale));
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}
