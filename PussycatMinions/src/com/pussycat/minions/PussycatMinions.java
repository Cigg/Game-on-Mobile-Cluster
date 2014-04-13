package com.pussycat.minions;

import android.util.DisplayMetrics;

import com.pussycat.framework.Screen;
import com.pussycat.framework.implementation.AndroidGame;

public class PussycatMinions extends AndroidGame {
	private static int width;
	private static int height;
	private static float ppi;
	
	private static int densityDpi;
	private static int density;
	private static int xdpi, ydpi;
    
	@Override
    public Screen getInitScreen() {
		
    	DisplayMetrics metrics = getResources().getDisplayMetrics();
    	width = metrics.widthPixels;
    	height = metrics.heightPixels;
        ppi = metrics.xdpi;
        		
		densityDpi = metrics.densityDpi;
		density = (int) metrics.density;
		xdpi = (int) metrics.xdpi;
		ydpi = (int) metrics.ydpi;
        
    	
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
    
    public static float getPpi(){
    	return ppi;
    }
    
    public static int getDensityDPI() {
    	return densityDpi;
    }
    
    public static int getDensity() {
    	return density;
    }
    
    public static int getYDPI() {
    	return ydpi;
    }
    
    public static int getXDPI() {
    	return xdpi;
    }
    
}