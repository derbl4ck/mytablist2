package de.devxpress.mytablist2;

import java.lang.reflect.Field;
import org.bukkit.Bukkit;

public class PacketManager {

	private static String packageName = Bukkit.getServer().getClass().getPackage().getName();
	  private static String version = packageName.substring(packageName.lastIndexOf(".") + 1);
	  private Object packet;
	  private Class<?> nmsPacket;

	  public PacketManager(String packetName)
	    throws InstantiationException, IllegalAccessException, ClassNotFoundException
	  {
	    this.nmsPacket = Class.forName("net.minecraft.server." + version + "." + packetName);
	    this.packet = this.nmsPacket.newInstance();
	  }

	  public void setField(String fieldName, Object value)
	  {
	    try
	    {
	      Field f = this.packet.getClass().getField(fieldName);
	      f.setAccessible(true);
	      f.set(this.packet, value);
	      f.setAccessible(false);
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	  }

	  public void setDeclaredField(String fieldName, Object value)
	  {
	    try
	    {
	      Field f = this.packet.getClass().getDeclaredField(fieldName);
	      f.setAccessible(true);
	      f.set(this.packet, value);
	      f.setAccessible(false);
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	  }

	  public Object getField(String fieldName)
	  {
	    try
	    {
	      Field f = this.packet.getClass().getField(fieldName);
	      f.setAccessible(true);
	      Object s = f.get(this.packet);
	      f.setAccessible(false);
	      return s;
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    return null;
	  }

	  public Object getDeclaredField(String fieldName)
	  {
	    try
	    {
	      Field f = this.packet.getClass().getDeclaredField(fieldName);
	      f.setAccessible(true);
	      Object s = f.get(this.packet);
	      f.setAccessible(false);
	      return s;
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	    }
	    return null;
	  }

	  public Class<?> getPacketClass()
	  {
	    return this.nmsPacket;
	  }

	  public Object getPacket()
	  {
	    return this.packet;
	  }

	  public boolean isUsable()
	  {
	    return this.packet != null;
	  }
	
}
