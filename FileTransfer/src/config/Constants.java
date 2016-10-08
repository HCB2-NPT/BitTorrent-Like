package config;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.file.Paths;

public final class Constants {
	public final static String 	HOST 						= "255.255.255.255"; //send to broadcast
	public final static byte[] 	MY_ADDRESS					= getMyAddress().getBytes();
	public final static int 	PORT 						= 11111;
	public final static int 	LISTEN_BUFFER_MAXSIZE 		= 65000;
	public final static int 	DATA_BUFFER_MAXSIZE 		= 60000;
	public final static int 	SENDING_DELAY				= ((DATA_BUFFER_MAXSIZE / 8192) + 1) * 4;
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
