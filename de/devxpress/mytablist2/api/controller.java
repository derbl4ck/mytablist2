package de.devxpress.mytablist2.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import de.devxpress.mytablist2.Main;

public class controller {
	
	private static Main plugin;
	
	public static List<addon> Addons = new ArrayList<addon>();
	
	public controller(Main plugin){
	    controller.plugin = plugin;
	}
	
	public static void addVariable(Class<?> Class, String Method, Plugin Plugin){
		addon a = new addon(Class, Method, Plugin);
		Addons.add(a);
		Bukkit.getConsoleSender().sendMessage("[MyTabList2] §8new Variables added (" + Plugin.getName() + ")");
	}
	
	public static String replace(String string, org.bukkit.entity.Player p) {
		if(plugin.useApi()){
			for(addon a : Addons){
						try {
							Method method;
							method = a.classs.getMethod(a.method, string.getClass(), org.bukkit.entity.Player.class);
							Object value = method.invoke( null, new Object[]{ string , p });
							string = (String) value;
						} catch (NoSuchMethodException | SecurityException
								| IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException e) {
							Bukkit.getConsoleSender().sendMessage("[MyTabList2] §4An Error Occoured while adding the Variables from plugin " + a.plugin.getName() + "!");
						}
			
				
			}
		}
		return string;
	}
	
	public String getAddons(){
		String s = "";
		if(Addons.size() > 1){
			for(addon a : Addons){
			s = s + a.plugin.getName() + ", ";
			}
		}else{
			for(addon a : Addons){
				s = s + a.plugin.getName();
			}
		}
		return s;
	}
}
