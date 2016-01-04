package de.devxpress.mytablist2.api;

import java.io.Serializable;

import org.bukkit.plugin.Plugin;

@SuppressWarnings("serial")
public class addon implements Serializable {
	
public Class<?> classs;
public String method;
public Plugin plugin;

	public addon(Class<?> Class, String Method, Plugin Plugin) {
	    classs = Class;
	    method = Method;
	    plugin = Plugin;
	}

}