package de.devxpress.mytablist2.Listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.devxpress.mytablist2.Main;
import de.devxpress.mytablist2.TabManager;

public class OnPlayerJoin implements Listener{
	
	Main plugin;
	
	public OnPlayerJoin(Main plugin){
		this.plugin = plugin;
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	  public void playerJoin(PlayerJoinEvent e){
		plugin.uniqueLogins += 1;
		List<String> list = new ArrayList<String>();
		for(Player p : Bukkit .getOnlinePlayers()){
			list.add(p.getName());
		}
		TabManager.getInstance().playerTablists.put(e.getPlayer(), list);
		TabManager.getInstance().playerlist(e.getPlayer(), true);
	    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    	public void run() {
	    		for (Player player : Bukkit.getOnlinePlayers()){
	    	    	TabManager.getInstance().refreshTablist(player);
	    	    }
	    	}
	    	}, 5L);
	  }
	
	@EventHandler(priority=EventPriority.MONITOR)
	  public void playerLeave(PlayerQuitEvent e){
	    TabManager.getInstance().removeTablist(e.getPlayer());
		TabManager.getInstance().playerlist(e.getPlayer(), false);
		for (Player player : Bukkit.getOnlinePlayers()){
	    	TabManager.getInstance().refreshTablist(player);
	    }
	  }
}
