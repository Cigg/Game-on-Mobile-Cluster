package com.pussycat.minions;

public class BallTypesHandler {
	
	private enum BALL_TYPES {
		REGULAR_BALL 
	}
	
	private BallType[] ballTypes;
	
	public BallTypesHandler() {
		ballTypes = new BallType[32];

		ballTypes[BALL_TYPES.REGULAR_BALL.ordinal()] = new BallType(Assets.localBall, 50);
		
	//	ballTypes[0] = new BallType(Assets.localBall, Float.parseFloat(PussycatMinions.settings.getSetting("ballRadius")));
	}
	
	public BallType getBallType(final int type) {
		return ballTypes[type];
	}
}
