package de.devxpress.mytablist2.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.devxpress.mytablist2.Main;
import de.devxpress.mytablist2.utils.types.SupportedFunctions;

public class CommandHandler implements CommandExecutor{
	
	Main plugin;
	
	public CommandHandler(Main plugin){
	    this.plugin = plugin;
	}
	

	LanguageManager ml = new LanguageManager(plugin);
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	  {
	    if (command.getName().equalsIgnoreCase("mytablist"))
	    {
	    	if (sender.hasPermission("mytablist2.main") || sender.isOp() || sender.hasPermission("mytablist2.reload") || sender.hasPermission("mytablist2.setvar") || sender.hasPermission("mytablist2.setline") || sender.hasPermission("mytablist2.debuginfo")){
			    if(plugin.issubcommand(args)){
			    	if(args[0].equalsIgnoreCase("reload")){
			    		  if(sender.hasPermission("mytablist2.reload") || sender.isOp()){
			    			plugin.alertOperators(sender, "§e§oReloading MyTablist " + plugin.getPluginVersion() + " ...§7§o");
					        sender.sendMessage(Main.prefix() + "§e§oReloading MyTablist " + plugin.getPluginVersion() + " ...§7§o");
					        plugin.reloadConfig();
					        plugin.reloadplugin();
					        plugin.alertOperators(sender, "§a§oMyTablist " + plugin.getPluginVersion() + " sucessfully reloaded.§7§o");
					        sender.sendMessage(Main.prefix() + "§a§oMyTablist " + plugin.getPluginVersion() + " sucessfully reloaded.§7§o");
					        return true;
					      }
				
					      sender.sendMessage(Main.prefix() + "§cYou don't have permission to execute this command!");
					      return true;
			    	}else if(args[0].equalsIgnoreCase("setvar")){
			    		if (sender.hasPermission("mytablist2.setvar") || sender.isOp()){
			    			if(args.length == 1){
			    				sender.sendMessage(Main.prefix() + "§aThe variable must be set!");
							      return true;
			    			}else if(args.length == 2 || args.length == 3){	// if(args.length > 1)
			    				
			    				String variable = args[1];
			    				String value = "";
			    				
			    				if(args.length == 3){
			    					value = args[2];
			    				}else{
			    					value = "";
			    				}
			    				
			    				File file = new File(Main.getInstance().getDataFolder() + Main.getFilesep() + "config.yml");
			    				YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
			    				//conf.options().header("");
			    				
			    			     conf.set("Customvars." + variable, "" + value);
			    			        	  
	    			        	try {
	    							conf.save(file);
	    						} catch (IOException e) {
	    							e.printStackTrace();
	    						}
	    			        	
	    			        	if(args.length == 3){
	    			        		sender.sendMessage(Main.prefix() + "§aThe Variable " + variable + " has been set to: " + value);
	    			        	}else{
	    			        		sender.sendMessage(Main.prefix() + "§aThe Variable " + variable + " has been Cleared!");
	    			        	}
	    			        		sender.sendMessage(Main.prefix() + "§aThis will delete all Comments in the file!");
	    			        	
	    			        	return true;
			    			}else{
			    				sender.sendMessage(Main.prefix() + "§cAn Error occurred while performing /mytablist setvar" + args.length);
	    			        	return true;
			    			}
			    			
					    }
				
					      sender.sendMessage(Main.prefix() + "§cYou don't have permission to execute this command!");
					      return true;
			    	}else if(args[0].equalsIgnoreCase("debuginfo")){
			    		if (sender.hasPermission("mytablist2.debuginfo") || sender.isOp()){
			    		
	    			        		sender.sendMessage("§7--------Debug-Info---------");
	    			        		sender.sendMessage("§6MyTablist version: §7" + plugin.getPluginVersion());
	    			        		sender.sendMessage("§6MyTablist Build: §7" + plugin.getBuild());
	    			        		sender.sendMessage("§6Bukkit version: §7" + plugin.getBukkitVersion());
	    			        		sender.sendMessage("§6Minecraft version: §7" + plugin.getServerVersion());
	    			        		sender.sendMessage("§6Server Software: §7" + Bukkit.getVersion());
	    			        		sender.sendMessage("§6Permission Support: " + plugin.DebugSupport(SupportedFunctions.PERMISSIONS));
	    			        		sender.sendMessage("§6Chat Support: " + plugin.DebugSupport(SupportedFunctions.CHAT));
	    			        		sender.sendMessage("§6Economy Support: " + plugin.DebugSupport(SupportedFunctions.ECONOMY));
	    			        		sender.sendMessage("§6Multiverse Support: " + plugin.DebugSupport(SupportedFunctions.MULTIVERSE));
	    			        		sender.sendMessage("§6SkillAPI Support: " + plugin.DebugSupport(SupportedFunctions.SKILLAPI));
	    			        		sender.sendMessage("§6Vault Support: " + plugin.DebugSupport(SupportedFunctions.VAULT));
	    			        		sender.sendMessage("§6useAutoUpdater: " + plugin.useAutoUpdater());
	    			        		sender.sendMessage("§6Autorefresh: " + plugin.useAutoRefresh());
	    			        		sender.sendMessage("§6Logins: §7" + plugin.uniqueLogins);
	    			        		sender.sendMessage("§6Players: §7" + plugin.getServer().getOnlinePlayers().length);
	    			        		sender.sendMessage("§6OS: §7" + System.getProperty("os.name"));
	    			        		sender.sendMessage("§6Java-Version: §7" + System.getProperty("java.version"));
	    			        		sender.sendMessage("§6Addons: §7" + plugin.getAPI().getAddons());
	    			        		sender.sendMessage("§7---------------------------");
			    			return true;
					    }
					      sender.sendMessage(Main.prefix() + "§cYou don't have permission to execute this command!");
					      return true;
			    	}else if(args[0].equalsIgnoreCase("removetablist")){
			    		if (sender.hasPermission("mytablist2.removetablist") || sender.isOp()){
				    		
			        		plugin.getTabManager().removeTablist((Player)sender);
			        		sender.sendMessage("§6Tablist removed!");
	    			return true;
			    }
			      sender.sendMessage(Main.prefix() + "§cYou don't have permission to execute this command!");
			      return true;
	    	}/*else if(args[0].equalsIgnoreCase("setline")){
			    		if (sender.hasPermission("mytablist2.setline") || sender.isOp()){
			    			if(args.length == 1){
			    				sender.sendMessage(prefix() + "§aThe line must be set!");
							      return true;
			    			}else if(args.length >= 4){	
			    				
			    				String world = args[1];
			    				String line = args[2]+"."+args[3];
			    				String value = "";
			    				
			    				if(args.length >= 5){
			    					for(int j = 4; j < args.length; j++){
			    						value = value + " " + args[j];
			    					}
			    				}else{
			    					value = "";
			    				}
			    				sender.sendMessage(world);
			    				sender.sendMessage(line);
			    				sender.sendMessage(value);
			    				
			    				File file = new File(getInstance().getDataFolder() + getFilesep() + "worlds" + getFilesep() + world + ".yml");
			    				if(!file.exists()){
			    					makefile(world, world);
			    				}
			    				
			    				YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
			    				
			    			     conf.set(line, value);
			    			        	  
	    			        	try {
	    							conf.save(file);
	    						} catch (IOException e) {
	    							e.printStackTrace();
	    						}
	    			        	
	    			        	if(args.length >= 5){
	    			        		sender.sendMessage(prefix() + "§aThe Variable " + line + " has been set to: " + value);
	    			        	}else{
	    			        		sender.sendMessage(prefix() + "§aThe Variable " + line + " has been Cleared!");
	    			        	}
	    			        	reloadTablist();
	    			        	return true;
			    			}else{
			    				sender.sendMessage(prefix() + "§cAn Error occurred while performing /mytablist setvar" + args.length);
	    			        	return true;
			    			}
			    			
					    }
				
					      sender.sendMessage(prefix() + "§cYou don't have permission to execute this command!");
					      return true;*/
			    	
			    } else{ 
			    	sender.sendMessage(ml.getCommandHelp());
			    }
	    	}else{
	    		sender.sendMessage(Main.prefix() + "§cYou don't have permission to execute this command!");
	    	}
	    }

	    return true;
	  }
	
}
