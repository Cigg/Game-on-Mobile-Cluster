package com.pussycat.minions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import android.util.Config;
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
	
	private static Background bg;
	
	final int nRader = 32;
	final int nKolumner = 32;
	Bitmap[][] bitmaps = new Bitmap[nRader][nKolumner];
	final int numberOfExecutionThreads = 4;
	
	
	public Device() {
		this.screenHeight = PussycatMinions.getScreenHeight();
		this.screenWidth = PussycatMinions.getScreenWidth();
		
		this.densityDpi = PussycatMinions.getDensityDPI();
		this.density = PussycatMinions.getDensity();
		this.xdpi = PussycatMinions.getXDPI();
		this.ydpi = PussycatMinions.getYDPI();
		
		BackgroundHandler bgh = new BackgroundHandler();
		this.bg = bgh.backgrounds[BACKGROUNDS.COORDINATES.ordinal()];
		
		
		
		l_half = (int) Math.ceil((Math.sqrt( Math.pow(screenWidth * bg.ppix / xdpi, 2) + Math.pow(screenHeight * bg.ppiy / ydpi, 2)) / 2));
		l_side = 2 * l_half;
		backgroundTiled = Bitmap.createBitmap(l_side,  l_side, Bitmap.Config.ARGB_8888);
		backgroundFinal = Bitmap.createBitmap(screenWidth,  screenHeight, Bitmap.Config.ARGB_8888);
		backgroundCanvas = new Canvas(backgroundTiled);
	}
	
	public static float rotateX(final float x, final float y, final double theta) {
		return (float) (x * Math.cos(theta) -  y * Math.sin(theta));
	}
	
	
	public static float rotateY(final float x, final float y, final double theta) {
		return (float) (x * Math.sin(theta) + y * Math.cos(theta));
	}
	
	
	public static double radiansToDegrees(float radians) {
		return radians * Math.PI / 180;
	}
	
	
	// TODO: Fix in a better way, this is temporary :)
	static boolean once = true;
	
	public static void setOnce() {
		once = true;
	}
	
	
	public boolean isPointInsideRectangle(final float x, final float y, final int halfx, final int halfy) {
		return   -halfx <= x  &&  x <= halfx	 && 
				 -halfy <= y  &&  y <= halfy	;
	}
	
	public class IndexPair {
		
		public final int rad;
		public final int kolumn;
		
		public IndexPair(final int rad, final int kolumn) {
			this.rad = rad;
			this.kolumn = kolumn;
		}
	}

	public static class Executor implements Runnable {
		

		final String file;
		Bitmap[][] data;
		public ArrayList<IndexPair> indexPairs = new ArrayList<IndexPair>();
		
		int vk;
		int hk;
		
		public Executor(Bitmap[][] data, ArrayList<IndexPair> indexPairs, String file, int vk, int hk) {
			this.data = data;
			this.indexPairs = indexPairs;
			this.file = file;
			this.vk = vk;
			this.hk = hk;
		}
		
		public void run() {
			

			AssetManager assets = AndroidGraphics.getAssets();
			InputStream istream = null;
			//BitmapRegionDecoder decoder = null;
			
			/*
			try {
				istream = assets.open(file);
	        	decoder = BitmapRegionDecoder.newInstance(istream, false);
	        } catch (IOException e) {
	        	Log.d("TILE", "FAIL");
	        	e.printStackTrace();
	        }
			*/
			
			//int nw;
			//int nh;
			
			for(IndexPair indexPair : indexPairs) {
				//nw = (indexPair.kolumn * vk);
			    //nh = (indexPair.rad * hk);
			    //data[indexPair.rad][indexPair.kolumn] = decoder.decodeRegion(new Rect(nw,nh, (nw+vk),(nh+hk)), null);
			    
				
	
			    String fileName = "Coordinates_32x32" + File.separator + "Coordinates_" + indexPair.rad + "_" + indexPair.kolumn + ".png";
			    
			    try {
					istream = assets.open(fileName);
		        
		        } catch (IOException e) {
		        	Log.d("TILE", "FAIL");
		        	e.printStackTrace();
		        }
				
			    data[indexPair.rad][indexPair.kolumn] = BitmapFactory.decodeStream(istream);
			    Log.d("TILE", "i, j = " + indexPair.rad + "  ,  " + indexPair.kolumn);

			    
			    /*
			    
			    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			    data[indexPair.rad][indexPair.kolumn].compress(Bitmap.CompressFormat.PNG, 100, bytes);
			    
			   
			    String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + fileName;
			    File f = new File(dir);
			    
			    Log.d("TILE7", "DIR: " + dir);
			    
			    try {
					f.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.d("TILE7", "ERROR f.createNewFile()");
					e.printStackTrace();
				}

			    FileOutputStream fo = null;
				try {
					fo = new FileOutputStream(f);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					Log.d("TILE7", "ERROR fo = new FileOutputStream(f);");
					e.printStackTrace();
				}
			    try {
					fo.write(bytes.toByteArray());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.d("TILE7", "ERROR fo.write(bytes.toByteArray());");
					e.printStackTrace();
				}

			    try {
					fo.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.d("TILE7", "ERROR 	fo.close();");
					e.printStackTrace();
				}	    
				    
				*/

			}
			
		    
		}
		
		
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
			
			final int widthStep = width/nKolumner;
			final int heightStep = height/nRader;
			
			final int halfx = (int) ((screenWidth / 2) * (bg.ppix / xdpi));
			final int halfy = (int) ((screenHeight / 2) * (bg.ppiy / ydpi));
			
			Log.d("TILE6", "oooooooo NEW MAP ooooooooo");
			
			ArrayList<IndexPair> indexes = new ArrayList<IndexPair>();
			
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
					
					float upperLeftCornerXRotated = rotateX(upperLeftCornerXTranslated, upperLeftCornerYTranslated, radiansToDegrees(angle));
					float upperRightCornerXRotated = rotateX(upperRightCornerXTranslated, upperRightCornerYTranslated, radiansToDegrees(angle));
					float lowerLeftCornerXRotated = rotateX(lowerLeftCornerXTranslated, lowerLeftCornerYTranslated, radiansToDegrees(angle));
					float lowerRightCornerXRotated = rotateX(lowerRightCornerXTranslated, lowerRightCornerYTranslated, radiansToDegrees(angle)); 
					
					float upperLeftCornerYRotated = rotateY(upperLeftCornerXTranslated, upperLeftCornerYTranslated, radiansToDegrees(angle));
					float upperRightCornerYRotated = rotateY(upperRightCornerXTranslated, upperRightCornerYTranslated, radiansToDegrees(angle));
					float lowerLeftCornerYRotated = rotateY(lowerLeftCornerXTranslated, lowerLeftCornerYTranslated, radiansToDegrees(angle)); 
					float lowerRightCornerYRotated = rotateY(lowerRightCornerXTranslated, lowerRightCornerYTranslated, radiansToDegrees(angle));
					
					if( isPointInsideRectangle(upperLeftCornerXRotated, upperLeftCornerYRotated, halfx, halfy) 		||
						isPointInsideRectangle(upperRightCornerXRotated, upperRightCornerYRotated, halfx, halfy) 	||
						isPointInsideRectangle(lowerLeftCornerXRotated, lowerLeftCornerYRotated, halfx, halfy) 		||
						isPointInsideRectangle(lowerRightCornerXRotated, lowerRightCornerYRotated, halfx, halfy) 			) {
						
						indexes.add(new IndexPair(rad, kolumn));
						
						line = line + "x";
						counter ++;
					} else {
						line = line + "-";
					}
	
				}
				Log.d("TILE6", line);
			}
			
			Log.d("TILE6", "Counter = " + counter);
			
			backgroundCanvas.drawColor(Color.WHITE);
			
			final int tix = - l_half + screenWidth/2;  // Tile image translation in x
			final int tiy = - l_half + screenHeight/2; // Tile image translation in y
			
			final int tx = - displayImageCenterX  + l_half;  // Tiles translation in x
			final int ty = - displayImageCenterY  + l_half; // Tiles translation in y
			
			
			Thread[] threads = new Thread[numberOfExecutionThreads];
			final int numberOfTilesForEachExecutionThread = counter / numberOfExecutionThreads;
			final int first = numberOfTilesForEachExecutionThread + counter % numberOfExecutionThreads;
			
		
			ArrayList<IndexPair> temp = new ArrayList<IndexPair>();
			Log.d("TILE6", "numberOfExecutionThreads: " + numberOfExecutionThreads);
			Log.d("TILE6", "numberOfTilesForEachExecutionThread: " + numberOfTilesForEachExecutionThread);
			Log.d("TILE6", "first: " + first);
			
			
			int tot = first + (numberOfExecutionThreads-1) *  numberOfTilesForEachExecutionThread;
			
			Log.d("TILE6", "TOTAL NR: " + tot );
			int indexLast = 0;
			for(int i=0; i<first; i++) {
				Log.d("TILE6", "IND: " + i );
				temp.add(indexes.get(i));
			}
			
			indexLast = first;
			threads[0] = new Thread(new Executor(bitmaps, temp, bg.file, widthStep, heightStep));
			threads[0].start();
			
			for(int i=1; i<numberOfExecutionThreads; i++) {
				ArrayList<IndexPair> temp2 = new ArrayList<IndexPair>();
				for(int j=indexLast; j< indexLast + numberOfTilesForEachExecutionThread; j++) {
					Log.d("TILE6", "IND: " + j );
					temp2.add(indexes.get(j));
					
				}
				indexLast += numberOfTilesForEachExecutionThread;
				threads[i] = new Thread(new Executor(bitmaps, temp2, bg.file, widthStep, heightStep));
				threads[i].start();
			}
		
		
			for (int i=0; i<numberOfExecutionThreads; i++) {
				try {
					threads[i].join();	
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			int a = 0;
			int nw;
			int nh;
			for(IndexPair indexPair : indexes ) {
				Log.d("TILE6", "INTE: " + a );
				a++;
				nw = (indexPair.kolumn * widthStep);
			    nh = (indexPair.rad * heightStep);
			    
				backgroundCanvas.drawBitmap(bitmaps[indexPair.rad][indexPair.kolumn], nw + tx, nh + ty, new Paint());
				bitmaps[indexPair.rad][indexPair.kolumn].recycle();
			}
				
			
			canvas.save();
			
			canvas.drawColor(Color.WHITE);
			
			// Rotate around screen center
			canvas.translate(screenWidth/2, screenHeight/2);
			canvas.rotate(angle);
			canvas.scale(xdpi/ppix , ydpi/ppiy);
			canvas.translate(-screenWidth/2, -screenHeight/2);
			canvas.drawBitmap(backgroundTiled, tix, tiy, new Paint()); // Draw to the current frame buffer
	
			canvas.restore();
			
			// Mark out screen center
			Paint redPaint = new Paint();
			redPaint.setColor(Color.RED);
			canvas.drawCircle(screenWidth/2, screenHeight/2, 15, redPaint); // Draw to the current frame buffer
			
			// Get and store the rotated background from the current frame buffer
			backgroundFinal.recycle();
			backgroundFinal = Bitmap.createBitmap( graphics.getFrameBuffer() );
			
			once = false;
		} else {
			canvas.drawBitmap(backgroundFinal, 0, 0, graphics.getPaint());
		}
		
	}
	
	

}
