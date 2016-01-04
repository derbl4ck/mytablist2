package de.devxpress.mytablist2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

public class PacketHandler {

	Main plugin;
	private Class<?> packetPlayOutPlayerInfo;
	private Class<?> playerInfoData;
	private Method getPlayerHandle;
	private Field getPlayerConnection;
	private Method sendPacket;

	  public PacketHandler(Main plugin)
	  {
	    try
	    {
	    	this.plugin = plugin;
	  	    this.packetPlayOutPlayerInfo = getMCClass("PacketPlayOutPlayerInfo");
	  	    this.getPlayerHandle = getCraftClass("entity.CraftPlayer").getMethod("getHandle", new Class[0]);
	  	    this.getPlayerConnection = getMCClass("EntityPlayer").getDeclaredField("playerConnection");
	  	    this.sendPacket = getMCClass("PlayerConnection").getMethod("sendPacket", new Class[] { getMCClass("Packet") });
	    }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    }
	    
	    try {
	    	this.playerInfoData = getMCClass("PlayerInfoData", false);
	    }catch(Exception e){
	    	
	    }
	  }
	  
	  public Object createTablistPacket(String text, boolean cancel, int ping)
	  {
	    try
	    {
	      Object packet = this.packetPlayOutPlayerInfo.newInstance();
	      Field a = this.packetPlayOutPlayerInfo.getDeclaredField("a");
	      a.setAccessible(true);
	      a.set(packet, text);
	      Field b = this.packetPlayOutPlayerInfo.getDeclaredField("b");
	      b.setAccessible(true);
	      b.set(packet, Boolean.valueOf(cancel));
	      Field c = this.packetPlayOutPlayerInfo.getDeclaredField("c");
	      c.setAccessible(true);
	      c.set(packet, ping);
	      return packet;
	    } catch (Exception localException) {
	    	try {
	    		try {
	  			  Object packet = this.packetPlayOutPlayerInfo.newInstance();
	  		
	  		      Field a = this.packetPlayOutPlayerInfo.getDeclaredField("action");
	  		      a.setAccessible(true);
	  		      a.set(packet, 0);
	  		      Field b = this.packetPlayOutPlayerInfo.getDeclaredField("username");
	  		      b.setAccessible(true);
	  		      b.set(packet, text);
	  		      Field c = this.packetPlayOutPlayerInfo.getDeclaredField("ping");
	  		      c.setAccessible(true);
	  		      c.set(packet, ping);
	  		      
	  		      return packet;
	  		  }catch(Exception e){
	  			  try {
	  				  Object pid = this.playerInfoData.newInstance(); //Instantiation Exception : spigot 1.8
  					
	  			      Field c = this.playerInfoData.getDeclaredField("b");
	  			      c.setAccessible(true);
	  			      c.set(pid, ping);
	  				  
	  			      Field d = this.playerInfoData.getDeclaredField("e");
	  			      d.setAccessible(true);
	  			      d.set(pid, text);
	  				  
	  			      final List<Object> a = Lists.newArrayList();
	  				  a.add(pid);
	  				  
	  				  Object packet = this.packetPlayOutPlayerInfo.newInstance();
	  					
	  			      Field b = this.packetPlayOutPlayerInfo.getDeclaredField("b");
	  			      b.setAccessible(true);
	  			      b.set(packet, a);
	  			      
	  			    
	  			      /*
	  			       * this.b.add(new PlayerInfoData(this, localEntityPlayer.getProfile(), localEntityPlayer.ping, localEntityPlayer.playerInteractManager.getGameMode(), localEntityPlayer.getPlayerListName()));
	  			       */
	  			      
	  			      
	  			      return packet;
	  			  }catch(Exception x){
	  				  e.printStackTrace();
	  				  x.printStackTrace();
	  				  return null;
	  			  }
	  		  }
	    	}catch(Exception e){
	    		localException.printStackTrace();
	    		e.printStackTrace();
	    	}
	    }
	    return null;
	  }

	  public Object createTablistPacket(String text, boolean cancel)
	  {
	    return createTablistPacket(text, cancel, 0);
	  }

	  public void sendPackets(Player player, List<Object> packets)
	  {
	    try
	    {
	      for (Iterator<Object> localIterator = packets.iterator(); localIterator.hasNext(); ) { Object packet = localIterator.next();

	        this.sendPacket.invoke(this.getPlayerConnection.get(this.getPlayerHandle.invoke(player, new Object[0])), new Object[] { packet });
	      }
	    }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    	plugin.getLogger().info("An error has occurred whilst sending the packets. Is Bukkit up to date? #rj32");
	    }
	  }

	  public void sendPackets(List<Object> packets)
	  {
	    try
	    {
	      for (Player player : Bukkit.getOnlinePlayers())
	      {
	        for (Iterator<Object> localIterator = packets.iterator(); localIterator.hasNext(); ) { Object packet = localIterator.next();

	          this.sendPacket.invoke(this.getPlayerConnection.get(this.getPlayerHandle.invoke(player, new Object[0])), new Object[] { packet });
	        }
	      }
	    }
	    catch (Exception e)
	    {
	    	plugin.getLogger().info("An error has occurred whilst sending the packets. Is Bukkit up to date? #rj47");
	    }
	  }
	  
	  public Class<?> getMCClass(String name){
		  return getMCClass(name, true);
	  }
	  
	  public Class<?> getMCClass(String name, Boolean log)
	  {
	    try {
		  String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
		  String className = "net.minecraft.server." + version + name;
		  return Class.forName(className);
	    }catch(ClassNotFoundException e){
	    	plugin.getLogger().info("MyTablist cant find internal server packages! #rj21");
	    	//e.printStackTrace();
	    }
		return null;
	  }

	  public Class<?> getCraftClass(String name)
	  {
		  try {
			  String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
			  String className = "org.bukkit.craftbukkit." + version + name;
			  return Class.forName(className);
		    }catch(ClassNotFoundException e){
		    	plugin.getLogger().info("MyTablist cant find internal server packages! #rj22");
		    	//e.printStackTrace();
		    }
		return null;
	  }
	
	  public Class<?> getBungeeClass(String name)
	  {
		  try {
			  String className = "net.md_5.bungee." + name;
			  return Class.forName(className);
		    }catch(ClassNotFoundException e){
		    	plugin.getLogger().info("MyTablist cant find internal server packages!");
		    	//e.printStackTrace();
		    }
		return null;
	  }
}
