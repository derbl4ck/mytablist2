package de.devxpress.mytablist2;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;

import mcstats.Metrics;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.devxpress.mytablist2.Listener.OnPlayerJoin;
import de.devxpress.mytablist2.Listener.OnPluginEnable;
import de.devxpress.mytablist2.Listener.OnWorldSwitch;
import de.devxpress.mytablist2.api.controller;
import de.devxpress.mytablist2.utils.CommandHandler;
import de.devxpress.mytablist2.utils.Updater;
import de.devxpress.mytablist2.utils.Updater.UpdateResult;
import de.devxpress.mytablist2.utils.types.SupportedFunctions;
import de.devxpress.mytablist2.utils.types.Version;

public class Main extends JavaPlugin {

	  private Version build = Version.ALPHA; //TODO build status
	  private TabManager tab;
	  private OnPlayerJoin server;
	  private OnPluginEnable spl;
	  private OnWorldSwitch wswitch;
	  private CommandHandler cmdHandler;
	  private controller api;
	  private static Main instance;
	  private boolean metrics;
	  private boolean useupdater;
	  private boolean automaticdownload;
	  private boolean useapi;
	  private boolean pingseption;
	  private boolean usedisplaynames;
	  private boolean autorefresh;
	  private static boolean Vault;
	  private String serverVersion = "Unknown";
	  private String bukkitVersion = "Unknown";
	  private String serverSoftName = "Unknown";
	  private String configtabh = "";
	  private String configtabf = "";
	  private int interval;
	  public static Economy economy = null;
	  public static Permission permission = null;
	  public static Chat chat = null;
	  public int uniqueLogins = 0;
	  public URL filesFeed;
	  public String version;
	  public String link;

	  public void onEnable(){
	    instance = this;
	    this.tab = new TabManager(this);
	    this.server = new OnPlayerJoin(this);
	    this.wswitch = new OnWorldSwitch();
	    this.spl = new OnPluginEnable(this);
	    this.cmdHandler = new CommandHandler(this);
	    this.api = new controller(this);
	    
	    Bukkit.getPluginManager().registerEvents(this.server, this);
	    Bukkit.getPluginManager().registerEvents(this.wswitch, this);
	    Bukkit.getPluginManager().registerEvents(this.spl, this);
	    
	    if(detecterrors()){
	    	checkBukkitVersion();
	    	checkServerVersion();
	    	configure();
	    
	    	getCommand("mytablist").setExecutor(cmdHandler);
	    
		    if(isUpdater() == true){
		    	Updater updater;
		    	if(automaticdownload == true){
		    		updater = new Updater(this, 52488, this.getFile(), Updater.UpdateType.DEFAULT, true);
		    	}else{
		    		updater = new Updater(this, 52488, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
		    	}
			    
			    if(updater.getResult() == UpdateResult.DISABLED){
			    	ConsoleCommandSender console = this.getServer().getConsoleSender();
			    	console.sendMessage("[MyTablist2] Auto Updater is Disabled. Not checking for an new Version.");
			    }else{
			    		String updatename = updater.getLatestName().replaceAll("[a-zA-Z ]", "");
			    if(!this.getDescription().getVersion().equals(updatename)){
			    	if(automaticdownload == false){
			    		ConsoleCommandSender console = this.getServer().getConsoleSender();
			    	  	console.sendMessage("[MyTablist2] " + ChatColor.GREEN + "A new version is available: " + updater.getLatestName() + " (this version: " + this.getDescription().getVersion() + ")");
			        	console.sendMessage("[MyTablist2] Get it from: " + updater.getLatestFileLink());
			    	}
			    }else{
			    		ConsoleCommandSender console = this.getServer().getConsoleSender();
			    		console.sendMessage("[MyTablist2] MyTablist is up to Date!");
			    }
			    }
		    }else{
		    	ConsoleCommandSender console = this.getServer().getConsoleSender();
		    	console.sendMessage("[MyTablist2] Auto Updater is Disabled. Not checking for an new Version.");
		    }
	    
		    if (this.metrics){
		    	try
		        {
		          new Metrics(this).start();
		          getLogger().log(Level.INFO, "Metrics initialized!");
		        } catch (Exception localException2) {
		          getLogger().log(Level.WARNING, "Failed to initialize Metrics!");
		        }
		    }
		    
		    Vault = checkvault(1);
		    
		    setupEconomy();
		    setupPermissions();
		    setupChat();
		   
		    loadConfigs();
		    
		    for(Player p : Bukkit.getOnlinePlayers()){
		    	TabManager.getInstance().playerlist(p, true);
		    }
		    
		    reloadTablist();
		    startrefreshAsync();
		    
		    getLogger().info("Version " + getPluginVersion() + " by DevXPress");
	    }
	  }
	  
	  
	  public void loadConfigs(){
		 if(!getInstance().getConfig().isConfigurationSection("Configs")){
			 getLogger().info("No Configs found using default Config.");
			 File file = new File(getInstance().getDataFolder() + getFilesep() + "worlds" + getFilesep() + "default.yml");
	  		  if(!file.exists()){
	    		setdefaults("default", "MyTablist 2", "A plugin by", "DevXPress");
			  }
		 }else{
			 
			 ConfigurationSection entries = getInstance().getConfig().getConfigurationSection("Configs");
			 
				if(entries.getKeys(false).size() > 0){
					for (int i = 0; i < entries.getKeys(false).size(); i++) {
						String key = (String) entries.getKeys(false).toArray()[i];
						
						File file = new File(getInstance().getDataFolder() + getFilesep() + "worlds" + getFilesep() + key + ".yml");
				  		  if(!file.exists()){
				    		setdefaults(key, "MyTablist 2", "A plugin by", "DevXPress");
						  }
					}
				}else{
					 File file = new File(getInstance().getDataFolder() + getFilesep() + "worlds" + getFilesep() + "default.yml");
			  		  if(!file.exists()){
			    		setdefaults("default", "MyTablist 2", "A plugin by", "DevXPress");
					  }
				 }
		 }
	  }
	  
	  public void configure(){
		  	saveDefaultConfig();
		    reloadConfig();

		    this.interval = getConfig().getInt("refreshInterval");
		    this.metrics = getConfig().getBoolean("useMetrics");
		    this.setUpdater(getConfig().getBoolean("useAutoUpdater"));
		    this.automaticdownload = getConfig().getBoolean("autodownload");
		    this.autorefresh = getConfig().getBoolean("autoRefresh");
		    this.useapi = getConfig().getBoolean("useAPI");
		    this.pingseption = getConfig().getBoolean("pingseption");
		    this.usedisplaynames = getConfig().getBoolean("useDisplayNames");
		    this.configtabh = getConfig().getString("tabheader");
		    this.configtabf = getConfig().getString("tabfooter");
	  }

	  public void onDisable()
	  {
	    Bukkit.getScheduler().cancelTasks(this);

	    unloadTablist();
	    this.tab.playerTablists.clear();

	    getLogger().info("MyTablist " + getPluginVersion() + " disabled!");
	  }
	  
	  @SuppressWarnings("deprecation")
	  public void startrefreshAsync(){
		  if (this.autorefresh){
		     Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, startRefresh(), 1L, this.interval * 20L);
		     Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, checktabapi(), 1L, this.interval * 20L);
		  }else{
			 Bukkit.getScheduler().scheduleSyncDelayedTask(this, startRefresh(), 20L);
			 Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, checktabapi(), 1L, 10 * 20L);
	      }
	  }
 
	  public boolean issubcommand(String[] args){
		  if (args.length == 0){
			  return false;
		  }else{
			  if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("setvar") || args[0].equalsIgnoreCase("setline") || args[0].equalsIgnoreCase("debuginfo") || args[0].equalsIgnoreCase("removetablist")){
				  return true;
			  }else{
				  return false;
			  }
		  }
	  }
	  
	  public void reloadplugin(){
	        Bukkit.getPluginManager().disablePlugin(this);
	        Bukkit.getPluginManager().enablePlugin(this);
	  }
	  
	  public void reloadTablist(){
	    for (Player player : Bukkit.getOnlinePlayers()){
	      TabManager.getInstance().setTablist(player);
	    }
	  }

	  private void unloadTablist(){
	    for (Player player : Bukkit.getOnlinePlayers()){
	      TabManager.getInstance().removeTablist(player);
	    }
	  }

	  public static String prefix(){
		  return "§7[§6MyTablist2§7] §r";
	  }
	  
	  private void checkServerVersion()
	  {
	    try
	    {
	    	this.serverVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
	    }
	    catch (Exception e)
	    {
	      getLogger().severe("Unknown or unsupported CraftBukkit version! Is the Plugin up to date?");
	      Bukkit.getPluginManager().disablePlugin(this);
	    }
	  }

	  private void checkBukkitVersion()
	  {
	    String version = Bukkit.getVersion();
	    version = version.replace("(", "");
	    version = version.replace(")", "");
	    version = version.split(" ")[2];
	    this.bukkitVersion = version;
	  }
	  
	  public boolean detecterrors()
	  {
		 return disable_tabapi();
	  }
	  
	  public boolean disable_tabapi()
	  {
		  Plugin tabapi = getServer().getPluginManager().getPlugin("TabAPI");
		  if(tabapi != null){
			  if (tabapi.isEnabled()) {
				  getLogger().warning("This Plugin does not work with TabAPI. TabAPI will be disabled automatically!");
				  Bukkit.getPluginManager().disablePlugin(tabapi);
				  return false;
			  }else{
				  return true;
			  }
		  }else{
			  return true;
		  }
	  }
	  
	  public boolean setupEconomy(){
		  if (checkvault() == false) {
	            return false;
		  }
		  	RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
	        if (economyProvider != null) {
	            economy = economyProvider.getProvider();
	            getLogger().info("Economy Plugin found! " + economy.getName() + " hooked!");
	            return true;
	        }
	        return (economy) != null;
	  }
	  
	  public boolean checkvault(int log){
		  if (getServer().getPluginManager().getPlugin("Vault") == null) {
				 if(log == 1){getLogger().info("Vault not found. Support Disabled!");}
		            return false;
		     }
		  return true;
	  }
	  
	  public String getvaultversion(){
		  if (getServer().getPluginManager().getPlugin("Vault") != null) {
			  return getServer().getPluginManager().getPlugin("Vault").getDescription().getVersion();
		  }else{
			  return "";
		  }
	  }
	  
	  public boolean checkvault(){
		  return checkvault(0);
	  }
	  
	  public boolean checkmultiverse(){
		  if (getServer().getPluginManager().getPlugin("Multiverse-Core") == null) {
			  return false;
		  }else{
			  return getServer().getPluginManager().getPlugin("Multiverse-Core").isEnabled();
		  }
	  }
	  
	  public String getmultiverseversion(){
		  if (getServer().getPluginManager().getPlugin("Multiverse-Core") != null) {
			  return getServer().getPluginManager().getPlugin("Multiverse-Core").getDescription().getVersion();
		  }else{
			  return "";
		  }
	  }
	  
	  public String getexversion(String plugin){
		  if (getServer().getPluginManager().getPlugin(plugin) != null) {
			  return getServer().getPluginManager().getPlugin(plugin).getDescription().getVersion();
		  }else{
			  return "";
		  }
	  }
	  
	  public boolean hasSkillAPI(){
		  if (getServer().getPluginManager().getPlugin("SkillAPI") == null) {
		      return false;
		  }else{
			  return getServer().getPluginManager().getPlugin("SkillAPI").isEnabled();
		  }
	  }
	  
	  public String getSkillAPIversion(){
		  if (getServer().getPluginManager().getPlugin("SkillAPI") == null) {
		      return "";
		  }else{
			  return getServer().getPluginManager().getPlugin("SkillAPI").getDescription().getVersion();
		  }
	  }
	  
	  public boolean setupPermissions(){
		  if (checkvault() == false) {
		      return false;
		  }
	        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
	        if (permissionProvider != null) {
	            permission = permissionProvider.getProvider();
	            getLogger().info("Permission Plugin found! " + permission.getName() + " hooked!");
	        }
	        return (permission != null);
	  }
	  
	  public boolean setupChat(){
		  if (checkvault() == false) {
			  return false;
		  }
	        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
	        if (chatProvider != null) {
	            chat = chatProvider.getProvider();
	            getLogger().info("Chat Plugin found! " + chat.getName() + " hooked!");
	        }
	        return (chat != null);
	  }
	  
	  public boolean hasSupport(SupportedFunctions f){
		  if(f == SupportedFunctions.CHAT){
			  if(useVault()){
				  if(chat.isEnabled()){
					  return true;
				  }else{
					  return false;
				  }
			  }else {
				  return false;
			  }
		  }else if(f == SupportedFunctions.ECONOMY){
			  if(useVault()){
				  if(economy.isEnabled()){
					  return true;
				  }else{
					  return false;
				  }
			  }else {
				  return false;
			  }
		  }else if(f == SupportedFunctions.MULTIVERSE){
			  return checkmultiverse();
		  }else if(f == SupportedFunctions.VAULT){
			  return checkvault();
		  }else if(f == SupportedFunctions.SKILLAPI){
			  return hasSkillAPI();
		  }else if(f == SupportedFunctions.PERMISSIONS){
			  if(useVault()){
				  if(permission.isEnabled()){
					  return true;
				  }else{
					  return false;
				  }
			  }else {
				  return false;
			  }
		  }else{
			  return false;
		  }
	  }
	  
	  public String Supports(SupportedFunctions f, boolean colored){
		  boolean sp = hasSupport(f);
		  if(sp == true){
			  if(colored == true){
				  return "§aYes";
			  }else{
				  return "Yes";
			  }
		  }else{
			  if(colored == true){
				  return "§cNo";
			  }else{
				  return "No";
			  }
		  }
	  }
	  
	  public String DebugSupport(SupportedFunctions f){
		  String i = "";
		  
		  if(hasSupport(f)){
			  i = " §7(";
			  
			  if(f == SupportedFunctions.CHAT){
				  i = i + chat.getName() + " " + getexversion(chat.getName());
			  }else if(f == SupportedFunctions.ECONOMY){
				  i = i + economy.getName() + " " + getexversion(economy.getName());
			  }else if(f == SupportedFunctions.PERMISSIONS){
				  i = i + permission.getName() + " " + getexversion(permission.getName());
			  }else if(f == SupportedFunctions.MULTIVERSE){
				  i = i + getmultiverseversion();
			  }else if(f == SupportedFunctions.VAULT){
				  i = i + getvaultversion();
			  }else if(f == SupportedFunctions.SKILLAPI){
				  i = i + getSkillAPIversion();
			  }
			  
			  i = i + "§7)";
		  }
		  return Supports(f, true) + i;
	  }
	  
	  public String getBukkitVersion(){
		  return this.bukkitVersion;
	  }

	  public String getServerVersion(){
		  return this.serverVersion;
	  }
	  
	  public String getServerSoftware(){
		  return this.serverSoftName;
	  }
	  
	  public TabManager getTabManager(){
		  return tab;
	  }
	  
	  public controller getAPI(){
		  return api;
	  }
	  
	  public Version getBuildVersion(){
		  return build;
	  }
	  
	  public String getBuild(){
		  if(build == Version.ALPHA){
			  return "Alpha";
		  }else if(build == Version.BETA){
			  return "Beta";
		  }else if(build == Version.DEV){
			  return "Dev";
		  }else if(build == Version.FINAL){
			  return "Final";
		  }else{
			  return "Dev";
		  }
	  }
	  
	  public static String getFilesep(){
		  return System.getProperty("file.separator");
	  }
	  
	  public String getPluginVersion(){
		  return this.getDescription().getVersion();
	  }
	  
	  public boolean useApi(){
		  return this.useapi;
	  }
	  
	  public static boolean useVault(){
		  return Vault;
	  }
	  
	  public boolean isUpdater(){
		  return useupdater;
	  }

	  public boolean isPingseption(){
		  return pingseption;
	  }
	  
	  public boolean useDisplayNames(){
		  return usedisplaynames;
	  }
	  
	  public void setUpdater(boolean updater){
		  this.useupdater = updater;
	  }
	  
	  public String getTabConfHeader(){
		  return this.configtabh;
	  }
	  
	  public String getTabConfFooter(){
		  return this.configtabf;
	  }
	  
	  public String useAutoUpdater(){
		  if(isUpdater() == true){
			  return "§aYes";
		  }else{
			  return "§cNo";
		  }
	  }
	  
	  public String useAutoRefresh(){
		    if(this.autorefresh == true){
		    	return "§aYes §7(" + this.interval + ")";
		    }else{
		    	return "§cNo";
		    }
	  }
	  
	  public static Main getInstance(){
		  return instance;
	  }

	  public void alertOperators(CommandSender sender, String alert){
	    for(Player player : Bukkit.getOnlinePlayers()){
	      if(player.isOp()){
	        if (!sender.getName().equals(player.getName())){
	        	player.sendMessage("§7§o[" + sender.getName() + ": " + alert + "]");
	        }
	      }
	    }
	  }

	  private BukkitRunnable startRefresh(){
	    BukkitRunnable runnable = new BukkitRunnable(){
	      public void run(){
	        for (Player player : Bukkit.getOnlinePlayers()){
	        	TabManager.getInstance().refreshTablist(player);
	        }
	      }
	    };
	    return runnable;
	  }
	  
	  private BukkitRunnable checktabapi(){
		BukkitRunnable runnable = new BukkitRunnable(){
		  public void run(){
			  disable_tabapi();
		  }
		};
		return runnable;
	  }
	  
	  public static String getGroup(Player player){
		  if(useVault()){
			  if(permission.isEnabled()){
		    	if(permission.getGroups().length > 0){
			    	if(permission.getPlayerGroups(player).length > 0){
			    		return permission.getPrimaryGroup(player);
			    	}
			    }
			  }
		  }
		  return "";
		  
	  }
	  
	  public static boolean hasPermission(Player player, List<String> list){
		  for(String permission:list){
			  String perm = permission.substring(5);
			 if(player.hasPermission(perm)){
				 return true;
			 }
		  }
		  return false;
	  }
	
	@SuppressWarnings("unchecked")
	public static String getWorldConfigs(Player player, String dest){
		
		String pworld = player.getWorld().getName();
		String file = null;
		
		if(getInstance().getConfig().isConfigurationSection("Configs")){
		 
			 ConfigurationSection entries = getInstance().getConfig().getConfigurationSection("Configs");
		 
			if(entries.getKeys(false).size() > 0){
				
				for (int i = 0; i < entries.getKeys(false).size(); i++) {
					String key = (String) entries.getKeys(false).toArray()[i];
					
				    for (int j = 0; j < entries.getList(key).size(); j++) {
				    	if(entries.getList(key).contains("p_" + player.getName())){
				    		file = (String) entries.getKeys(false).toArray()[i]; //persönliche tablist mit p_Spielername
				    		makefile(file, "p_" + player.getName());
				    	}else if(entries.getList(key).contains("g_" + getGroup(player))){ //persönline tablist mit g_Gruppenname
				    		file = (String) entries.getKeys(false).toArray()[i];
				    		makefile(file, "g_" + getGroup(player));
				    	}else if(hasPermission(player, (List<String>) entries.getList(key))){ //persönliche tablist mit perm_Permissionname
				    		file = (String) entries.getKeys(false).toArray()[i];
				    		makefile(file, pworld);
				    	}else if(entries.getList(key).contains(pworld)){
				    		file = (String) entries.getKeys(false).toArray()[i];
				    		makefile(file, pworld);
				    	}else{
				    		File file2 = new File(getInstance().getDataFolder() + getFilesep() + "worlds" + getFilesep() + "default.yml");
					  		  if(!file2.exists()){
					    		setdefaults("default", "MyTablist 2", "A plugin by", "DevXPress");
							  }
					  		file = "default";
				    	}
				    	
				    }
				}
			}else{
				 File file2 = new File(getInstance().getDataFolder() + getFilesep() + "worlds" + getFilesep() + "default.yml");
		  		  if(!file2.exists()){
		    		setdefaults("default", "MyTablist 2", "A plugin by", "DevXPress");
				  }
		  		file = "default";
			 }
			
		 }else{
			 File file2 = new File(getInstance().getDataFolder() + getFilesep() + "worlds" + getFilesep() + "default.yml");
	  		  if(!file2.exists()){
	    		setdefaults("default", "MyTablist 2", "A plugin by", "DevXPress");
			  }
	  		file = "default";
		 }
		
		File file2 = new File(getInstance().getDataFolder() + getFilesep() + "worlds" + getFilesep() + file + ".yml");
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(file2);
		String string = conf.getString(dest);
		
		return string;
	}
	
	static void makefile(String name, String configfor){
		  File filedir = new File(getInstance().getDataFolder() + getFilesep() + "worlds" + getFilesep());
		  File file = new File(getInstance().getDataFolder() + getFilesep() + "worlds" + getFilesep() + name + ".yml");
		  if(!file.exists()){
			  try {
				filedir.mkdirs();
				file.createNewFile();
				setdefaults(name, "This is the", "config file for", configfor);
			} catch (IOException e) {
				getInstance().getLogger().warning("An error occurred while creating the config file!");
			}
		  }
	}
	
	static void setdefaults(String filename, String firstline, String middleline, String leftline){
		String header = "\n IMPORTANT: If the length of a slot is bigger then 16 characters,\n then will be the text of the slot reduced to 16 characters!\n\n IMPORTANT: Minecraft will delete a Tablist slot if two slot's have the same Value!!\n";
		
		File file = new File(getInstance().getDataFolder() + getFilesep() + "worlds" + getFilesep() + filename + ".yml");
		YamlConfiguration conf = YamlConfiguration.loadConfiguration(file);
		conf.options().header(header);
			for (int z = 1; z <= 20; z++)
	          for (int u = 1; u <= 3; u++)
	          {
	        	  if(z == 1 && u == 1){
	        		  conf.set("lines." + z + "." + u, "&6" + firstline);
	        	  }else if(z == 1 && u == 2){
	        		  conf.set("lines." + z + "." + u, "&6" + middleline);
	        	  }else if(z == 1 && u == 3){
	        		  conf.set("lines." + z + "." + u, "&6" + leftline);
	        	  }else{
	        		  conf.set("lines." + z + "." + u, "");
	        	  }
	        	  
	        	  conf.set("lines.2.1", "%empty%");
	        	  conf.set("lines.2.2", "%empty%");
	        	  conf.set("lines.2.3", "%empty%");
	        	  
	        	  try {
					conf.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
	          }
	}
}
