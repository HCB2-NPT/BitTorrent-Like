package srcSocket;

import java.io.*;

import config.AppConfig;

public final class Misc {
	public static byte[] Int2Bytes(int i)
    {
      byte[] result = new byte[4];

      result[0] = (byte) (i >> 24);
      result[1] = (byte) (i >> 16);
      result[2] = (byte) (i >> 8);
      result[3] = (byte) (i);

      return result;
    }
	
	public static byte[] Long2Bytes(long i)
    {
	  byte[] result = new byte[8];
	
	  result[0] = (byte) (i >> 56);
	  result[1] = (byte) (i >> 48);
	  result[2] = (byte) (i >> 40);
	  result[3] = (byte) (i >> 32);
	  result[4] = (byte) (i >> 24);
	  result[5] = (byte) (i >> 16);
	  result[6] = (byte) (i >> 8);
	  result[7] = (byte) (i);
	
	  return result;
    }
	
	public static File createTempFile(String fileName, long len) throws IOException{
		File f = new File(AppConfig.FOLDER_SEED + AppConfig.PREFIX_EMPTY_FILE + fileName);
		//delete file if exist
		f.deleteOnExit();
		//create file
		FileOutputStream s = new FileOutputStream(f);
		int k = (int) Math.min(1000000, len);
		byte[] buf = new byte[k];
		while (len >= 1000000){
			s.write(buf);
			len -= k;
		}
		if (len > 0)
			s.write(new byte[(int) len]);
		s.flush();
		s.close();
		return f;
	}
}
