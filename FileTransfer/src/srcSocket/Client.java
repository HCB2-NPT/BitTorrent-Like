package srcSocket;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import config.Constants;

public final class Client{
    public static void sendRequest(String filename){
    	Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try
		        {
					System.out.println("start request");
		    		//prepare data
					byte[] myIp = Constants.MY_ADDRESS;
					byte[] myIpLen = Misc.Int2Bytes(myIp.length);
		    		byte[] fileName = filename.getBytes(StandardCharsets.UTF_8);
		    		byte[] fileNameLen = Misc.Int2Bytes(fileName.length);
		    		byte[] sendData = new byte[skGlobals.dataType_Size + myIpLen.length + myIp.length + fileNameLen.length + fileName.length];
		    		int offset = 0;
		    		System.arraycopy(skGlobals.dataType_bRequest, 0, sendData, offset, skGlobals.dataType_Size);
		    		offset += skGlobals.dataType_Size;
		    		System.arraycopy(myIpLen, 0, sendData, offset, myIpLen.length);
		    		offset += myIpLen.length;
		    		System.arraycopy(myIp, 0, sendData, offset, myIp.length);
		    		offset += myIp.length;
		    		System.arraycopy(fileNameLen, 0, sendData, offset, fileNameLen.length);
		    		offset += fileNameLen.length;
		    		System.arraycopy(fileName, 0, sendData, offset, fileName.length);
		    		
		        	//get host
		            InetAddress host = InetAddress.getByName(Constants.HOST);
		            System.out.println(Inet4Address.getLocalHost().getHostAddress());
		            
		            //create socket and dgram
		            DatagramSocket socket = new DatagramSocket();
		            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, host, Constants.PORT);
		            
		            //send
		            socket.send(packet);
		            
		            //close
		            socket.close();
		            System.out.println("end request");
		        }
		        catch(Exception e)
		        {
		            //e.printStackTrace();
		        }
			}
		});
    	t.start();
    }
    
    public static void sendResponse(String host, String filename){
    	Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try
		        {
					System.out.println("start send-response");
		    		//prepare data
					byte[] myIp = Constants.MY_ADDRESS;
					byte[] myIpLen = Misc.Int2Bytes(myIp.length);
					byte[] fileName = filename.getBytes(StandardCharsets.UTF_8);
		    		byte[] fileNameLen = Misc.Int2Bytes(fileName.length);
		    		File f = new File(Constants.FOLDER_SEED + filename);
		    		byte[] fileLen = Misc.Long2Bytes(f.length());
		    		byte[] sendData = new byte[skGlobals.dataType_Size + myIpLen.length + myIp.length + fileNameLen.length + fileName.length + fileLen.length];
		    		int offset = 0;
		    		System.arraycopy(skGlobals.dataType_bResponse, 0, sendData, offset, skGlobals.dataType_Size);
		    		offset += skGlobals.dataType_Size;
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
		            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(host), Constants.PORT);
		            
		            //send
		            socket.send(packet);
		            
		            //close
		            socket.close();
		            System.out.println("end send-response");
		        }
		        catch(Exception e)
		        {
		            //e.printStackTrace();
		        }
			}
		});
    	t.start();
    }
    
    public static void sendSeedInfo(String host, String filename, long offset, int len){
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try
		        {
					System.out.println("start send-info");
		    		//prepare data
					byte[] myIp = Constants.MY_ADDRESS;
					byte[] myIpLen = Misc.Int2Bytes(myIp.length);
		    		byte[] fileName = filename.getBytes(StandardCharsets.UTF_8);
		    		byte[] fileNameLen = Misc.Int2Bytes(fileName.length);
		    		byte[] seedOffset = Misc.Long2Bytes(offset);
		    		byte[] seedLen = Misc.Int2Bytes(len);
		    		byte[] sendData = new byte[skGlobals.dataType_Size + myIpLen.length + myIp.length + fileNameLen.length + fileName.length + seedOffset.length + seedLen.length];
		    		int offset = 0;
		    		System.arraycopy(skGlobals.dataType_bSendSeedInfo, 0, sendData, offset, skGlobals.dataType_Size);
		    		offset += skGlobals.dataType_Size;
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
		            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(host), Constants.PORT);
		            
		            //send
		            socket.send(packet);
		            
		            //close
		            socket.close();
		            System.out.println("end send-info");
		        }
		        catch(Exception e)
		        {
		            //e.printStackTrace();
		        }
			}
		});
    	t.start();
    }
    
    public static void sendData(String host, String filename, long offset, int len, byte[] data, boolean isEnd){
    	Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try
		        {
					System.out.println("start send-data");
		    		//prepare data
					byte[] myIp = Constants.MY_ADDRESS;
					byte[] myIpLen = Misc.Int2Bytes(myIp.length);
		    		byte[] fileName = filename.getBytes(StandardCharsets.UTF_8);
		    		byte[] fileNameLen = Misc.Int2Bytes(fileName.length);
		    		byte[] seedOffset = Misc.Long2Bytes(offset);
		    		byte[] seedLen = Misc.Int2Bytes(len);
		    		byte[] sendData = new byte[skGlobals.dataType_Size + myIpLen.length + myIp.length + fileNameLen.length + fileName.length + seedOffset.length + seedLen.length + data.length];
		    		int offset = 0;
		    		if (isEnd)
		    			System.arraycopy(skGlobals.dataType_bSendData_End, 0, sendData, offset, skGlobals.dataType_Size);
		    		else
		    			System.arraycopy(skGlobals.dataType_bSendData_Continue, 0, sendData, offset, skGlobals.dataType_Size);
		    		offset += skGlobals.dataType_Size;
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
		            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(host), Constants.PORT);
		            
		            //send
		            socket.send(packet);
		            
		            //close
		            socket.close();
		            System.out.println("end send-data");
		        }
		        catch(Exception e)
		        {
		            //e.printStackTrace();
		        }
			}
		});
    	t.start();
    }
}