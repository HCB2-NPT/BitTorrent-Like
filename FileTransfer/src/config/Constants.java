package config;

import java.nio.file.Paths;

public final class Constants {
	public final static String 	HOST 				= "192.168.43.255"; //Broadcast
	public final static int 	PORT 				= 11111;
	public final static int 	BUFFER_SIZE 		= 60000;
	public final static String 	FOLDER_SEED 		= Paths.get(System.getProperty("user.dir")) + "\\seeding file\\";
}
