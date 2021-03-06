package config;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.file.Paths;

public final class AppConfig {
	public final static String 	APP_NAME					= "BitTorrent-Like";
	public final static String 	APP_ICON					= "appicon.png";
	
	public final static String 	HOST 						= "255.255.255.255"; //send to broadcast
	public final static byte[] 	MY_ADDRESS					= getMyAddress().getBytes();
	public final static int 	PORT 						= 12346;
	public final static int 	DATA_BUFFER_MAXSIZE 		= 20000;
	public final static int 	LISTEN_BUFFER_MAXSIZE 		= 30000;
	public final static int		REPAIR_DELAY				= 5000;
	public final static int		REPAIR_TIMER				= 1000;
	public final static int 	SENDER_DELAY				= ((DATA_BUFFER_MAXSIZE / 4096) + 1) * 8;
	public final static String 	FOLDER_SEED 				= Paths.get(System.getProperty("user.dir")) + "\\seeding files\\";
	public final static String 	PREFIX_EMPTY_FILE			= "DOWNLOADING-";
	
	public final static String getMyAddress(){
		try {
			return Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			//e.printStackTrace();
		}
		return null;
	}
}
