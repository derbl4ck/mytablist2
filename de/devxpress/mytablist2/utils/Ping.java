package de.devxpress.mytablist2.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import de.devxpress.mytablist2.utils.types.ConnectionTypes;

public class Ping {	
	
	  private String ip;
	  private int port;
	  
	  public Ping(String ip, int port)
	  {
	    this.ip = ip;
	    this.port = port;
	  }
	  
	  public Ping(String ip)
	  {
	    this.ip = ip;
	    this.port = 25565;
	  }
	  
	  public Ping()
	  {
	    this.ip = "127.0.0.1";
	    this.port = 25565;
	  }
	  
	  public synchronized String getData(ConnectionTypes con)
	  {
		  try {
	             Socket socket = new Socket();
	             socket.connect(new InetSocketAddress(this.ip, this.port), 1 * 1000);
	            
	             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
	             DataInputStream in = new DataInputStream(socket.getInputStream());
	            
	             out.write(0xFE);
	            
	             StringBuilder str = new StringBuilder();
	            
	             int b;
	             while ((b = in.read()) != -1) {
	                     if (b != 0 && b > 16 && b != 255 && b != 23 && b != 24) {
	                             str.append((char) b);
	                     }
	             }
	            
	             String[] data = str.toString().split("§");
	             int length = data.length;
	             String onlinePlayers = data[length - 2];
	             String maxPlayers = data[length - 1];
	            socket.close();
		  
	      if (con == ConnectionTypes.PLAYERS_ONLINE) {
	        return 
	          onlinePlayers + "/" + maxPlayers;
	      }
	      if (con == ConnectionTypes.CURRENT_PLAYERS) {
	        return onlinePlayers;
	      }
	      if (con == ConnectionTypes.IS_ONLINE) {
	        return "";
	      }
	      if (con == ConnectionTypes.MOTD) {
	        return "";
	      }
	      if (con == ConnectionTypes.SERVER_VERSION) {
	        return "";
	      }
	      if (con == ConnectionTypes.MAX_PLAYERS) {
	        return maxPlayers;
	      }
	      System.out.println("Connection value not handled.");
	      return null;
	    }catch (Exception e) {}
	    
	    return null;
	  }
	  
	  public boolean isOnline()
	  {
	    String isOnline = getData(ConnectionTypes.IS_ONLINE);
	    if (isOnline != null) {
	      return true;
	    }
	    return false;
	  }
	  
	  public String getMOTD()
	  {
	    return getData(ConnectionTypes.MOTD);
	  }
	  
	  public String getPlayers()
	  {
	    return getData(ConnectionTypes.CURRENT_PLAYERS);
	  }
	  
	  public String getMaxPlayers()
	  {
	    return getData(ConnectionTypes.MAX_PLAYERS);
	  }
	  
	  public String getVersion()
	  {
	    return getData(ConnectionTypes.SERVER_VERSION);
	  }
	

}
