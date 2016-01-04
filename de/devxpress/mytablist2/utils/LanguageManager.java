package de.devxpress.mytablist2.utils;

import de.devxpress.mytablist2.Main;

public class LanguageManager {

	Main plugin;
	
	public LanguageManager(Main plugin){
	    this.plugin = plugin;
	}
	public String getCommandHelp(){
		return Main.prefix() + "§cPlease use a correct Subcommand!\n§a - /mytablist reload\n§a - /mytablist setvar [variable] [value]";
	}
}
