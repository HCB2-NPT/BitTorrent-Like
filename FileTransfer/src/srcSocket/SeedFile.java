package srcSocket;

import config.AppConfig;

public class SeedFile {
	private String fileName = null;
	private String filePath = null;
	private String prefix = null;
	
	public SeedFile(String name, String path){
		fileName = name;
		filePath = path;
		prefix = "SEEDING";
	}
	
	public String getFileName(){
		return fileName;
	}
	
	public void __setFileName(String name){
		fileName = name;
	}
	
	public String getFilePath(){
		return filePath;
	}
	
	public void setPrefix(String pr){
		prefix = pr;
	}
	
	@Override
	public String toString() {
		if (fileName.indexOf(AppConfig.PREFIX_EMPTY_FILE) == 0)
    		return String.format("%2$8s | %1$s", fileName, prefix);
    	else
    		return String.format("%2$8s | %1$s", fileName, prefix);
	}
}
