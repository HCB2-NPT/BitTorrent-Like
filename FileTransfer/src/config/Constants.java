package config;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.file.Paths;

public final class Constants {
	public final static String 	HOST 				= "255.255.255.255"; //send to broadcast
	public final static byte[] 	MY_ADDRESS			= getMyAddress().getBytes();
	public final static int 	PORT 				= 11111;
	public final static int 	BUFFER_SIZE 		= 60000;
	public final static String 	FOLDER_SEED 		= Paths.get(System.getProperty("user.dir")) + "\\seeding file\\";
	
	public final static String getMyAddress(){
		try {
			return Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
