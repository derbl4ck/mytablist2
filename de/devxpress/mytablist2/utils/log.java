package de.devxpress.mytablist2.utils;

import de.devxpress.mytablist2.Main;

public class log {

	public void info(String message){
		Main.getInstance().getLogger().info(message);
	}
	
}
