package de.devxpress.mytablist2.Listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import de.devxpress.mytablist2.TabManager;

public class OnWorldSwitch implements Listener{
	
	@EventHandler(priority=EventPriority.MONITOR)
	  public void worldswitch(PlayerChangedWorldEvent e)
	  {
		TabManager.getInstance().refreshTablist(e.getPlayer());
		 	for (Player player : Bukkit.getOnlinePlayers()){
		 		TabManager.getInstance().refreshTablist(player);
		    }
	  }
	
	
}
