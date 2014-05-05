package com.pussycat.minions;

public class Background {
	
		public final String fileName;
		public final int width;
		public final int height;
		public final float ppix;
		public final float ppiy;
		public final int nRows;
		public final int nCols;
		
	public Background(final String fileName, final int width, final int height, final float ppix, final float ppiy, final int nRows, final int nCols) {
		this.fileName = fileName;
		this.width = width;
		this.height = height;
		this.ppix = ppix;
		this.ppiy = ppiy;
		this.nRows = nRows;
		this.nCols = nCols;
	}
	
}
