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
			
	private final int screenHeight;
	private final int screenWidth;
	
	public final float xdpi;
	public final float ydpi;
	private final float densityDpi;
	private final float density;
	
	private static int l_half;
	private static int l_side;
	
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
	
	
	public static void tileBitmap(final int tx, final int ty, final int displayImageCenterX, final int displayImageCenterY) {
		
		// TODO: Create Picture class and PictureHandler 
		final String file = bg.file; // "coordinates.png";
		final int width = bg.width; // 16540; // Image width
		final int height = bg.height; //16540; // Image height

		// TODO: Set number of threads with regards to number available
		final int k = 64;
		
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
				    backgroundCanvas.drawBitmap(bitmaps[i][j], nw + tx, nh + ty, new Paint());
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
	
	
	public void drawBackground(Graphics graphics) {

		Canvas canvas = graphics.getCanvas();
		
		if(once) {
		
			final float ppix = bg.ppix; // 295.35714285714285714285714285714f; // Image ppi
			final float ppiy = bg.ppiy; // 295.35714285714285714285714285714f; // Image ppi
			final int width = bg.width; // 16540; // Image width
			final int height = bg.height; // 16540; // Image height


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
			
			Log.d("TILE", "angle = " + angle);
			
			Log.d("TILE", "g_device_midX = " + g_device_midX);
			Log.d("TILE", "g_device_midY = " + g_device_midY);
			
			Log.d("TILE", "g_main_midX = " + g_main_midX);
			Log.d("TILE", "g_main_midY = " + g_main_midY);
			
			
			Log.d("TILE", "g_dx = " + g_dx);
			Log.d("TILE", "g_dy = " + g_dy);
			Log.d("TILE", "l_dx = " + l_dx);
			Log.d("TILE", "l_dy = " + l_dy);
			
			Log.d("TILE", "displayImageCenterX = " + displayImageCenterX);
			Log.d("TILE", "displayImageCenterY = " + displayImageCenterY);
			
		
			
			l_half = (int) Math.ceil((Math.sqrt( Math.pow(screenHeight * ppix / xdpi, 2) + Math.pow(screenWidth * ppiy / ydpi, 2)) / 2));
			l_side = 2 * l_half;
			
			backgroundTiled = Bitmap.createBitmap(l_side,  l_side, Bitmap.Config.ARGB_8888);
			backgroundFinal = Bitmap.createBitmap(screenWidth,  screenHeight, Bitmap.Config.ARGB_8888);
			
			
			backgroundCanvas = new Canvas(backgroundTiled);
			
			//final int displayImageCenterX = 2522;
			//final int displayImageCenterY = 2003;
			
			// The center point of the desired view
		// final int displayImageCenterX = 16540;   // Pixel coordinates of image
		// final int displayImageCenterY = 16540/2; // Pixel coordinates of image
			
			// Rotation angle around the center point of the desired view
			final float theta = angle; 

			
			final int tix = - (l_half - screenWidth/2);  // Tile image translation in x
			final int tiy = - (l_half - screenHeight/2); // Tile image translation in y
			
			final int tx = - (displayImageCenterX - screenWidth/2) - tix;  // Tiles translation in x
			final int ty = - (displayImageCenterY - screenHeight/2) - tiy; // Tiles translation in y

			
			tileBitmap(tx, ty, displayImageCenterX, displayImageCenterY); // Tile the background image
			
			canvas.save();
			
			canvas.drawColor(Color.WHITE);
			
			// Rotate around screen center
			canvas.translate(screenWidth/2, screenHeight/2);
			canvas.rotate(theta);
			canvas.scale(xdpi/ppix, ydpi/ppiy);
			canvas.translate(-screenWidth/2, -screenHeight/2);
			canvas.drawBitmap(backgroundTiled, tix, tiy, new Paint()); // Draw to the current frame buffer
	
			canvas.restore();
			
			// Mark out screen center
			Paint redPaint = new Paint();
			redPaint.setColor(Color.RED);
			canvas.drawCircle(screenWidth/2, screenHeight/2, 15, redPaint); // Draw to the current frame buffer
			
			// Get and store the rotated background from the current frame buffer
			backgroundFinal = Bitmap.createBitmap( graphics.getFrameBuffer() );
			
			
			Log.d("TILE", "tix = " + tix);
			Log.d("TILE", "tiy = " + tiy);
	         
			once = false;
		} else {
			canvas.drawBitmap(backgroundFinal, 0, 0, graphics.getPaint());
		}
		
	}
	
	

}
