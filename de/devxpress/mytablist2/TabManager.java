package de.devxpress.mytablist2;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.utils.WorldManager;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.PlayerSkills;

import de.devxpress.mytablist2.api.controller;
import de.devxpress.mytablist2.utils.Ping;
import de.devxpress.mytablist2.utils.log;

public class TabManager {

	  Main plugin;
	  PacketHandler packet;
	  log log;
	  controller api;
	  private static TabManager instance;
	  public Map<Player, List<String>> playerTablists = new HashMap<Player, List<String>>();
	  private List<Player> onlineplayers = new ArrayList<Player>();
	  private final LinkedList<Double> loggedTps = new LinkedList<Double>();
	  private long lastCall = getMillis() - 3000L;
	  private float tps = 20.0F;
	  private int interval = 40;
	  private int empty_c = 0;
	  private int players_c = 0;
	  private int emptyping = 1000; //500 = 3 balken;

	  public TabManager(Main plugin)
	  {
	    instance = this;
	    this.plugin = plugin;
	    this.packet = new PacketHandler(plugin);
	    this.api = plugin.getAPI();
	  }

	  public int lastindex(List<String> tablist){
		  int last = 0;
		  for(int i = tablist.size(); i > 0; i-- ){
			  String p = tablist.get(i-1);
			  if(!p.isEmpty()){
				  last = i-1;
				  i = 0;
			  }
		  }
		  return last;
	  }
	  
	  public void playerlist(Player p, boolean online){
		  if(online == true){
			  onlineplayers.add(p);
		  } else {
			  onlineplayers.remove(p);
		  }
	  }
	  
	  public void setTablist(Player player){
		  /*try {
			  tab18.getInstance().sendHF(player, plugin.getTabConfHeader(), plugin.getTabConfFooter());
		  }catch(Exception e){
			  e.printStackTrace(); //TODO NOT SHOW THIS UNTIL ERRORS ARE FIXED
		  }*/
		  
		  if (!this.playerTablists.containsKey(player)){
			  empty_c = 0;
			  players_c = 0;
			  int ping = 0;
			  List<Player> pls = new ArrayList<Player>();
			      
			      for (Player p : onlineplayers){
			    	  if(player.canSee(p)){
			    		  pls.add(p);
			    	  }
			      }
			      List<Object> packets = new ArrayList<>();
			      List<String> output = new ArrayList<>();
			      int i = 0;
			      int last = lastindex(getTabListContent(player));
			      for (String slot : getTabListContent(player)){
			    	  	if(slot.isEmpty()){
			    	  		if(players_c < pls.size()){
			    	  			String playername;
			    	  			if(plugin.useDisplayNames()){
			    	  				playername = pls.get(players_c).getDisplayName();
			    	  			}else{
			    	  				playername = pls.get(players_c).getPlayerListName();
			    	  			}
								  slot = playername;
								  ping = getPing(pls.get(players_c));
								  players_c++;
						  } else {
							  if(plugin.isPingseption()){
								  slot = empty(empty_c);
								  	ping = emptyping;
							  } else {
								  if(last > i-1){	
								  	slot = empty(empty_c);
								  	ping = emptyping;
								  } else {
									  slot = null;
								  }
							  }
						  }
			    	  	} else {
			    	  		if(slot.contains("%empty%")){
			    	  		    slot = slot.replace("%empty%", empty(empty_c));
			    	  		    ping = emptyping;
			    	  		}
			      		}
			    	  	
			    	  	if(slot != null){
					        slot = userValues(slot, player);
					        slot = textValues(slot);
							slot = controller.replace(slot, player);										
							
							output.add(editText(slot));
							packets.add(this.packet.createTablistPacket(editText(slot), true, ping));
						}
						
						i++;
			      }
			      this.playerTablists.put(player, output);
			      this.packet.sendPackets(player, packets);
		  }
	  }

	  public void refreshTablist(Player player){
		 removeTablist(player);
		 setTablist(player);
	  }

	/*
	 * TODO We must remove the tablist fully -> dubble player names
	 */
	public void removeTablist(Player player){
		 if (this.playerTablists.containsKey(player)){
		    List<Object> packets = new ArrayList<Object>();

		      for(Player p : Bukkit.getOnlinePlayers()){
				  packets.add(this.packet.createTablistPacket(p.getPlayerListName(), false, getPing(p)));
			  }
			for(String slot : playerTablists.get(player)){
				packets.add(this.packet.createTablistPacket(editText(slot), false));
			}
		    this.packet.sendPackets(player, packets);
		    this.playerTablists.remove(player);	
		}
		
	}

	  public Map<Player, List<String>> getPlayerTablists()
	  {
	    return this.playerTablists;
	  }

	  public String textValues(String line){
	    line = line.replaceAll("%sc1", "▄ █ █ █ ▀");
	    line = line.replaceAll("%sc2", "▀ █ █ █ ▄");
	    line = line.replaceAll("%sc3", "███▓▒░░");
	    line = line.replaceAll("%sc4", "░░▒▓███");
	    line = line.replaceAll("%sc5", "▁ ▂ ▃ ▄ ▅ ▆ █");
	    line = line.replaceAll("%sc6", "█ ▆ ▅ ▄ ▃ ▂ ▁");
	    line = line.replaceAll("%sc7", "★");
	    line = line.replaceAll("%sc8", "♬");
	    line = line.replaceAll("%sc9", "☆");
	    line = line.replaceAll("%sc10", "►");
	    line = line.replaceAll("%sc11", "◄");
	    line = line.replaceAll("%sc12", "█");
	    line = line.replaceAll("%sc13", "☺");
	    line = line.replaceAll("%sc14", "ღ");
	    line = line.replaceAll("%sc15", "㋛");
	    line = line.replaceAll("%sc16", "◣_◢");
	    line = line.replaceAll("%sc17", "¸¸.•*¨*•♫");
	    line = line.replaceAll("%sc18", "♪•*¨*•.¸¸");
	    line = line.replaceAll("%sc19", "|̶̿ ̶̿ ̶̿ ̶̿'");
	    line = line.replaceAll("%sc20", "▄▄ ■■ ▀▀");
	    line = line.replaceAll("%sc21", "▀▀ ■■ ▄▄");
	    line = line.replaceAll("%sc22", "▼ ▼ ▼");
	    line = line.replaceAll("%sc23", "■ □ ■");
	    line = line.replaceAll("%sc24", "□ ■ □");
	    line = line.replaceAll("%sc25", "█ █ █");
	    line = line.replaceAll("%a", "ä");
	    line = line.replaceAll("%A", "Ä");
	    line = line.replaceAll("%o", "ö");
	    line = line.replaceAll("%O", "Ö");
	    line = line.replaceAll("%u", "ü");
	    line = line.replaceAll("%U", "Ü");
	    line = line.replaceAll("%s", "ß");
	    line = ChatColor.translateAlternateColorCodes('&', line);
	    return line;
	  }

	  @SuppressWarnings("deprecation")
	public String userValues(String line, Player player){
	    Calendar cal = Calendar.getInstance();

	    String d = new SimpleDateFormat("dd_MM_yyyy").format(Calendar.getInstance().getTime());
	    String[] date = d.split("_");

	    String ip_adress = player.getAddress().getAddress().toString();
	    ip_adress = ip_adress.replaceAll("/", "");

	    PluginDescriptionFile descFile = this.plugin.getDescription();
	    String version = descFile.getVersion();

	    String bukkitVersion = this.plugin.getBukkitVersion();
	    String serverVersion = this.plugin.getServerVersion();
	    	
	    Long free = Long.valueOf(Runtime.getRuntime().freeMemory() / 1024L / 1024L);
	    Long max = Long.valueOf(Runtime.getRuntime().maxMemory() / 1024L / 1024L);
	    Long used = max - free;
	    int percent = (int) (used * 100 / max);
	    
	    line = line.replaceAll("%username%", player.getName());
	    line = line.replaceAll("%displayname%", player.getDisplayName());
	    line = line.replaceAll("%numplayers%", String.valueOf(this.plugin.getServer().getOnlinePlayers().length));
	    line = line.replaceAll("%maxplayers%", String.valueOf(this.plugin.getServer().getMaxPlayers()));
	    line = line.replaceAll("%ip-adress%", ip_adress);
	    line = line.replaceAll("%ping%", String.valueOf(getPing(player)));
	    line = line.replaceAll("%day%", fixFormat(String.valueOf(date[0])));
	    line = line.replaceAll("%month%", fixFormat(String.valueOf(date[1])));
	    line = line.replaceAll("%year%", fixFormat(String.valueOf(date[2])));
	    line = line.replaceAll("%hours%", fixFormat(String.valueOf(cal.getTime().getHours())));
	    line = line.replaceAll("%minutes%", fixFormat(String.valueOf(cal.getTime().getMinutes())));
	    line = line.replaceAll("%seconds%", fixFormat(String.valueOf(cal.getTime().getSeconds())));
	    line = line.replaceAll("%version%", version);
	    line = line.replaceAll("%bukkitversion%", bukkitVersion);
	    line = line.replaceAll("%serverversion%", serverVersion);
	    line = line.replaceAll("%servername%", this.plugin.getServer().getServerName());
	    line = line.replaceAll("%serverip%", this.plugin.getServer().getIp());
	    line = line.replaceAll("%motd%", this.plugin.getServer().getMotd());
	    line = line.replaceAll("%var1%", con("Customvars.1"));
	    line = line.replaceAll("%var2%", con("Customvars.2"));
	    line = line.replaceAll("%var3%", con("Customvars.3"));
	    line = line.replaceAll("%var4%", con("Customvars.4"));
	    line = line.replaceAll("%var5%", con("Customvars.5"));
	    line = line.replaceAll("%player1%", checkplayer(cp(1)));
	    line = line.replaceAll("%player2%", checkplayer(cp(2)));
	    line = line.replaceAll("%player3%", checkplayer(cp(3)));
	    line = line.replaceAll("%player4%", checkplayer(cp(4)));
	    line = line.replaceAll("%player5%", checkplayer(cp(5)));
	    line = line.replaceAll("%world%", player.getWorld().getName());
	    line = line.replaceAll("%maxmem%", String.valueOf(max) + "MB");
	    line = line.replaceAll("%freemem%", String.valueOf(free) + "MB");
	    line = line.replaceAll("%curmem%", max - free + "MB");
	    line = line.replaceAll("%permem%", percent + "%");
	    line = line.replaceAll("%permem2%", bar(percent));
	    line = line.replaceAll("%tps%", String.valueOf(gettpss()));
	    line = line.replaceAll("%logins%", String.valueOf(this.plugin.uniqueLogins));
	    line = line.replaceAll("%lag-percent%", String.valueOf(gettpss()/20*100));
	   
	   
	    if(line.contains("%ping!")){
	    
	    	String st = "%ping";
	    	String l = line;
	    	int p = l.indexOf(st);
	    	String af = l.substring(p); //comand	    	
	    	String work = af.substring(p + 1); //ohne erstes %
	    	int last = work.indexOf("%");
	    	String wo = work.substring(0, last);	    	
		      String[] spl = wo.split("!");
		      
		      String rep = " ";
		      if(spl[3].equalsIgnoreCase("online")){
		    	  Ping ping = new Ping(spl[1], Integer.valueOf(spl[2]));
		    	  rep = ping.getPlayers();
		      }else if(spl[3].equalsIgnoreCase("max")){
		    	  Ping ping = new Ping(spl[1], Integer.valueOf(spl[2]));
		    	  rep = ping.getMaxPlayers();
		      }
		      line = line.replaceAll(af, String.valueOf(rep));
	    }
	      
	    if(Main.useVault()){
	    	if(Main.economy != null && Main.economy.isEnabled()){
		    	double money = Main.economy.getBalance((OfflinePlayer) player);
		    	String currency = "";
		    	if (money == 1){
		    		currency = Main.economy.currencyNameSingular();
		    	} else {
		    		currency = Main.economy.currencyNamePlural();
		    	}
		    	line = line.replaceAll("%money%", String.valueOf(money));
		    	line = line.replaceAll("%currency%", String.valueOf(currency));
	    	}
	    	if(Main.permission != null && Main.permission.isEnabled()){
		    	if(Main.permission.getGroups().length > 0){
			    	if(Main.permission.getPlayerGroups(player).length > 0){
			    		String group = Main.permission.getPrimaryGroup(player);
			    		line = line.replaceAll("%group%", group);
			    		line = line.replaceAll("%groupprefix%", Main.chat.getGroupPrefix(player.getWorld(), group));
			    		line = line.replaceAll("%groupsuffix%", Main.chat.getGroupSuffix(player.getWorld(), group));
			    	}
		    	}
	    	}
	    	
	    	line = line.replaceAll("%group1.1%", groups(0, 1));
	    	line = line.replaceAll("%group1.2%", groups(1, 1));
	    	line = line.replaceAll("%group1.3%", groups(2, 1));
	    	line = line.replaceAll("%group1.4%", groups(3, 1));
	    	line = line.replaceAll("%group1.5%", groups(4, 1));
	    	
	    	line = line.replaceAll("%group2.1%", groups(0, 2));
	    	line = line.replaceAll("%group2.2%", groups(1, 2));
	    	line = line.replaceAll("%group2.3%", groups(2, 2));
	    	line = line.replaceAll("%group2.4%", groups(3, 2));
	    	line = line.replaceAll("%group2.5%", groups(4, 2));
	    }
	    
	    if(plugin.checkmultiverse() == true){
			line = line.replaceAll("%mvalias%", getAliasForWorld(player.getWorld()));
	    }
	    
	    if(plugin.hasSkillAPI() == true){
	    	PlayerSkills pskill = new PlayerSkills(getSkillAPI(), player.getName());
	    	line = line.replaceAll("%skillapimana%", String.valueOf(pskill.getMana()));
	    	line = line.replaceAll("%skillapimanamax%", String.valueOf(pskill.getMaxMana()));
	    	line = line.replaceAll("%skillapiexp%", String.valueOf(pskill.getExp()));
	    	line = line.replaceAll("%skillapineedexp%", String.valueOf(pskill.getExpToNextLevel()));
	    	line = line.replaceAll("%skillapilevel%", String.valueOf(pskill.getLevel()));
	    	line = line.replaceAll("%skillapipoints%", String.valueOf(pskill.getPoints()));
	    	line = line.replaceAll("%skillapiclassname%", String.valueOf(pskill.getClassName()));
	    }
	    
	    return line;
	  }
	  
	  
	  String bar(int i){
		  int bars = i / 10;
		  String o = "";
		  
		  for(int w = 0; bars > w; w++){
			  o = o + "|";
		  }
		  
		  return o;
	  }

	  private String con(String s){
		  	return this.plugin.getConfig().getString(s);
	  }
	  
	  public String cp(int i){
		    String checkplayername = "";
		    if(con("Players." + i) != ""){ checkplayername = con("Players." + i); }else{ checkplayername = "Default"; }
		    return checkplayername;
	  }
	  
	  public String checkplayer(String p){
		  String msg = "";
		  int online = 0;
		  for (Player p1 : Bukkit.getOnlinePlayers()){
			  if(p1.getName().contains(p)){
				  online = 1;
			  }
		  }
		  
		  if(online == 1){
			  msg = ChatColor.GREEN + "Online";
		  }else{
			  msg = ChatColor.GRAY + "Offline";
		  }
		  
		  return msg;
	  }
	
	public String groups(int s, int i){
		  String group = "";
		  String[] pgroups;
		  
		 try {
			 if(Main.permission.getGroups().length > 0){ //TODO
			  
			  List<String> players = new ArrayList<String>();
		  
				  if(con("Groups." + i) != ""){ group = con("Groups." + i); }else{ group = "Default"; }
				  
					  for(Player player : Bukkit.getOnlinePlayers()){
						  pgroups = Main.permission.getPlayerGroups(player);
						  
						  for(int t = 0; pgroups.length > t; t++){
							  if(pgroups[t].contains(group)){
								  players.add(player.getName());
							  }
						  }
					  }
					  
					  if(players.size() > 0){
						  if(s < players.size()){
							  if(players.get(s).length() > 0){
								  return players.get(s);
							  }}
					  }
			  }
		 }catch(Exception e){}
		  
		  return " ";
	  }
	  
	  private int getPing(Player player){
	    try{
	      Object nms_player = player.getClass().getMethod("getHandle", new Class[0]).invoke(player, new Object[0]);
	      Field fieldPing = nms_player.getClass().getDeclaredField("ping");
	      fieldPing.setAccessible(true);
	      return fieldPing.getInt(nms_player);
	    }
	    catch (Exception e) {
	    }
	    return 38;
	  }

	  private String fixFormat(String input){
	    if (input.length() == 1){
	      input = "0" + input;
	    }
	    return input;
	  }
	  
	  public String getAliasForWorld(World world){
		  WorldManager wm = new WorldManager(getMultiverseCore());
		  if(world != null){
		    	 MultiverseWorld mvWorld = wm.getMVWorld(world);
			    if(mvWorld != null){
			    	return "" + mvWorld.getAlias(); //getColoredWorldString();
			    }else{
			    	return "" + world.getName();
			    }
		  }else{
			  return "Default";
		  }
	  }
	  
	  public MultiverseCore getMultiverseCore() {
	        Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("Multiverse-Core");
	 
	        if (plugin instanceof MultiverseCore) {
	            return (MultiverseCore) plugin;
	        }
	 
	        throw new RuntimeException("MultiVerse not found!");
	  }
	  
	  public SkillAPI getSkillAPI() {
	        Plugin plugin = this.plugin.getServer().getPluginManager().getPlugin("SkillAPI");
	 
	        if (plugin instanceof SkillAPI) {
	            return (SkillAPI) plugin;
	        }
	 
	        throw new RuntimeException("SkillAPI not found!");
	    }
	  
	  /*public void addTps(Float tps)
	  {
	    if ((tps != null) && (tps.floatValue() <= 20.0F)) {
	      this.loggedTps.add(tps);
	    }
	    if (this.loggedTps.size() > 10)
	      this.loggedTps.poll();
	  }*/

	  public final double getAverageTps()
	  {
		  loggedTps.add(Double.valueOf(20.0D));
		  
	   /* float amount = 0.0F;
	    for (Float f : this.loggedTps) {
	      if (f != null) {
	        amount += f.floatValue();
	      }
	    }
	    return amount / this.loggedTps.size();*/
		  double avg = 0.0D;
		  
		  for (Double f : this.loggedTps)
		    {
		      if (f != null)
		      {
		        avg += f.doubleValue();
		      }
		    }
		    return avg / this.loggedTps.size();
	  }

	  public long getMillis()
	  {
	    return System.currentTimeMillis();
	  }

	  public final float getTps()
	  {
	    return this.tps;
	  }

	  public final void tpsmanager()
	  {
	    long currentTime = getMillis();
	    long spentTime = (currentTime - this.lastCall) / 1000L;
	    if (spentTime == 0L) {
	      spentTime = 1L;
	    }

	    float calculatedTps = (float)(this.interval / spentTime);
	    if (calculatedTps > 20.0F) {
	      calculatedTps = 20.0F;
	    }

	    setTps(calculatedTps);
	    //addTps(Float.valueOf(calculatedTps));
	  }
	  
	  private void setTps(float newTps)
	  {
	    this.tps = newTps;
	  }
	  
	  private double gettpss(){
		  tpsmanager();
		  double tps = this.getAverageTps();
			  @SuppressWarnings("unused")
			ChatColor color;
			    
			  if (tps >= 18.0D)
			    {
			      color = ChatColor.GREEN;
			    }
			    else
			    {
			      if (tps >= 15.0D)
			      {
			        color = ChatColor.YELLOW;
			      }
			      else
			      {
			        color = ChatColor.RED;
			      }
			    }
			  
			  return tps;
	  }

	  public String editText(String text)
	  {
	    int length = text.length();

	    if (length > 16)
	    {
	      text = text.substring(0, 16);
	    }

	    length = text.length();

	    if (length != 16)
	    {
	      length = 16 - length;

	      for (int i = 0; i < length; i++)
	      {
	        text = text + " ";
	      }
	    }

	    return text;
	  }

	  public static TabManager getInstance()
	  {
	    return instance;
	  }
	  
	  public ArrayList<String> getTabListContent(Player player) {
			List<String> liste = new ArrayList<String>();
			  
			  for (int z = 1; z <= 20; z++)
		          for (int s = 1; s <= 3; s++)
		          {
		        	  liste.add(Main.getWorldConfigs(player, "lines." + z + "." + s));
		          }
			  
			  return (ArrayList<String>) liste;
	  }
	  
	  String empty(int i){
		  String string = ""; 
		  switch (i) {
		  	case 1: string = "&r &0"; break;
		  	case 2: string = "&0 &r "; break;
		  	case 3: string = "&1 &r "; break;
		  	case 4: string = "&2 &r "; break;
		  	case 5: string = "&3 &r "; break;
		  	case 6: string = "&4 &r "; break;
		  	case 7: string = "&5 &r "; break;
		  	case 8: string = "&6 &r "; break;
		  	case 9: string = "&7 &r "; break;
		  	case 10: string = "&8 &r "; break;
		  	case 11: string = "&9 &r "; break;
		  	case 12: string = "&a &r "; break;
		  	case 13: string = "&b &r "; break;
		  	case 14: string = "&c &r "; break;
		  	case 15: string = "&d &r "; break;
		  	case 16: string = "&e &r "; break;
		  	case 17: string = "&f &r "; break;
		  	case 18: string = "&k &r "; break;
		  	case 19: string = "&l &r "; break;
		  	case 20: string = "&f &2 &r "; break;
		  	case 21: string = "&o &r "; break;
		  	case 22: string = "&f &3 &r "; break;
		  	case 23: string = "&0 &1 &r "; break;
		  	case 24: string = "&1 &2 &r "; break;
		  	case 25: string = "&2 &3 &r "; break;
		  	case 26: string = "&3 &4 &r "; break;
		  	case 27: string = "&4 &5 &r "; break;
		  	case 28: string = "&5 &6 &r "; break;
		  	case 29: string = "&6 &7 &r "; break;
		  	case 30: string = "&7 &8 &r "; break;
		  	case 31: string = "&8 &9 &r "; break;
		  	case 32: string = "&9 &a &r "; break;
		  	case 33: string = "&a &b &r "; break;
		  	case 34: string = "&b &c &r "; break;
		  	case 35: string = "&c &d &r "; break;
		  	case 36: string = "&d &e &r "; break;
		  	case 37: string = "&e &f &r "; break;
		  	case 38: string = "&f &k &r "; break;
		  	case 39: string = "&k &l &r "; break;
		  	case 40: string = "&l &l &r "; break;
		  	case 41: string = "&l &o &r "; break;
		  	case 42: string = "&o &o &r "; break;
		  	case 43: string = "&0 &1 &2 &r "; break;
		  	case 44: string = "&1 &2 &3 &r "; break;
		  	case 45: string = "&2 &3 &4 &r "; break;
		  	case 46: string = "&3 &4 &5 &r "; break;
		  	case 47: string = "&4 &5 &6 &r "; break;
		  	case 48: string = "&5 &6 &7 &r "; break;
		  	case 49: string = "&6 &7 &8 &r "; break;
		  	case 50: string = "&7 &8 &9 &r "; break;
		  	case 51: string = "&8 &9 &a &r "; break;
		  	case 52: string = "&9 &a &b &r "; break;
		  	case 53: string = "&a &b &c &r "; break;
		  	case 54: string = "&b &c &d &r "; break;
		  	case 55: string = "&c &d &e &r "; break;
		  	case 56: string = "&d &e &f &r "; break;
		  	case 57: string = "&e &f &k &r "; break;
		  	case 58: string = "&f &k &l &r "; break;
		  	case 59: string = "&k &l &l &r "; break;
		  	case 60: string = "&l &f &o &r "; break;
		  	
		}
		  
		  empty_c++;
		  return string;
	  }
	
	  @SuppressWarnings("unused")
	private List<String> emptyer(){
		  List<String> local = new ArrayList<String>();
		  
		  local.add("&r &0"); //0
		  local.add("&0 &r ");
		  local.add("&1 &r ");
		  local.add("&2 &r ");
		  local.add("&3 &r ");
		  local.add("&4 &r ");
		  local.add("&5 &r ");
		  local.add("&6 &r ");
		  local.add("&7 &r ");
		  local.add("&8 &r "); //10
		  local.add("&9 &r ");
		  local.add("&a &r ");
		  local.add("&b &r ");
		  local.add("&c &r ");
		  local.add("&d &r ");
		  local.add("&e &r ");
		  local.add("&f &r ");
		  local.add("&k &r ");
		  local.add("&l &r ");
		  local.add("&f &2 &r ");//20
		  local.add("&o &r ");
		  local.add("&f &3 &r ");
		  local.add("&0 &1 &r ");
		  local.add("&1 &2 &r ");
		  local.add("&2 &3 &r ");
		  local.add("&3 &4 &r ");
		  local.add("&4 &5 &r ");
		  local.add("&5 &6 &r ");
		  local.add("&6 &7 &r ");
		  local.add("&7 &8 &r "); //30
		  local.add("&8 &9 &r ");
		  local.add("&9 &a &r ");
		  local.add("&a &b &r ");
		  local.add("&b &c &r ");
		  local.add("&c &d &r ");
		  local.add("&d &e &r ");
		  local.add("&e &f &r ");
		  local.add("&f &k &r ");
		  local.add("&k &l &r ");
		  local.add("&l &l &r "); //40
		  local.add("&l &o &r ");
		  local.add("&o &o &r ");
		  local.add("&0 &1 &2 &r ");
		  local.add("&1 &2 &3 &r ");
		  local.add("&2 &3 &4 &r ");
		  local.add("&3 &4 &5 &r ");
		  local.add("&4 &5 &6 &r ");
		  local.add("&5 &6 &7 &r ");
		  local.add("&6 &7 &8 &r ");
		  local.add("&7 &8 &9 &r "); //50
		  local.add("&8 &9 &a &r ");
		  local.add("&9 &a &b &r ");
		  local.add("&a &b &c &r ");
		  local.add("&b &c &d &r ");
		  local.add("&c &d &e &r ");
		  local.add("&d &e &f &r ");
		  local.add("&e &f &k &r ");
		  local.add("&f &k &l &r ");
		  local.add("&k &l &l &r ");
		  local.add("&l &f &o &r ");//60
		  
		  return local;
	  }
}
