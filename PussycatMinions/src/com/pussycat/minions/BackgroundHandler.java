package com.pussycat.minions;

public class BackgroundHandler {

	public static Background[] backgrounds;
	
	public static enum BACKGROUNDS {
		COORDINATES,
		COORDINATES_LOW_RES_3,
		COLORSPECTRUM,
		COORDINATES_SPLIT,
		SAND
	}
	
	public BackgroundHandler()  {
		
		backgrounds = new Background[BACKGROUNDS.values().length];		
		
		backgrounds [BACKGROUNDS.COORDINATES.ordinal()] = new Background(		"Coordinates_32x32", "Coordinates_", ".png",							
																				16540, 											
																				16540,											
																				295.35714285714285714285714285714f,				
																				295.35714285714285714285714285714f,
																				32,
																				32										);		
		
		/*
		backgrounds [BACKGROUNDS.COLORSPECTRUM.ordinal()] = new Background(		"colorspectrum.jpg", 		
																				5000, 							
																				4000,							
																				96,								
																				96, 
																				32,
																				32						);		
		
		backgrounds [BACKGROUNDS.COORDINATES_LOW_RES_3.ordinal()] = new Background(		"coordinates_lowres3.png", 		
																						2962, 						
																						2962,							
																						72,							
																						72,
																						32,
																						32								);
		*/
		
		
		backgrounds [BACKGROUNDS.COORDINATES_SPLIT.ordinal()] = new Background(		"Coordinates_split",  "Coordinates_", ".png",							
																					8270, 											
																					8270,											
																					150.0f,				
																					150.0f,
																					20,
																					20										);		
		
		backgrounds [BACKGROUNDS.SAND.ordinal()] = new Background(		"sand_split",  "Coordinates_", ".png",							
																					4134, 											
																					4134,											
																					150.0f,				
																					150.0f,
																					20,
																					20										);	
	}
	
}
