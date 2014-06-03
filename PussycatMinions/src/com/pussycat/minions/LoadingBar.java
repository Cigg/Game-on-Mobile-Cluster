package com.pussycat.minions;

import android.graphics.Color;

import com.pussycat.framework.Graphics;

public class LoadingBar {
	
	private int radius =  PussycatMinions.meters2Pixels(0.7f / 100.0f);;
	private AnimatedValue angle = new AnimatedValue(0);
	private AnimatedValue animatedRadius = new AnimatedValue(0);
	
	private int x, x2;
	private int y, y2;
	
	private int px;
	private int py;
	
	private AnimatedValue animatedXImg;
	private int xImg;
	private int yImg;
	private final float TIME_SIFT = 1.3f;
	private int diff = PussycatMinions.meters2Pixels(1.50f / 100.0f);
	private Animation animation;

	private int IMG_RADIUS = PussycatMinions.meters2Pixels(0.0050f);
	
	
	public LoadingBar() {
		px = (PussycatMinions.getScreenWidth() / 2);
		py = (PussycatMinions.getScreenHeight() / 2);
		
		xImg = (PussycatMinions.getScreenWidth() / 2);
		yImg = (PussycatMinions.getScreenHeight() / 2);
		animatedXImg = new AnimatedValue(0);
		
		//animation = new Animation(animatedXImg, -diff / 2.0f, diff / 2.0f, System.nanoTime(), TIME_SIFT, Animation.INTERPOLATION.COSINE, Animation.TYPE.ENDLESS);
		//AnimationHandler.getInstance().addAnimation(animation);
		
		animation = new Animation(angle, 0, 2* Math.PI, System.nanoTime(), 0.7f, Animation.INTERPOLATION.LINEAR, Animation.TYPE.ENDLESS);
		AnimationHandler.getInstance().addAnimation(animation);
		//AnimationHandler.getInstance().addAnimation(new Animation(animatedRadius, 0, 300, System.nanoTime(), 4, Animation.INTERPOLATION.COSINE, Animation.TYPE.PING_PONG));
	}
	
	
	public void update(final float time) {
		
		//radius = (int) animatedRadius.getValue();
		x = px + (int) (Math.cos(angle.getValue()) * radius);
		y = py + (int) (Math.sin(angle.getValue()) * radius);
		
		//x2 = px + (int) (Math.cos(-angle.getValue()) * -radius);
		//y2 = py + (int) (Math.sin(-angle.getValue()) * -radius);
		
	}
	
	
	public void draw(Graphics graphics) {
		
	    graphics.drawScaledImage(	Assets.loadImg, 
			 	(int)this.x - this.IMG_RADIUS, 
			 	(int)this.y - this.IMG_RADIUS, 
			 	this.IMG_RADIUS * 2, 
			 	this.IMG_RADIUS * 2, 
			 	0, 
			 	0, 
			 	Assets.loadImg.getWidth(), 
			 	Assets.loadImg.getHeight(), 0.0f	);	
	    
	    /*
		 graphics.drawScaledImage(	
			Assets.loadImg, 
			x - (int)(PussycatMinions.meters2Pixels(0.0075f*2)) / 2, 
		 	y - (int)(PussycatMinions.meters2Pixels(0.0075f*2)) / 2, 
		 	(int)(PussycatMinions.meters2Pixels(0.0075f*2)), 
		 	(int)(PussycatMinions.meters2Pixels(0.0075f*2)), 
		 	0, 
		 	0, 
		 	128, 
		 	128,
		 	0.0f	);
		 /* 
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
		
		 graphics.drawScaledImage(	
			Assets.localBall, 
			xImg + (int)animatedXImg.getValue() - (int)(PussycatMinions.meters2Pixels(0.0075f)) , 
			yImg - (int)(PussycatMinions.meters2Pixels(0.0075f)), 
		 	(int)(PussycatMinions.meters2Pixels(0.0075f*2)), 
		 	(int)(PussycatMinions.meters2Pixels(0.0075f*2)), 
		 	0, 
		 	0, 
		 	128, 
		 	128,
		 	0.0f	);
		 	 */
	}
	
	
	public void setFinished(final boolean isFinished) {
		animation.setIsFinished(isFinished);
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
