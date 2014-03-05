package com.pussycat.framework.implementation.test;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.pussycat.framework.implementation.AndroidGame;

/**
 * @author Sebastian
 *
 */
public class AndroidGameTest extends
		ActivityInstrumentationTestCase2<AndroidGame> {
	AndroidGame androidGame;
	View view;
	
	public AndroidGameTest() {
		super(AndroidGame.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		androidGame = getActivity();
		view = androidGame.getCurrentFocus();
		Log.d("TEST", "HEJ");
		
		//MotionEvent.obtain(downTime, eventTime, action, x, y, pressure, size, metaState, xPrecision, yPrecision, deviceId, edgeFlags)
	}
	
}
