package srcSocket;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import config.Constants;
import helper.MessageBox;

public class Server{
	private DatagramSocket socket;
    private DatagramPacket packet;
    private boolean active = true;
    
    public void stop(){
    	active = false;
    }
    
    public boolean isActive(){
    	return active;
    }
    
    public boolean listen(){
    	try
        {
            socket = new DatagramSocket(Constants.PORT);
        }
        catch( Exception ex )
        {
            System.out.println("Problem creating socket on port: " + Constants.PORT);
        	//MessageBox.Show("Problem creating socket on port: " + Constants.PORT, "Shutdown...");
            return false;
        }
    	
    	byte[] buffer = new byte[Constants.LISTEN_BUFFER_MAXSIZE];
    	byte[] receivedData;
    	byte[] data;
    	byte[] dataType = new byte[skGlobals.dataType_Size];
    	byte[] ip;
    	byte[] ipLen = new byte[4];
    	String type;
    	String Ip;
    	
    	active = true;
        while (active)
        {
            try
            {
            	packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
                
                receivedData = packet.getData();
                
                int offset = 0;
                
                System.arraycopy(receivedData, offset, dataType, 0, dataType.length);
                type = new String(dataType);
                
                offset += dataType.length;
                
                System.arraycopy(receivedData, offset, ipLen, 0, ipLen.length);
                ip = new byte[ByteBuffer.wrap(ipLen).getInt()];
                
                offset += ipLen.length;
                
                System.arraycopy(receivedData, offset, ip, 0, ip.length);
                Ip = new String(ip);
                
                offset += ip.length;
                
                data = new byte[receivedData.length - offset];
                System.arraycopy(receivedData, offset, data, 0, data.length);
                
                switch(type){
	                case skGlobals.dataType_Request:
	                	ReceiveRequest(data, Ip);
	            		break;
	            	case skGlobals.dataType_Response:
	            		ReceiveResponse(data, Ip);
	            		break;
	            	case skGlobals.dataType_SendSeedInfo:
	            		ReceiveSeedInfo(data, Ip);
	            		break;
	            	case skGlobals.dataType_SendData_Continue:
	            		ReceiveData(data, Ip, false);
	            		break;
	            	case skGlobals.dataType_SendData_End:
	            		ReceiveData(data, Ip, true);
	            		break;
                }
            }
            catch (SocketException se)
            {
            	//
            }
            catch (IOException ie)
            {
            	//
            }
        }
        active = false;
        return true;
    }
    
    void ReceiveRequest(byte[] data, String from) throws UnsupportedEncodingException{
    	Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
    			try{
			    	System.out.println("start receive-request");
			    	System.out.println("from: " + from);
			    	System.out.println("to: " + Constants.getMyAddress());
			    	
			    	if (from != Constants.getMyAddress()){
				    	byte[] fileNameLen = new byte[4];
						System.arraycopy(data, 0, fileNameLen, 0, 4);
						int len = ByteBuffer.wrap(fileNameLen).getInt();
						
						byte[] fileName = new byte[len];
						System.arraycopy(data, 4, fileName, 0, len);
						String name = new String(fileName, "UTF-8");
						
						File f = new File(Constants.FOLDER_SEED + name);
						if (f.exists() && !f.isDirectory()){
							Client.sendResponse(from, name);
							System.out.println("send response success!");
						}
			    	}
			    	System.out.println("end receive-request");
		    	}
		    	catch (Exception e){
		    		//e.printStackTrace();
		    	}
			}
		});
    	t.start();
    }
    
	void ReceiveResponse(byte[] data, String from){
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					System.out.println("start receive-response");
					System.out.println("from: " + from);
			    	System.out.println("to: " + Constants.getMyAddress());
			    	
					byte[] fileNameLen = new byte[4];
					System.arraycopy(data, 0, fileNameLen, 0, 4);
					int len = ByteBuffer.wrap(fileNameLen).getInt();
					
					byte[] fileName = new byte[len];
					System.arraycopy(data, 4, fileName, 0, len);
					String name = new String(fileName, "UTF-8");
					
					byte[] fileLen = new byte[8];
					System.arraycopy(data, 4 + len, fileLen, 0, 8);
					long fLen = ByteBuffer.wrap(fileLen).getLong();
					
					DownloadingFileInfo dfi;
					if (MappingFiles.getMap().containsKey(name)){
						dfi = MappingFiles.getMap().get(name);
						dfi.NSeeders++;
					}else{
						dfi = new DownloadingFileInfo();
						MappingFiles.getMap().put(name, dfi);
						dfi.Name = name;
						dfi.FileLength = fLen;
						dfi.NSeeders++;
						
						//create empty file
						Misc.createTempFile(name, fLen);
					}
					
					synchronized (dfi.readLocker){
						dfi.MaxLengthForSending = (int) Math.max(Math.min(dfi.FileLength / (dfi.NSeeders * 4), 100000000), Constants.DATA_BUFFER_MAXSIZE);
						int lengthForSending = (int) Math.min(dfi.FileLength - dfi.Offset, dfi.MaxLengthForSending);
						Client.sendSeedInfo(from, name, dfi.Offset, lengthForSending);
						dfi.Offset += lengthForSending;
					}
					
					System.out.println("end receive-response");
				}
				catch (Exception e){
					//e.printStackTrace();
				}
			}
		});
    	t.start();
	}
	
	static Object readLocker = new Object();
	void ReceiveSeedInfo(byte[] data, String from){
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					System.out.println("start receive:seed-info");
					System.out.println("from: " + from);
			    	System.out.println("to: " + Constants.getMyAddress());
			    	
					byte[] fileNameLen = new byte[4];
					System.arraycopy(data, 0, fileNameLen, 0, 4);
					int len = ByteBuffer.wrap(fileNameLen).getInt();
					
					byte[] fileName = new byte[len];
					System.arraycopy(data, 4, fileName, 0, len);
					String name = new String(fileName, "UTF-8");
					
					byte[] os = new byte[8];
					System.arraycopy(data, 4 + len, os, 0, 8);
					long Offset = ByteBuffer.wrap(os).getLong();
					
					byte[] l = new byte[4];
					System.arraycopy(data, 4 + len + 8, l, 0, 4);
					int Length = ByteBuffer.wrap(l).getInt();
					
					System.out.println("end receive:seed-info");
					System.out.println("start server:send-data");
					
					//read file-data
					long off = Offset;
					long stop = Offset + Length;
					int sendLen;
					byte[] sendData;
					//BufferedInputStream input;
					while(off < stop){
						sendLen = (int) Math.min(stop - off, Constants.DATA_BUFFER_MAXSIZE);
						sendData = new byte[sendLen];
						
						synchronized (readLocker){
							BufferedInputStream input = new BufferedInputStream(new FileInputStream(Constants.FOLDER_SEED + name));
							input.skip(off);
							sendLen = input.read(sendData);
							input.close();
						}
						
						if (sendLen + off >= Length){
							Client.sendData(from, name, off, sendLen, sendData, true);
							break;
						}
						else{
							Client.sendData(from, name, off, sendLen, sendData, false);
							off += sendLen;
						}
					}
					
					System.out.println("end server:send-data");
				}
				catch (Exception e){
					//e.printStackTrace();
				}
			}
		});
		t.start();
	}
	
	void ReceiveData(byte[] data, String from, boolean isEnd){
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					System.out.println("start receive-data");
					System.out.println("from: " + from);
			    	System.out.println("to: " + Constants.getMyAddress());
			    	
					byte[] fileNameLen = new byte[4];
					System.arraycopy(data, 0, fileNameLen, 0, 4);
					int len = ByteBuffer.wrap(fileNameLen).getInt();
					
					byte[] fileName = new byte[len];
					System.arraycopy(data, 4, fileName, 0, len);
					String name = new String(fileName, "UTF-8");
					
					byte[] os = new byte[8];
					System.arraycopy(data, 4 + len, os, 0, 8);
					long Offset = ByteBuffer.wrap(os).getLong();
					
					byte[] l = new byte[4];
					System.arraycopy(data, 4 + len + 8, l, 0, 4);
					int Length = ByteBuffer.wrap(l).getInt();
					
					byte[] d = new byte[Length];
					System.arraycopy(data, 4 + len + 8 + 4, d, 0, Length);
					
					//write file-data
					if (MappingFiles.getMap().containsKey(name)){
						DownloadingFileInfo dfi = MappingFiles.getMap().get(name);
						synchronized (dfi.writeLocker){
							if (MappingFiles.getMap().containsKey(name)){
								RandomAccessFile fh = new RandomAccessFile(new File(Constants.FOLDER_SEED + Constants.PREFIX_EMPTY_FILE + name), "rw");
							    fh.seek(Offset);
							    fh.read(new byte[Length]);
							    fh.seek(Offset);
							    fh.write(d);
							    fh.close();
							    
							    dfi.Complete(Offset, Length);
								if (isEnd){
									if (dfi.Offset < dfi.FileLength){
										int lengthForSending = (int) Math.min(dfi.FileLength - dfi.Offset, dfi.MaxLengthForSending);
										Client.sendSeedInfo(from, name, dfi.Offset, lengthForSending);
										dfi.Offset += lengthForSending;
										System.out.println("send seed-info, keep seeding another data...");
									}else{
										SentData miss = dfi.getARangeLoss();
										if (miss == null){
											new File(Constants.FOLDER_SEED + Constants.PREFIX_EMPTY_FILE + name).renameTo(new File(Constants.FOLDER_SEED + name));
											MappingFiles.getMap().remove(name);
											//MessageBox.Show(name + " is downloaded!", "Notify");
											System.out.println("Download complete!");
										}else{
											int lengthForSending = (int) Math.min(miss.Length, dfi.MaxLengthForSending);
											Client.sendSeedInfo(from, name, miss.Offset, lengthForSending);
											System.out.println("Download missed data!");
										}
									}
								}
							}
						}
					}
					
					System.out.println("end receive-data");
				}
				catch (Exception e){
					//e.printStackTrace();
				}
			}
		});
		t.start();
	}
}