package com.pussycat.minions;

import com.pussycat.framework.Screen;
import com.pussycat.framework.implementation.AndroidGame;

public class PussycatMinions extends AndroidGame {
    @Override
    public Screen getInitScreen() {
    	
        return new LoadingScreen(this);
    }
}