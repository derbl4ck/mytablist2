package de.devxpress.mytablist2.utils;

import java.util.HashMap;
import java.util.Map;

import de.devxpress.mytablist2.utils.time;
import de.devxpress.mytablist2.utils.time.type;

public class counter {
	
	time time;
	public Map<String, String> players = new HashMap<String, String>();
	
	public void addPlayer(String player){
		players.put(player, time.getTime(type.FULL));
	}
	
	
	
}
