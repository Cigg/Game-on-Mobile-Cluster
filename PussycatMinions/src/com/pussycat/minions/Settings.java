package com.pussycat.minions;

import java.util.HashMap;
import java.util.Map;

import com.pussycat.framework.Graphics.ImageFormat;

public class Settings {
	
	private Map<String, String> map = new HashMap<String, String>();

	public Settings(){
		map.put("ballRadius", "0.01");
		
		// Button settings
		map.put("menuButton", "4.0");
		map.put("settingsButton", "1.3");
		
	}
	
	public String getSetting(String key)
	{
		return map.get(key);
	}
}
