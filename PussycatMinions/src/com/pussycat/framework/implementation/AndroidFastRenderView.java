package com.pussycat.framework.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AndroidFastRenderView extends SurfaceView implements Runnable {
    AndroidGame game;
    Bitmap framebuffer;
    Thread renderThread = null;
    SurfaceHolder holder;
    volatile boolean running = false;
    
    public AndroidFastRenderView(AndroidGame game, Bitmap framebuffer) {
        super(game);
        this.game = game;
        this.framebuffer = framebuffer;
        this.holder = getHolder();
    }

    public void resume() { 
        running = true;
        renderThread = new Thread(this);
        renderThread.start();   
    }      
    
    public void run() {
    	
        Thread.currentThread().setName("AndroidFastRenderView");
        
        Rect dstRect = new Rect();
        
        float beginTime = System.nanoTime();;
        float frameTime;
        
        while(running) {  
        	
        	frameTime = System.nanoTime() - beginTime; 	// length of previous frame in nanoseconds
        	beginTime = System.nanoTime();
        	
  
            if(!holder.getSurface().isValid()) {
                continue;           
            }

            //Log.d("RENDERFPS", "RENDERFPS: " + Math.pow(10, 9) / frameTime); 
           

            game.getCurrentScreen().update(frameTime);
            game.getCurrentScreen().paint(frameTime);
          
            Canvas canvas = holder.lockCanvas();
            canvas.getClipBounds(dstRect);
            canvas.drawBitmap(framebuffer, null, dstRect, null);                           
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {                        
        running = false;                        
        while(true) {
            try {
                renderThread.join();
                break;
            } catch (InterruptedException e) {
                // retry
            }
            
        }
    }     
    
  
}