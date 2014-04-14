package com.pussycat.minions;

public class BackgroundHandler {

	public static Background[] backgrounds;
	
	public static enum BACKGROUNDS {
		COORDINATES,
		COORDINATES_LOW_RES_3,
		COLORSPECTRUM
	}
	
	public BackgroundHandler()  {
		
		backgrounds = new Background[BACKGROUNDS.values().length];		
		
		backgrounds [BACKGROUNDS.COORDINATES.ordinal()] = new Background(		"coordinates.png", 								// file
																				16540, 											// Width
																				16540,											// Height
																				295.35714285714285714285714285714f,				// ppix
																				295.35714285714285714285714285714f		);		// ppiy
		
		backgrounds [BACKGROUNDS.COLORSPECTRUM.ordinal()] = new Background(		"colorspectrum.jpg", 			// file
																				5000, 							// Width
																				4000,							// Height
																				96,								// ppix
																				96						);		// ppiy
																					
		
		
		backgrounds [BACKGROUNDS.COORDINATES_LOW_RES_3.ordinal()] = new Background(		"coordinates_lowres3.png", 		// file
																						2962, 							// Width
																						2962,							// Height
																						72,								// ppix
																						72							);	// ppiy
		
	}
	
}
