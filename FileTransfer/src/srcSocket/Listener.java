package srcSocket;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import config.AppConfig;
import helper.Debugger;
import javafx.collections.ObservableList;

public class Listener{
	private DatagramSocket socket;
    private DatagramPacket packet;
    private boolean active = true;
    private IListenerEvent event;
    private ObservableList<SeedFile> seedFiles;
    
    public Listener(IListenerEvent e){
    	event = e;
    }
    
    public ObservableList<SeedFile> getSeedFiles(){
    	return seedFiles;
    }
    
    public void setSeedFiles(ObservableList<SeedFile> ol){
    	seedFiles = ol;
    }
    
    public SeedFile getSeedFileBy(String name){
    	for (SeedFile seedFile : seedFiles) {
			if (seedFile.getFileName().equals(name)){
				return seedFile;
			}
		}
    	return null;
    }
    
    public void stop(){
    	active = false;
    }
    
    public boolean isActive(){
    	return active;
    }
    
    public boolean listen(){
    	try
        {
            socket = new DatagramSocket(AppConfig.PORT);
        }
        catch( Exception ex )
        {
            Debugger.log("Problem creating socket on port: " + AppConfig.PORT);
        	event.ListenFail();
            return false;
        }
    	
    	byte[] buffer = new byte[AppConfig.LISTEN_BUFFER_MAXSIZE];
    	byte[] receivedData;
    	byte[] data;
    	byte[] dataType = new byte[config.dataType_Size];
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
                
                if (Ip != AppConfig.getMyAddress()){
                	offset += ip.length;
	                data = new byte[receivedData.length - offset];
	                System.arraycopy(receivedData, offset, data, 0, data.length);
	                
	                switch(type){
		                case config.dataType_Request:
		                	ReceiveRequest(data, Ip);
		            		break;
		            	case config.dataType_Response:
		            		ReceiveResponse(data, Ip);
		            		break;
		            	case config.dataType_SendSeedInfo:
		            		ReceiveSeedInfo(data, Ip);
		            		break;
		            	case config.dataType_SendData_Continue:
		            		ReceiveData(data, Ip, false);
		            		break;
		            	case config.dataType_SendData_End:
		            		ReceiveData(data, Ip, true);
		            		break;
	                }
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
    	new Thread(new Runnable() {
			@Override
			public void run() {
    			try{
			    	Debugger.log("start receive-request");
			    	Debugger.log("from: " + from);
			    	Debugger.log("to: " + AppConfig.getMyAddress());
			    	
			    	byte[] fileNameLen = new byte[4];
					System.arraycopy(data, 0, fileNameLen, 0, 4);
					int len = ByteBuffer.wrap(fileNameLen).getInt();
					
					byte[] fileName = new byte[len];
					System.arraycopy(data, 4, fileName, 0, len);
					String name = new String(fileName, "UTF-8");
					
					SeedFile s = getSeedFileBy(name);
					if (s != null){
						Sender.sendResponse(from, name, new File(s.getFilePath()).length());
						Debugger.log("send response success!");
					}else{
						Debugger.log("can not found file: " + name);
					}
					
			    	Debugger.log("end receive-request");
		    	}
		    	catch (Exception e){
		    		//e.printStackTrace();
		    	}
			}
		}).start();
    }
    
	void ReceiveResponse(byte[] data, String from){
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
				try{
					Debugger.log("start receive-response");
					Debugger.log("from: " + from);
			    	Debugger.log("to: " + AppConfig.getMyAddress());
			    	
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
					}else{
						dfi = new DownloadingFileInfo();
						MappingFiles.getMap().put(name, dfi);
						dfi.Name = name;
						dfi.FileLength = fLen;
						
						//create empty file
						File f = Misc.createTempFile(name, fLen);
						seedFiles.add(new SeedFile(f.getName(), f.getPath()));
					}
					
					dfi.TimeStamp = System.currentTimeMillis();
					
					if (!dfi.Seeders.contains(from))
						dfi.Seeders.add(from);
					
					dfi.MaxLengthForSending = (int) Math.max(Math.min(dfi.FileLength / (dfi.Seeders.size() * 4), 100000000), AppConfig.DATA_BUFFER_MAXSIZE);
					
					synchronized (dfi.readLocker){
						if (dfi.Offset < dfi.FileLength){
							int lengthForSending = (int) Math.min(dfi.FileLength - dfi.Offset, dfi.MaxLengthForSending);
							Sender.sendSeedInfo(from, name, dfi.Offset, lengthForSending);
							dfi.Offset += lengthForSending;
						}else{
							RangeDataSent miss = dfi.getARangeLoss();
							if (miss != null){
								int lengthForSending = (int) Math.min(miss.Length, dfi.MaxLengthForSending);
								Sender.sendSeedInfo(from, name, miss.Offset, lengthForSending);
							}
						}
					}
					
					Debugger.log("end receive-response");
				}
				catch (Exception e){
					//e.printStackTrace();
				}
//			}
//		}).start();
	}
	
	static Object readLocker = new Object();
	void ReceiveSeedInfo(byte[] data, String from){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					Debugger.log("start receive:seed-info");
					Debugger.log("from: " + from);
			    	Debugger.log("to: " + AppConfig.getMyAddress());
			    	
					byte[] fileNameLen = new byte[4];
					System.arraycopy(data, 0, fileNameLen, 0, 4);
					int len = ByteBuffer.wrap(fileNameLen).getInt();
					
					byte[] fileName = new byte[len];
					System.arraycopy(data, 4, fileName, 0, len);
					String name = new String(fileName, "UTF-8");
					
					SeedFile s = getSeedFileBy(name);
					if (s != null){
					
						byte[] os = new byte[8];
						System.arraycopy(data, 4 + len, os, 0, 8);
						long Offset = ByteBuffer.wrap(os).getLong();
						
						byte[] l = new byte[4];
						System.arraycopy(data, 4 + len + 8, l, 0, 4);
						int Length = ByteBuffer.wrap(l).getInt();
						
						Debugger.log("end receive:seed-info");
						Debugger.log("start server:send-data");
						
						//read file-data
						long off = Offset;
						long stop = Offset + Length;
						int sendLen;
						byte[] sendData;
						//BufferedInputStream input;
						while(off < stop){
							sendLen = (int) Math.min(stop - off, AppConfig.DATA_BUFFER_MAXSIZE);
							sendData = new byte[sendLen];
							
							synchronized (readLocker){
								BufferedInputStream input = new BufferedInputStream(new FileInputStream(s.getFilePath()));
								input.skip(off);
								sendLen = input.read(sendData);
								input.close();
							}
							
							if (sendLen + off >= stop){
								Sender.sendData(from, name, off, sendLen, sendData, true);
								break;
							}
							else{
								Sender.sendData(from, name, off, sendLen, sendData, false);
								off += sendLen;
							}
							
							Thread.sleep(AppConfig.SENDER_DELAY);
						}
						
					}
					
					Debugger.log("end server:send-data");
				}
				catch (Exception e){
					//e.printStackTrace();
				}
			}
		}).start();
	}
	
	void ReceiveData(byte[] data, String from, boolean isEnd){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					Debugger.log("start receive-data");
					Debugger.log("from: " + from);
			    	Debugger.log("to: " + AppConfig.getMyAddress());
			    	
					byte[] fileNameLen = new byte[4];
					System.arraycopy(data, 0, fileNameLen, 0, 4);
					int len = ByteBuffer.wrap(fileNameLen).getInt();
					
					byte[] fileName = new byte[len];
					System.arraycopy(data, 4, fileName, 0, len);
					String name = new String(fileName, "UTF-8");
					
					SeedFile sf = getSeedFileBy(AppConfig.PREFIX_EMPTY_FILE + name);
					if (sf != null){
					
						byte[] os = new byte[8];
						System.arraycopy(data, 4 + len, os, 0, 8);
						long Offset = ByteBuffer.wrap(os).getLong();
						
						byte[] l = new byte[4];
						System.arraycopy(data, 4 + len + 8, l, 0, 4);
						int Length = ByteBuffer.wrap(l).getInt();
						
						byte[] d = new byte[Length];
						System.arraycopy(data, 4 + len + 8 + 4, d, 0, Length);
						
						//write file-data
						DownloadingFileInfo dfi = MappingFiles.getMap().get(name);
						if (dfi != null && dfi.isRun){
							dfi.TimeStamp = System.currentTimeMillis();
							synchronized (dfi.writeLocker){
								if (!dfi.IsReceived(Offset, Length)){
									RandomAccessFile fh = new RandomAccessFile(new File(sf.getFilePath()), "rw");
								    fh.seek(Offset);
								    fh.read(new byte[Length]);
								    fh.seek(Offset);
								    fh.write(d);
								    fh.close();
								    
								    dfi.Complete(Offset, Length);
									sf.setPrefix(String.format("%.2f", ((float)dfi.LengthDownloaded() / (float)dfi.FileLength) * 100f) + "%");
									event.ReceiveData(dfi);
								    
									if (Debugger.IsEnable){
									    for (RangeDataSent s : dfi.ListSentData) {
											Debugger.log(s.Offset + " - " + s.Length);
										}
								    }
								}
								if (isEnd){
									if (dfi.Offset < dfi.FileLength){
										int lengthForSending = (int) Math.min(dfi.FileLength - dfi.Offset, dfi.MaxLengthForSending);
										Sender.sendSeedInfo(from, name, dfi.Offset, lengthForSending);
										dfi.Offset += lengthForSending;
										Debugger.log("send seed-info, keep seeding another data...");
									}else{
										RangeDataSent miss = dfi.getARangeLoss();
										if (miss == null){
											new File(sf.getFilePath()).renameTo(new File(AppConfig.FOLDER_SEED + name));
											sf.__setFileName(name);
											sf.setPrefix("SEEDING");
											event.DownloadCompleted(dfi);
											MappingFiles.getMap().remove(name);
											Debugger.log("Download complete!");
										}else{
											int lengthForSending = (int) Math.min(miss.Length, dfi.MaxLengthForSending);
											Sender.sendSeedInfo(from, name, miss.Offset, lengthForSending);
											Debugger.log("Download missed data!");
										}
									}
								}
							}
						}
					
					}
					
					Debugger.log("end receive-data");
				}
				catch (Exception e){
					//e.printStackTrace();
				}
			}
		}).start();
	}
}