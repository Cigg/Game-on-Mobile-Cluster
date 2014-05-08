package com.pussycat.minions;

import java.util.HashMap;
import java.util.Map;

public class Settings {
	
	private Map<String, String> map = new HashMap<String, String>();

	public Settings(){
		map.put("ballRadius", "0.01");
	}
	
	public String getSetting(String key)
	{
		return map.get(key);
	}
}
