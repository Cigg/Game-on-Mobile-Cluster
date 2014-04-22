package com.pussycat.minions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;

import com.pussycat.framework.Graphics;
import com.pussycat.framework.Image;
import com.pussycat.framework.implementation.AndroidFastRenderView;
import com.pussycat.framework.implementation.AndroidFileIO;
import com.pussycat.framework.implementation.AndroidGame;
import com.pussycat.framework.implementation.AndroidGraphics;
import com.pussycat.framework.implementation.AndroidImage;
import com.pussycat.minions.BackgroundHandler.BACKGROUNDS;

public class Device {
	
	private static Bitmap backgroundTiled;
	private static Bitmap backgroundFinal;
	private static Canvas backgroundCanvas;
			
	private static int screenHeight;
	private static int screenWidth;
	
	public static float xdpi;
	public static float ydpi;
	private final float densityDpi;
	private final float density;
	
	private static int l_half;
	private static int l_side;
	private static boolean[][] includeTile;
	
	private static Background bg;
	
	public Device() {
		this.screenHeight = PussycatMinions.getScreenHeight();
		this.screenWidth = PussycatMinions.getScreenWidth();
		
		this.densityDpi = PussycatMinions.getDensityDPI();
		this.density = PussycatMinions.getDensity();
		this.xdpi = PussycatMinions.getXDPI();
		this.ydpi = PussycatMinions.getYDPI();
	
		BackgroundHandler bgh = new BackgroundHandler();
		this.bg = bgh.backgrounds[BACKGROUNDS.COORDINATES_LOW_RES_3.ordinal()];
	}
	
	public static boolean[][] getTiles(final float theta, final int k, final int width, final int height, final int displayImageCenterX, final int displayImageCenterY, final int tx, final int ty, final int l_dx, final int l_dy) {
		
		boolean[][] tiles = new boolean[k][k];
		
		Log.d("TILE3", "Improved TILES:");
		Log.d("TILE3", "--------------------------------");
		
		int counter = 0;
		for(int i=0 ; i<k; i++) {
			String line = "";
			for(int j=0 ; j<k; j++) {
				tiles[i][j] = tileIsinside(theta, i, j, k, width, height, displayImageCenterX, displayImageCenterY, tx, ty, l_dx, l_dy);
				
				
				if(tiles[i][j]) {
					counter ++;
					//Log.d("TILE2", "Tile:  " + i + "  " + j + "   inside.");
					line = line + "x";
				} else {
					line = line + "-";
				}
			}
			Log.d("TILE3", line);
		}
		Log.d("TILE3", "--------------------------------");
		
		Log.d("TILE3", "Number of  tiles: " + counter);
		return tiles;
	}
	
	
	public static boolean tileIsinside(float theta, final int i, final int j, final int k, final int width, final int height, final int displayImageCenterX, final int displayImageCenterY, final int tx, final int ty, final int l_dx, final int l_dy) {	

		
		final int vk = width/k;
		final int hk = height/k;
				

		final int ulx = i * vk 			- displayImageCenterX;
		final int uly = j * hk 			- displayImageCenterY;
		
		final int urx = (i+1) * vk 		- displayImageCenterX;
		final int ury = j * hk 			- displayImageCenterY;
		
		final int llx = i * vk 			- displayImageCenterX;
		final int lly = (j+1) * hk 		- displayImageCenterY;
		
		final int lrx = (i+1) * vk 		- displayImageCenterX;
		final int lry = (j+1) * hk 		- displayImageCenterY;
			
		
		// Tile corners rotated
		final float ulxr = rotateX(ulx, uly, theta);
		final float ulyr = rotateY(ulx, uly, theta);
		
		final float urxr = rotateX(urx, ury, theta);
		final float uryr = rotateY(urx, ury, theta);
		
		final float llxr = rotateX(llx, lly, theta);
		final float llyr = rotateY(llx, lly, theta);
		
		final float lrxr = rotateX(lrx, lry, theta);
		final float lryr = rotateY(lrx, lry, theta);
		
		
		
		final int halfx = (int) ((screenWidth / 2) * (bg.ppix / xdpi));
		final int halfy = (int) ((screenHeight / 2) * (bg.ppiy / ydpi));

		
		return 	inside(ulxr, ulyr, halfx, halfy)   ||
				inside(urxr, uryr, halfx, halfy)   ||
				inside(llxr, llyr, halfx, halfy)   ||
				inside(lrxr, lryr, halfx, halfy)   ;
		
		
	}
	
	
	public static float rotateX(final float x, final float y, final float theta) {
		return (float) (x * Math.cos(theta * Math.PI / 180) -  y * Math.sin(theta * Math.PI / 180));
	}
	
	
	public static float rotateY(final float x, final float y, final float theta) {
		return (float) (x * Math.sin(theta * Math.PI / 180) + y * Math.cos(theta * Math.PI / 180));
	}

	
	public static boolean inside(final float x, final float y, final int halfx, final int halfy) {
		return   -halfx <= x  &&  x <= halfx	 && 
				 -halfy <= y  &&  y <= halfy	;
	}

	
	public static void tileBitmap(final int tx, final int ty, final int displayImageCenterX, final int displayImageCenterY, final float theta, final int l_dx, final int l_dy) {
		
		// TODO: Create Picture class and PictureHandler 
		final String file = bg.file; // "coordinates.png";
		final int width = bg.width; // 16540; // Image width
		final int height = bg.height; //16540; // Image height

		// TODO: Set number of threads with regards to number available
		final int k = 60;
		
		Thread[] threads = new Thread[k];
		Bitmap[][] bitmaps = new Bitmap[k][k];
	
		final int vk = width/k;
		final int hk = height/k;
		
		//final int centerX = width / 2;  // Center point of image
		//final int centerY = height / 2; // Center point of image
		
		// Determines the view, is pixel values of bounding box
		final int lowerX = displayImageCenterX - l_half;
		final int lowerY = displayImageCenterY - l_half; 
		
		final int higherX = displayImageCenterX + l_half;
		final int higherY = displayImageCenterY + l_half;
		
		/*
		final int lowerX = centerX - l_half;
		final int lowerY = height - centerY - l_half; // Seen from lower left corner
		
		final int higherX = centerX + l_half;
		final int higherY = height - centerY + l_half; // Seen from lower left corner
		*/
		
		boolean[][] tiles = getTiles(theta, k, width, height, displayImageCenterX, displayImageCenterY, tx, ty, l_dx, l_dy);
		
		// TODO: Optimize number of tiles with regards to the rotation angle
		// Get lower bounds
		int a = 0;
		int b = 0;
		
		do {
			if( (a+1) * vk > lowerX ) {
				break;
			}
			a++;
		} while(a < k);
		
		do {
			if( (b+1) * hk > lowerY ) {
				break;
			}
			b++;
		} while(b < k);
		
		// Get higher bounds
		int c = k;
		int d = k;
		
		do {
			c--;
			if(c * vk < higherX) {
				break;
			}
		} while(c > 0);
		
		do {
			d--;
			if(d * hk < higherY) {
				break;
			}
		} while(d > 0);
		
		
		Log.d("TILE", "a = " + a);
		Log.d("TILE", "b = " + b);
		Log.d("TILE", "c = " + c);
		Log.d("TILE", "d = " + d);
	
		
		// TODO: Make functions for these
		final int rb = b;
		final int eb = d + 1;
		
		final int kb = a;
		final int ke = c + 1;
		
		final int counter = (eb - rb) * (ke - kb);
	
		
		Log.d("TILE3", "Regular TILES:");
		Log.d("TILE3", "--------------------------------");
		for (int i = 0; i < k; i++) {
			String line = "";
			for (int j = 0; j < k; j++) {
				if(i >= rb && i < eb && j >= kb && j < ke) {
					line = line + "x";
				} else {
					line = line + "-";
				}
			}	
			Log.d("TILE3", line);
		}
		Log.d("TILE3", "--------------------------------");
		Log.d("TILE3", "Number of  tiles: " + counter);
		
		
		// TODO: Split up into threads getting a number of tiles instead of threads getting a whole row
		for (int i = rb; i < eb; i++) {
			threads[i] = new Thread(new Row(i, bitmaps[i], vk, hk, file, kb, ke));
			threads[i].start();
		}
		
		
		int nw;
		int nh;
		
		for (int i = rb; i < eb; i++) {
			try {
				threads[i].join();
				for (int j = kb; j < ke; j++) {
					nw = (j*vk);
				    nh = (i*hk);
				    	//backgroundCanvas.drawBitmap(bitmaps[i][j], nw + tx, nh + ty, new Paint());
				  /*  Paint pa = new Paint();
				    pa.setColor(Color.CYAN);	
				    pa.setAlpha(20);
				    backgroundCanvas.drawRect(nw + tx, nh + ty, bitmaps[i][j].getWidth(), bitmaps[i][j].getHeight(), pa);
				    
				    pa.setColor(Color.MAGENTA);
				    pa.setStrokeWidth(6);
				    pa.setAlpha(255);
				    backgroundCanvas.drawPoint(nw + tx, nh + ty, pa);
				    */
				    if(includeTile[i][j]) {
				    	backgroundCanvas.drawBitmap(bitmaps[i][j], nw + tx, nh + ty, new Paint());
				    }
				    
				    
				}				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
				
		
	}
	

	public static class Row implements Runnable {
		
		final int i;
		
		final int kb;
		final int ke;
		
		final int vk;
		final int hk;
		
		final String file;
		
		Bitmap[] data;
		
		
		Row(int i, Bitmap[] data, int vk, int hk, String file, int kb, int ke) {
			this.i = i;
			this.kb = kb;
			this.ke = ke;
			this.data = data;
			this.vk = vk;
			this.hk = hk;
			this.file = file;
		}
		
		
		public void run() {
			
			int nw;
			int nh;
			
			AssetManager assets = AndroidGraphics.getAssets();
			InputStream istream = null;
			BitmapRegionDecoder decoder = null;
			
			try {
				istream = assets.open(file);
	        	decoder = BitmapRegionDecoder.newInstance(istream, false);
	        } catch (IOException e) {
	        	Log.d("TILE", "FAIL");
	        	e.printStackTrace();
	        }
			
		    for (int j = kb; j < ke; j++) {
			    nw = (j*vk);
			    nh = (i*hk);
			    data[j] = decoder.decodeRegion(new Rect(nw,nh, (nw+vk),(nh+hk)), null);
			    Log.d("TILE", "i, j = " + i + "  ,  " + j);
		    } 
		    
		}
		
		
	}
	
	
	public static Bitmap RotateBitmap(Bitmap source, float angle, float px, float py)
	{
	      Matrix matrix = new Matrix();
	      matrix.postRotate(angle, px, py);
	      return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
	
	
	// TODO: Fix in a better way, this is temporary :)
	static boolean once = true;
	
	public static void setOnce() {
		once = true;
	}
	
	
	
	public boolean isInside(final float x, final float y, final int halfx, final int halfy) {
		return   -halfx <= x  &&  x <= halfx	 && 
				 -halfy <= y  &&  y <= halfy	;
	}
	
	
	public void drawBackground(Graphics graphics) {

		Canvas canvas = graphics.getCanvas();
		
		if(once) {
		
			final float ppix = bg.ppix;
			final float ppiy = bg.ppiy;
			final int width = bg.width; 
			final int height = bg.height;


			final float angle = SharedVariables.getInstance().getDeviceAngle();
			
			final float g_device_midX = SharedVariables.getInstance().getDeviceMiddleX();
			final float g_device_midY = SharedVariables.getInstance().getDeviceMiddleY();
			
			final float g_main_midX = SharedVariables.getInstance().getMainMiddleX();
			final float g_main_midY = SharedVariables.getInstance().getMainMiddleY();
					 
			
			final float g_dx = g_device_midX - g_main_midX;
			final float g_dy = - (g_device_midY - g_main_midY);
			
			final int l_dx = (int) (g_dx * ppix);
			final int l_dy = (int) (g_dy * ppiy);
			
			final int displayImageCenterX = width/2  + l_dx;  // Pixel coordinates of image
			final int displayImageCenterY = height/2 + l_dy; // Pixel coordinates of image
			
		
			final int nRader = 60;
			final int nKolumner = 60;
			
			includeTile = new boolean[nRader][nKolumner];
			
			for(int rad=0; rad<nRader; rad++) {
				for(int kolumn=0; kolumn<nKolumner; kolumn++) {
					includeTile[rad][kolumn] = false;
				}
			}
			
			final int widthStep = width/nKolumner;
			final int heightStep = height/nRader;
			
			final int halfx = (int) ((screenWidth / 2) * (bg.ppix / xdpi));
			final int halfy = (int) ((screenHeight / 2) * (bg.ppiy / ydpi));
			
			Log.d("TILE6", "oooooooo NEW MAP ooooooooo");
			
			int counter = 0;
			for(int rad=0; rad<nRader; rad++) {
				String line = "";
				for(int kolumn=0; kolumn<nKolumner; kolumn++) {
					
					float upperLeftCornerX = kolumn * widthStep;
					float upperLeftCornerY = rad * heightStep;
					
					float upperRightCornerX = (kolumn + 1) * widthStep;
					float upperRightCornerY = rad * heightStep;
					
					float lowerLeftCornerX = kolumn * widthStep;
					float lowerLeftCornerY = (rad + 1) * heightStep;
					
					float lowerRightCornerX = (kolumn + 1) * widthStep;
					float lowerRightCornerY = (rad + 1) * heightStep;
					
					
					float upperLeftCornerXTranslated =  upperLeftCornerX - displayImageCenterX;
					float upperRightCornerXTranslated = upperRightCornerX - displayImageCenterX;
					float lowerLeftCornerXTranslated = lowerLeftCornerX - displayImageCenterX;
					float lowerRightCornerXTranslated = lowerRightCornerX - displayImageCenterX;
					
					float upperLeftCornerYTranslated = upperLeftCornerY - displayImageCenterY;
					float upperRightCornerYTranslated = upperRightCornerY - displayImageCenterY;
					float lowerLeftCornerYTranslated = lowerLeftCornerY - displayImageCenterY;
					float lowerRightCornerYTranslated = lowerRightCornerY - displayImageCenterY;
					
					
					float upperLeftCornerXRotated = rotateX(upperLeftCornerXTranslated, upperLeftCornerYTranslated, angle);
					float upperRightCornerXRotated = rotateX(upperRightCornerXTranslated, upperRightCornerYTranslated, angle);
					float lowerLeftCornerXRotated = rotateX(lowerLeftCornerXTranslated, lowerLeftCornerYTranslated, angle);
					float lowerRightCornerXRotated = rotateX(lowerRightCornerXTranslated, lowerRightCornerYTranslated, angle); 
					
					float upperLeftCornerYRotated = rotateY(upperLeftCornerXTranslated, upperLeftCornerYTranslated, angle);
					float upperRightCornerYRotated = rotateY(upperRightCornerXTranslated, upperRightCornerYTranslated, angle);
					float lowerLeftCornerYRotated = rotateY(lowerLeftCornerXTranslated, lowerLeftCornerYTranslated, angle); 
					float lowerRightCornerYRotated = rotateY(lowerRightCornerXTranslated, lowerRightCornerYTranslated, angle);
					
					
					if( isInside(upperLeftCornerXRotated, upperLeftCornerYRotated, halfx, halfy) 		||
						isInside(upperRightCornerXRotated, upperRightCornerYRotated, halfx, halfy) 		||
						isInside(lowerLeftCornerXRotated, lowerLeftCornerYRotated, halfx, halfy) 		||
						isInside(lowerRightCornerXRotated, lowerRightCornerYRotated, halfx, halfy) 		) {
						// The tile should be included.
						includeTile[rad][kolumn] = true;
						line = line + "x";
						counter ++;
					} else {
						line = line + "-";
					}
	
				}
				Log.d("TILE6", line);
			}
			
			Log.d("TILE6", "Counter = " + counter);
			
			
			
			l_half = (int) Math.ceil((Math.sqrt( Math.pow(screenWidth * ppix / xdpi, 2) + Math.pow(screenHeight * ppiy / ydpi, 2)) / 2));
			l_side = 2 * l_half;
			
			backgroundTiled = Bitmap.createBitmap(l_side,  l_side, Bitmap.Config.ARGB_8888);
			backgroundFinal = Bitmap.createBitmap(screenWidth,  screenHeight, Bitmap.Config.ARGB_8888);
			
			
			backgroundCanvas = new Canvas(backgroundTiled);
			

			
			// Rotation angle around the center point of the desired view
			final float theta = angle; 
			
			
			final int tix = - (l_half - screenWidth/2);  // Tile image translation in x
			final int tiy = - (l_half - screenHeight/2); // Tile image translation in y
			
			final int tx = - (displayImageCenterX - screenWidth/2) - tix;  // Tiles translation in x
			final int ty = - (displayImageCenterY - screenHeight/2) - tiy; // Tiles translation in y
			
			
			tileBitmap(tx, ty, displayImageCenterX, displayImageCenterY, theta, l_dx, l_dy); // Tile the background image
			
			canvas.save();
			
			canvas.drawColor(Color.WHITE);
			
			// Rotate around screen center
			canvas.translate(screenWidth/2, screenHeight/2);
			canvas.rotate(theta);
			canvas.scale(xdpi/ppix , ydpi/ppiy);
			canvas.translate(-screenWidth/2, -screenHeight/2);
			canvas.drawBitmap(backgroundTiled, tix, tiy, new Paint()); // Draw to the current frame buffer
	
			canvas.restore();
			
			// Mark out screen center
			Paint redPaint = new Paint();
			redPaint.setColor(Color.RED);
			canvas.drawCircle(screenWidth/2, screenHeight/2, 15, redPaint); // Draw to the current frame buffer
			
			// Get and store the rotated background from the current frame buffer
			backgroundFinal = Bitmap.createBitmap( graphics.getFrameBuffer() );
	         
			
			
			once = false;
		} else {
			canvas.drawBitmap(backgroundFinal, 0, 0, graphics.getPaint());
		}
		
	}
	
	

}
