package com.pussycat.minions;

import com.pussycat.framework.Graphics;

public class LoadingBar {
	
	private int radius = 0;
	private AnimatedValue angle = new AnimatedValue(0);
	private AnimatedValue animatedRadius = new AnimatedValue(0);
	
	private int x, x2;
	private int y, y2;
	
	private int px;
	private int py;

	
	public LoadingBar() {
		px = (PussycatMinions.getScreenWidth() / 2);
		py = (PussycatMinions.getScreenHeight() / 2);
		
		AnimationHandler.getInstance().addAnimation(new Animation(angle, 0, 2* Math.PI, System.nanoTime(), 2, Animation.INTERPOLATION.COSINE, Animation.TYPE.PING_PONG));
		AnimationHandler.getInstance().addAnimation(new Animation(animatedRadius, 0, 300, System.nanoTime(), 4, Animation.INTERPOLATION.COSINE, Animation.TYPE.PING_PONG));
	}
	
	
	public void update(final float time) {
		radius = (int) animatedRadius.getValue();
		x = px + (int) (Math.cos(angle.getValue()) * radius);
		y = py + (int) (Math.sin(angle.getValue()) * radius);
		
		x2 = px + (int) (Math.cos(-angle.getValue()) * -radius);
		y2 = py + (int) (Math.sin(-angle.getValue()) * -radius);
	}
	
	
	public void draw(Graphics graphics) {
		 graphics.drawScaledImage(	
			Assets.localBall, 
			x - (int)(PussycatMinions.meters2Pixels(0.0075f*2)) / 2, 
		 	y - (int)(PussycatMinions.meters2Pixels(0.0075f*2)) / 2, 
		 	(int)(PussycatMinions.meters2Pixels(0.0075f*2)), 
		 	(int)(PussycatMinions.meters2Pixels(0.0075f*2)), 
		 	0, 
		 	0, 
		 	128, 
		 	128,
		 	0.0f	);
		 
		 graphics.drawScaledImage(	
			Assets.localBall, 
			x2 - (int)(PussycatMinions.meters2Pixels(0.0075f*2)) / 2, 
		 	y2 - (int)(PussycatMinions.meters2Pixels(0.0075f*2)) / 2, 
		 	(int)(PussycatMinions.meters2Pixels(0.0075f*2)), 
		 	(int)(PussycatMinions.meters2Pixels(0.0075f*2)), 
		 	0, 
		 	0, 
		 	128, 
		 	128,
		 	0.0f	);
	}
	
	
	public void setPx(final int px) {
		this.px = px;
	}

	
	public void setPy(final int py) {
		this.py = py;
	}
	
	
	public void setRadius(final int radius) {
		this.radius = radius;
	}
	

}
