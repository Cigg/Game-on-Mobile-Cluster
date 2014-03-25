package com.pussycat.framework;

import android.util.Log;

public class FPSCounter {
    long startTime = System.nanoTime();
    int frames = 0;
    int lastFrame = 0;
    
    public void logFrame() {
        frames++;
        if(System.nanoTime() - startTime >= 1000000000) {
            Log.d("FPSCounter", "fps: " + frames);
        	lastFrame = frames;
            frames = 0;
            startTime = System.nanoTime();
        }
    }
    
    public int getFPS(){
    	return lastFrame;
    }
}