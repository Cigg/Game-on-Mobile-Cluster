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
	
	final int nRader;
	final int nKolumner;
	Bitmap[][] bitmaps;
	final int numberOfExecutionThreads = 4;
	
	
	public Device() {
		this.screenHeight = PussycatMinions.getScreenHeight();
		this.screenWidth = PussycatMinions.getScreenWidth();
		
		this.densityDpi = PussycatMinions.getDensityDPI();
		this.density = PussycatMinions.getDensity();
		this.xdpi = PussycatMinions.getXDPI();
		this.ydpi = PussycatMinions.getYDPI();
		
		BackgroundHandler bgh = new BackgroundHandler();
		this.bg = bgh.backgrounds[BACKGROUNDS.WEB.ordinal()];
		
		nRader = bg.nRows;
		nKolumner = bg.nCols;
		bitmaps = new Bitmap[nRader][nKolumner];

		
		
		
		l_half = (int) Math.ceil( (Math.sqrt( Math.pow(screenWidth * bg.ppix / xdpi, 2) + Math.pow(screenHeight * bg.ppiy / ydpi, 2)) / 2) );
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
	
	
	public static double radiansToDegrees(final double radians) {
		return radians * Math.PI / 180;
	}
	
	
	// TODO: Fix in a better way, this is temporary :) - everything is temporary
	static boolean once = true;
	
	public static void setOnce() {
		once = true;
	}
	
	
	static int insider = 0;
	public boolean pointIsInsideRectangle(final float x, final float y, final int halfx, final int halfy) {
		boolean inside = -halfx <= x  &&  x <= halfx	 && 
						 -halfy <= y  &&  y <= halfy	;
		
		if(inside) {
			insider++;
			Log.d("LINE", "INSIDE: " + insider);
		}
		return inside;
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
			    
				
	
			   // String fileName = "Coordinates_split" + File.separator + "Coordinates_" + indexPair.rad + "_" + indexPair.kolumn + ".png";
				
				String fileName = bg.folder + File.separator + bg.fileName + indexPair.rad + "_" + indexPair.kolumn + bg.ending;
			    
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
			final Rectangle rect = new Rectangle(halfx, halfy);
			
			Log.d("TILE6", "oooooooo NEW MAP ooooooooo");
			
			ArrayList<IndexPair> indexes = new ArrayList<IndexPair>();	
			
			final int lowerX = displayImageCenterX - l_half;
			final int lowerY = displayImageCenterY - l_half; 
			
			final int higherX = displayImageCenterX + l_half;
			final int higherY = displayImageCenterY + l_half;
			
			
			int a = 0;
			int b = 0;
			
			do {
				if( (a+1) * widthStep > lowerX ) {
					break;
				}
				a++;
			} while(a < nKolumner);
			
			do {
				if( (b+1) * heightStep > lowerY ) {
					break;
				}
				b++;
			} while(b < nRader);
			

			int c = nKolumner;
			int d = nRader;
			
			do {
				c--;
				if(c * widthStep < higherX) {
					break;
				}
			} while(c > 0);
			
			do {
				d--;
				if(d * heightStep < higherY) {
					break;
				}
			} while(d > 0);
			
			
			// TODO: Make functions for these
			final int rb = b;
			final int eb = d + 1;
			
			final int kb = a;
			final int ke = c + 1;
			
	
			int counter = 0;
			for(int rad=rb; rad<eb; rad++) {
				String line = "";
				for(int kolumn=kb; kolumn<ke; kolumn++) {
					
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
					
					
					if(	pointIsInsideRectangle(upperLeftCornerXRotated, upperLeftCornerYRotated, halfx, halfy) 		||
						pointIsInsideRectangle(upperRightCornerXRotated, upperRightCornerYRotated, halfx, halfy) 	||
						pointIsInsideRectangle(lowerLeftCornerXRotated, lowerLeftCornerYRotated, halfx, halfy) 		||
						pointIsInsideRectangle(lowerRightCornerXRotated, lowerRightCornerYRotated, halfx, halfy) 	||
						LineIntersectsRect(new Point(upperLeftCornerXRotated, upperLeftCornerYRotated), new Point(upperRightCornerXRotated, upperRightCornerYRotated), rect) ||
						LineIntersectsRect(new Point(upperRightCornerXRotated, upperRightCornerYRotated), new Point(lowerRightCornerXRotated, lowerRightCornerYRotated), rect) ||
						LineIntersectsRect(new Point(lowerRightCornerXRotated, lowerRightCornerYRotated), new Point(lowerLeftCornerXRotated, lowerLeftCornerYRotated), rect) ||
						LineIntersectsRect(new Point(lowerLeftCornerXRotated, lowerLeftCornerYRotated), new Point(upperLeftCornerXRotated, upperLeftCornerYRotated), rect)) {
						
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
			
			final int tix = screenWidth/2 - l_half;  // Tile image translation in x
			final int tiy = screenHeight/2 - l_half; // Tile image translation in y
			
			final int tx = - displayImageCenterX  + l_half;  // Tiles translation in x
			final int ty = - displayImageCenterY  + l_half; // Tiles translation in y
			
			
			Thread[] threads = new Thread[numberOfExecutionThreads];
			final int numberOfTilesForEachExecutionThread = counter / numberOfExecutionThreads;
			final int first = numberOfTilesForEachExecutionThread + counter % numberOfExecutionThreads;
			
		
			ArrayList<IndexPair> temp = new ArrayList<IndexPair>();
			Log.d("DEVICE", "TOTAL TILES: " + indexes.size());
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
			threads[0] = new Thread(new Executor(bitmaps, temp, bg.fileName, widthStep, heightStep));
			threads[0].start();
			
			for(int i=1; i<numberOfExecutionThreads; i++) {
				ArrayList<IndexPair> temp2 = new ArrayList<IndexPair>();
				for(int j=indexLast; j< indexLast + numberOfTilesForEachExecutionThread; j++) {
					Log.d("TILE6", "IND: " + j );
					temp2.add(indexes.get(j));
					
				}
				indexLast += numberOfTilesForEachExecutionThread;
				threads[i] = new Thread(new Executor(bitmaps, temp2, bg.fileName, widthStep, heightStep));
				threads[i].start();
			}
		
		
			for (int i=0; i<numberOfExecutionThreads; i++) {
				try {
					threads[i].join();	
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			int nw;
			int nh;
			for(IndexPair indexPair : indexes ) {
				nw = (indexPair.kolumn * widthStep);
			    nh = (indexPair.rad * heightStep);
			    
			    Log.d("FAIL", "indexPair.rad][indexPair.kolumn : " + indexPair.rad + "  " + indexPair.kolumn);
			    
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
			canvas.drawCircle(screenWidth/2, screenHeight/2, PussycatMinions.meters2Pixels(0.08f / 100.0f), redPaint); // Draw to the current frame buffer
			
			// Get and store the rotated background from the current frame buffer
			backgroundFinal.recycle();
			backgroundFinal = Bitmap.createBitmap( graphics.getFrameBuffer() );
			
			once = false;
		} else {
			canvas.drawBitmap(backgroundFinal, 0, 0, graphics.getPaint());
		}
		
	}

	
	private class Rectangle {
		
		private final Point upperLeft;
		private final Point upperRight;
		private final Point lowerRight;
		private final Point lowerLeft;
		
		private Rectangle(final float halfx, final float halfy) {
			upperRight = new Point(halfx, -halfy);
			lowerRight =  new Point(halfx, halfy);
			
			upperLeft = new Point(-halfx, -halfy);
			lowerLeft =  new Point(-halfx, halfy);
		}
	}
	
	
	private class Point {
		
		final float x;
		final float y;
		
		private Point(final float x, final float y) {
			this.x = x;
			this.y = y;
		}
	}
	
	private static int intersections = 0;
	private boolean LineIntersectsRect(Point p1, Point p2, final Rectangle rect)
    {
        boolean intersect =
               LineIntersectsLine(p1, p2, rect.upperRight, rect.lowerRight) ||
               LineIntersectsLine(p1, p2, rect.lowerRight, rect.lowerLeft) ||
               LineIntersectsLine(p1, p2, rect.lowerLeft, rect.upperLeft) ||
               LineIntersectsLine(p1, p2, rect.upperLeft, rect.upperRight);
        
        if(intersect) {
        	intersections++;
        	 Log.d("LINE", "LineIntersectsLine: " + intersections);
        }
       
        return intersect;
    }
	  
	private boolean LineIntersectsLine(Point l1p1, Point l1p2, Point l2p1, Point l2p2)
    {
        float q = (l1p1.y - l2p1.y) * (l2p2.x - l2p1.x) - (l1p1.x - l2p1.x) * (l2p2.y - l2p1.y);
        float d = (l1p2.x - l1p1.x) * (l2p2.y - l2p1.y) - (l1p2.y - l1p1.y) * (l2p2.x - l2p1.x);

        if( d == 0 )
        {
            return false;
        }

        float r = q / d;

        q = (l1p1.y - l2p1.y) * (l1p2.x - l1p1.x) - (l1p1.x - l2p1.x) * (l1p2.y - l1p1.y);
        float s = q / d;

        if( r < 0 || r > 1 || s < 0 || s > 1 )
        {
            return false;
        }

        return true;
    }
	
	

}
