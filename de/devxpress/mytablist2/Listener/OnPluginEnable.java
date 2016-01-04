package de.devxpress.mytablist2.Listener;

import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import de.devxpress.mytablist2.Main;

public class OnPluginEnable implements Listener{
	
	Main plugin;
	
	public OnPluginEnable(Main plugin){
		this.plugin = plugin;
	}
	
	public void onPluginEnable(PluginEnableEvent event) {
		plugin.setupEconomy();
	    plugin.setupPermissions();
	    plugin.setupChat();
		
	}
	
	public void onPluginDisable(PluginDisableEvent event) {
		plugin.setupEconomy();
	    plugin.setupPermissions();
	    plugin.setupChat();
	}
	
}
