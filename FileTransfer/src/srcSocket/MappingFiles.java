package srcSocket;

import java.util.HashMap;

public final class MappingFiles {
	private static HashMap<String, DownloadingFileInfo> _map = new HashMap<String, DownloadingFileInfo>();
	
	public static HashMap<String, DownloadingFileInfo> getMap(){
		return _map;
	}
}
