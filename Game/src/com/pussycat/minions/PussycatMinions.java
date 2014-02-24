package com.pussycat.minions;

import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;

import com.pussycat.framework.Screen;
import com.pussycat.framework.implementation.AndroidGame;

public class PussycatMinions extends AndroidGame {
	static int width;
	static int height;
    @Override
    public Screen getInitScreen() {
    	
    	DisplayMetrics metrics = getResources().getDisplayMetrics();
    	width = metrics.widthPixels;
    	height = metrics.heightPixels;
        
        return new LoadingScreen(this);
    }
    
    public static int getWidth(){
    	return width;
    }
    
    public static int getHeight(){
    	return height;
    }
}