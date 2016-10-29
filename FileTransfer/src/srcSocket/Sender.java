package srcSocket;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

import config.AppConfig;
import helper.Debugger;

public final class Sender{
	public static void AutoRepair(){
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				for (DownloadingFileInfo dfi : MappingFiles.getMap().values()) {
					long cur = System.currentTimeMillis();
					if (dfi.TimeStamp + AppConfig.REPAIR_DELAY < cur){
						dfi.Seeders.clear();
						sendRequest(dfi.Name);
						dfi.TimeStamp = cur;
					}
				}
			}
		}, 0, AppConfig.REPAIR_TIMER);
	}
	
    public static void sendRequest(String filename){
    	new Thread(new Runnable() {
			@Override
			public void run() {
				try
		        {
					Debugger.log("start request");
		    		//prepare data
					byte[] myIp = AppConfig.MY_ADDRESS;
					byte[] myIpLen = Misc.Int2Bytes(myIp.length);
		    		byte[] fileName = filename.getBytes(StandardCharsets.UTF_8);
		    		byte[] fileNameLen = Misc.Int2Bytes(fileName.length);
		    		byte[] sendData = new byte[config.dataType_Size + myIpLen.length + myIp.length + fileNameLen.length + fileName.length];
		    		int offset = 0;
		    		System.arraycopy(config.dataType_bRequest, 0, sendData, offset, config.dataType_Size);
		    		offset += config.dataType_Size;
		    		System.arraycopy(myIpLen, 0, sendData, offset, myIpLen.length);
		    		offset += myIpLen.length;
		    		System.arraycopy(myIp, 0, sendData, offset, myIp.length);
		    		offset += myIp.length;
		    		System.arraycopy(fileNameLen, 0, sendData, offset, fileNameLen.length);
		    		offset += fileNameLen.length;
		    		System.arraycopy(fileName, 0, sendData, offset, fileName.length);
		    		
		        	//get host
		            InetAddress host = InetAddress.getByName(AppConfig.HOST);
		            Debugger.log(Inet4Address.getLocalHost().getHostAddress());
		            
		            //create socket and dgram
		            DatagramSocket socket = new DatagramSocket();
		            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, host, AppConfig.PORT);
		            
		            //send
	            	socket.send(packet);
		            
		            //close
		            socket.close();
		            Debugger.log("end request");
		        }
		        catch(Exception e)
		        {
		            //e.printStackTrace();
		        }
			}
		}).start();
    }
    
    public static void sendResponse(String host, String filename, long FL){
//    	new Thread(new Runnable() {
//			@Override
//			public void run() {
				try
		        {
					Debugger.log("start send-response");
		    		//prepare data
					byte[] myIp = AppConfig.MY_ADDRESS;
					byte[] myIpLen = Misc.Int2Bytes(myIp.length);
					byte[] fileName = filename.getBytes(StandardCharsets.UTF_8);
		    		byte[] fileNameLen = Misc.Int2Bytes(fileName.length);
		    		byte[] fileLen = Misc.Long2Bytes(FL);
		    		byte[] sendData = new byte[config.dataType_Size + myIpLen.length + myIp.length + fileNameLen.length + fileName.length + fileLen.length];
		    		int offset = 0;
		    		System.arraycopy(config.dataType_bResponse, 0, sendData, offset, config.dataType_Size);
		    		offset += config.dataType_Size;
		    		System.arraycopy(myIpLen, 0, sendData, offset, myIpLen.length);
		    		offset += myIpLen.length;
		    		System.arraycopy(myIp, 0, sendData, offset, myIp.length);
		    		offset += myIp.length;
		    		System.arraycopy(fileNameLen, 0, sendData, offset, fileNameLen.length);
		    		offset += fileNameLen.length;
		    		System.arraycopy(fileName, 0, sendData, offset, fileName.length);
		    		offset += fileName.length;
		    		System.arraycopy(fileLen, 0, sendData, offset, fileLen.length);
		            
		            //create socket and dgram
		            DatagramSocket socket = new DatagramSocket();
		            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(host), AppConfig.PORT);
		            
		            //send
		            socket.send(packet);
		            
		            //close
		            socket.close();
		            Debugger.log("end send-response");
		        }
		        catch(Exception e)
		        {
		            //e.printStackTrace();
		        }
//			}
//		}).start();
    }
    
    public static void sendSeedInfo(String host, String filename, long off, int len){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try
		        {
					Debugger.log("start send-info");
		    		//prepare data
					byte[] myIp = AppConfig.MY_ADDRESS;
					byte[] myIpLen = Misc.Int2Bytes(myIp.length);
		    		byte[] fileName = filename.getBytes(StandardCharsets.UTF_8);
		    		byte[] fileNameLen = Misc.Int2Bytes(fileName.length);
		    		byte[] seedOffset = Misc.Long2Bytes(off);
		    		byte[] seedLen = Misc.Int2Bytes(len);
		    		byte[] sendData = new byte[config.dataType_Size + myIpLen.length + myIp.length + fileNameLen.length + fileName.length + seedOffset.length + seedLen.length];
		    		int offset = 0;
		    		System.arraycopy(config.dataType_bSendSeedInfo, 0, sendData, offset, config.dataType_Size);
		    		offset += config.dataType_Size;
		    		System.arraycopy(myIpLen, 0, sendData, offset, myIpLen.length);
		    		offset += myIpLen.length;
		    		System.arraycopy(myIp, 0, sendData, offset, myIp.length);
		    		offset += myIp.length;
		    		System.arraycopy(fileNameLen, 0, sendData, offset, fileNameLen.length);
		    		offset += fileNameLen.length;
		    		System.arraycopy(fileName, 0, sendData, offset, fileName.length);
		    		offset += fileName.length;
		    		System.arraycopy(seedOffset, 0, sendData, offset, seedOffset.length);
		    		offset += seedOffset.length;
		    		System.arraycopy(seedLen, 0, sendData, offset, seedLen.length);
		            
		            //create socket and dgram
		            DatagramSocket socket = new DatagramSocket();
		            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(host), AppConfig.PORT);
		            
		            //send
		            socket.send(packet);
		            
		            //close
		            socket.close();
		            Debugger.log("end send-info");
		        }
		        catch(Exception e)
		        {
		            //e.printStackTrace();
		        }
			}
		}).start();
    }
    
    public static void sendData(String host, String filename, long off, int len, byte[] data, boolean isEnd){
    	//Thread t = new Thread(new Runnable() {
		//	@Override
		//	public void run() {
				try
		        {
					Debugger.log("start client:send-data");
		    		//prepare data
					byte[] myIp = AppConfig.MY_ADDRESS;
					byte[] myIpLen = Misc.Int2Bytes(myIp.length);
		    		byte[] fileName = filename.getBytes(StandardCharsets.UTF_8);
		    		byte[] fileNameLen = Misc.Int2Bytes(fileName.length);
		    		byte[] seedOffset = Misc.Long2Bytes(off);
		    		byte[] seedLen = Misc.Int2Bytes(len);
		    		byte[] sendData = new byte[config.dataType_Size + myIpLen.length + myIp.length + fileNameLen.length + fileName.length + seedOffset.length + seedLen.length + data.length];
		    		int offset = 0;
		    		if (isEnd)
		    			System.arraycopy(config.dataType_bSendData_End, 0, sendData, offset, config.dataType_Size);
		    		else
		    			System.arraycopy(config.dataType_bSendData_Continue, 0, sendData, offset, config.dataType_Size);
		    		offset += config.dataType_Size;
		    		System.arraycopy(myIpLen, 0, sendData, offset, myIpLen.length);
		    		offset += myIpLen.length;
		    		System.arraycopy(myIp, 0, sendData, offset, myIp.length);
		    		offset += myIp.length;
		    		System.arraycopy(fileNameLen, 0, sendData, offset, fileNameLen.length);
		    		offset += fileNameLen.length;
		    		System.arraycopy(fileName, 0, sendData, offset, fileName.length);
		    		offset += fileName.length;
		    		System.arraycopy(seedOffset, 0, sendData, offset, seedOffset.length);
		    		offset += seedOffset.length;
		    		System.arraycopy(seedLen, 0, sendData, offset, seedLen.length);
		    		offset += seedLen.length;
		    		System.arraycopy(data, 0, sendData, offset, data.length);
		            
		            //create socket and dgram
		            DatagramSocket socket = new DatagramSocket();
		            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(host), AppConfig.PORT);
		            
		            //send
		            socket.send(packet);
		            
		            //close
		            socket.close();
		            Debugger.log("end client:send-data");
		        }
		        catch(Exception e)
		        {
		            //e.printStackTrace();
		        }
		//	}
		//});
    	//t.start();
    }
}