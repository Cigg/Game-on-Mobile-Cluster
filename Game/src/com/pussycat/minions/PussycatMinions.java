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
        
    	//TO-DO: calculate the correct height of display
    	/*
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height += getResources().getDimensionPixelSize(resourceId);
        }
        */
    	
        return new LoadingScreen(this);
    }
    
    public static int getScreenWidth(){
    	return width;
    }
    
    public static int getScreenHeight(){
    	return height;
    }
}