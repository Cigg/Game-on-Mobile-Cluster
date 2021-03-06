package com.pussycat.framework.implementation;

import java.io.IOException;
import java.io.InputStream;

import android.R;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.pussycat.framework.Graphics;
import com.pussycat.framework.Image;
import com.pussycat.framework.Graphics.ImageFormat;
import com.pussycat.minions.PussycatMinions;

public class AndroidGraphics implements Graphics {
    static AssetManager assets;
    Bitmap frameBuffer;
    Canvas canvas;
    Paint paint;
    Rect srcRect = new Rect();
    Rect dstRect = new Rect();
    RectF srcRectF = new RectF();
    RectF dstRectF = new RectF();
    Matrix matrix = new Matrix();
    Bitmap bitmap;
    
    public AndroidGraphics(AssetManager assets, Bitmap frameBuffer) {
        this.assets = assets;
        this.frameBuffer = frameBuffer;
        this.canvas = new Canvas(frameBuffer);
        this.paint = new Paint();
    }

    public Bitmap getFrameBuffer() {
    	return this.frameBuffer;
    }
    
    public static AssetManager getAssets() {
    	return assets;
    }
    
    @Override
    public Image newImage(String fileName, ImageFormat format) {
        Config config = null;
        config = Config.ARGB_8888;
        Options options = new Options();
        options.inPreferredConfig = config;        
        
        InputStream in = null;
        Bitmap bitmap = null;
        try {
            in = assets.open(fileName);
            bitmap = BitmapFactory.decodeStream(in, null, options);
            if (bitmap == null)
                throw new RuntimeException("Couldn't load bitmap from asset '"
                        + fileName + "'");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load bitmap from asset '"
                    + fileName + "'");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        if (bitmap.getConfig() == Config.RGB_565)
            format = ImageFormat.RGB565;
        else if (bitmap.getConfig() == Config.ARGB_4444)
            format = ImageFormat.ARGB4444;
        else
            format = ImageFormat.ARGB8888;

        return new AndroidImage(bitmap, format);
    }
    
    @Override
    public Image newBackground(String fileName, ImageFormat format) {
    	
    	int imageHeight = 4000;
    	int imageWidth = 5000;
    	int screenHeight = PussycatMinions.getScreenHeight();
    	int screenWidth  = PussycatMinions.getScreenWidth();
    	
    	int xStart = (int) ((imageHeight - screenHeight)/2);
        int yStart = (int) ((imageWidth - screenWidth)/2);
        
    	Config config = null;
        config = Config.ARGB_8888;
        Options options = new Options();
        options.inPreferredConfig = config;
        
    	//Bitmap yourBitmap = Bitmap.createBitmap(fileName, xStart, yStart, screenWidth, screenHeight);		
       
                
        InputStream in = null;
        Bitmap bitmap = null;
       // Bitmap bitmapLarge = null;
        
        Rect rect = new Rect(yStart, xStart, yStart+screenWidth, xStart+screenHeight+50);
        
        int x1 = (int) (rect.left);
        int y1 = (int) (rect.top);
        int x2 = (int) (rect.left + rect.height());
        int y2 = (int) (rect.top + rect.width());
        
        float pts[] = new float[4];
        pts[0] = x1;
        pts[1] = y1;
        pts[2] = x2;
        pts[3] = y2;
        
        Matrix M = new Matrix();
       	M.setRotate(45);
        M.mapPoints(pts);
        System.out.println("Not rotated - x: " + x1 + "    y: " + y1);
        System.out.println("Rotated - x: " + pts[1] + "    y: " + pts[0]);
        //Rect rectRotated = new Rect((int) pts[1], (int)pts[0],(int)pts[3], (int)pts[2]);
        
        //Rect rotatedRect = rect*M;
        try {
            in = assets.open(fileName);
            	// bitmap = BitmapFactory.decodeStream(in, null, options);
				BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(in, false);
				bitmap = decoder.decodeRegion(rect, null);
	            // bitmapLarge = BitmapFactory.decodeStream(in, null, options);
				//Bitmap bitmap1 = BitmapFactory.decodeResource(null, );
				//bitmap = Bitmap.createBitmap(bitmap1, xStart, yStart, screenWidth, screenHeight, M, false);
            if (bitmap == null)
                throw new RuntimeException("Couldn't load bitmap from asset '"
                        + fileName + "'");
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load bitmap from asset '"
                    + fileName + "'");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        if (bitmap.getConfig() == Config.RGB_565)
            format = ImageFormat.RGB565;
        else if (bitmap.getConfig() == Config.ARGB_4444)
            format = ImageFormat.ARGB4444;
        else
            format = ImageFormat.ARGB8888;

        return new AndroidImage(bitmap, format);
    }
    
    public Image newScaledImage(Image image, int pixelWidth) {
		ImageFormat format;
        if (((AndroidImage)image).bitmap.getConfig() == Config.RGB_565)
            format = ImageFormat.RGB565;
        else if (((AndroidImage)image).bitmap.getConfig() == Config.ARGB_4444)
        	format = ImageFormat.ARGB4444;
        else
            format = ImageFormat.ARGB8888;
        
        int newWidth = pixelWidth;
        int newHeight = (int)(((float)newWidth/(float)image.getWidth())*(float)image.getHeight());
        
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(((AndroidImage)image).bitmap, newWidth, newHeight, false);
		
		return new AndroidImage(resizedBitmap, format);
    }
    
    public Image newScaledImage(Image image, int pixelWidth, int pixelHeight) {
		ImageFormat format;
        if (((AndroidImage)image).bitmap.getConfig() == Config.RGB_565)
            format = ImageFormat.RGB565;
        else if (((AndroidImage)image).bitmap.getConfig() == Config.ARGB_4444)
        	format = ImageFormat.ARGB4444;
        else
            format = ImageFormat.ARGB8888;
        
        int newWidth = pixelWidth;
        int newHeight = pixelHeight;
        
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(((AndroidImage)image).bitmap, newWidth, newHeight, false);
		
		return new AndroidImage(resizedBitmap, format);
    }
    
    
    @Override
    public void clearScreen(int color) {
        canvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8,
                (color & 0xff));
    }


    @Override
    public void drawLine(int x, int y, int x2, int y2, int color) {
        paint.setColor(color);
        canvas.drawLine(x, y, x2, y2, paint);
    }

    @Override
    public void drawRect(int x, int y, int width, int height, int color) {
        paint.setColor(color);
        paint.setStyle(Style.FILL);
        canvas.drawRect(x, y, x + width - 1, y + height - 1, paint);
    }
    
    @Override
    public void drawCircle(int x, int y, float r, int color){
    	paint.setColor(color);
    	paint.setStyle(Style.FILL);
    	canvas.drawCircle(x, y, r, paint);
    }
    
    @Override
    public void drawARGB(int a, int r, int g, int b) {
        paint.setStyle(Style.FILL);
       canvas.drawARGB(a, r, g, b);
    }
    
    @Override
    public void drawString(String text, int x, int y, Paint paint){
    	canvas.drawText(text, x, y, paint);

    	
    }
    

    public void drawImage(Image Image, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight) {
        srcRect.left = srcX;
        srcRect.top = srcY;
        srcRect.right = srcX + srcWidth;
        srcRect.bottom = srcY + srcHeight;
        
        
        dstRect.left = x;
        dstRect.top = y;
        dstRect.right = x + srcWidth;
        dstRect.bottom = y + srcHeight;

        canvas.drawBitmap(((AndroidImage) Image).bitmap, srcRect, dstRect,
                null);
    }
    
    @Override
    public void drawImage(Image Image, int x, int y) {
        canvas.drawBitmap(((AndroidImage)Image).bitmap, x, y, null);
    }
    
    public void drawScaledImage(Image Image, int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight, float angle){
	    	
		bitmap = ((AndroidImage) Image).bitmap;
		 
	    // calculate the scale
	    float scaleWidth = ((float) width) / srcWidth;
	    float scaleHeight = ((float) height) / srcHeight;
	   
	    // create a matrix for the manipulation
	    matrix.reset();
	    // resize the Bitmap
	    matrix.postScale(scaleWidth, scaleHeight);
	    // rotate the Bitmap
	    float angleInDegrees = angle*(180.0f/3.14f);
	    matrix.postRotate(angleInDegrees);
	
	    // recreate the new Bitmap
	    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, srcX, srcY,
	                      srcWidth, srcHeight, matrix, true);
	    
	    // x and y are top left coordinates
	    // x and y must be calculated if the image isn't square
	    // Question: should x and y be center of image instead?
	    canvas.drawBitmap(resizedBitmap, x, y, paint);
	}
   
    @Override
    public int getWidth() {
        return frameBuffer.getWidth();
    }

    @Override
    public int getHeight() {
        return frameBuffer.getHeight();
    }
    
    @Override
    public Canvas getCanvas() {
    	return this.canvas;
    }
    
    @Override
    public Paint getPaint() {
    	return this.paint;
    }
}
