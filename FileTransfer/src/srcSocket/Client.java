package srcSocket;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.*;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.charset.spi.CharsetProvider;
import java.util.Arrays;

import config.Constants;

public class Client{
    public void sendRequest(String filename){
    	Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try
		        {
					System.out.println("start request");
		    		//prepare data
		    		byte[] fileName = filename.getBytes(StandardCharsets.UTF_8);
		    		byte[] fileNameLen = Misc.Int2Bytes(fileName.length);
		    		byte[] sendData = new byte[skGlobals.dataType_bRequest.length + fileNameLen.length + fileName.length];
		    		int offset = 0;
		    		System.arraycopy(skGlobals.dataType_bRequest, 0, sendData, offset, skGlobals.dataType_bRequest.length);
		    		offset += skGlobals.dataType_bRequest.length;
		    		System.arraycopy(fileNameLen, 0, sendData, offset, fileNameLen.length);
		    		offset += fileNameLen.length;
		    		System.arraycopy(fileName, 0, sendData, offset, fileName.length);
		    		
		        	//get host
		            InetAddress host = InetAddress.getByName(Constants.HOST);
		            
		            //create socket and dgram
		            DatagramSocket socket = new DatagramSocket(Constants.PORT);
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
    
    public void sendRequestBack(InetAddress host, String filename){
    	Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try
		        {
					System.out.println("start send-response");
		    		//prepare data
					byte[] fileName = filename.getBytes(StandardCharsets.UTF_8);
		    		byte[] fileNameLen = Misc.Int2Bytes(fileName.length);
		    		byte[] sendData = new byte[skGlobals.dataType_bRequestBack.length];
		    		int offset = 0;
		    		System.arraycopy(skGlobals.dataType_bRequestBack, 0, sendData, offset, skGlobals.dataType_bRequestBack.length);
		    		offset += skGlobals.dataType_bRequest.length;
		    		System.arraycopy(fileNameLen, 0, sendData, offset, fileNameLen.length);
		    		offset += fileNameLen.length;
		    		System.arraycopy(fileName, 0, sendData, offset, fileName.length);
		            
		            //create socket and dgram
		            DatagramSocket socket = new DatagramSocket(Constants.PORT);
		            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, host, Constants.PORT);
		            
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
    
    public void sendRequestSeed(InetAddress host, String filename, long offset, long len){
    	Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try
		        {
					System.out.println("start send-info");
		    		//prepare data
		    		byte[] fileName = filename.getBytes(StandardCharsets.UTF_8);
		    		byte[] fileNameLen = Misc.Int2Bytes(fileName.length);
		    		byte[] seedOffset = Misc.Long2Bytes(offset);
		    		byte[] seedLen = Misc.Long2Bytes(len);
		    		byte[] sendData = new byte[skGlobals.dataType_bRequest.length + fileNameLen.length + fileName.length];
		    		int offset = 0;
		    		System.arraycopy(skGlobals.dataType_bRequest, 0, sendData, offset, skGlobals.dataType_bRequest.length);
		    		offset += skGlobals.dataType_bRequest.length;
		    		System.arraycopy(fileNameLen, 0, sendData, offset, fileNameLen.length);
		    		offset += fileNameLen.length;
		    		System.arraycopy(fileName, 0, sendData, offset, fileName.length);
		    		offset += fileName.length;
		    		System.arraycopy(seedOffset, 0, sendData, offset, seedOffset.length);
		    		offset += seedOffset.length;
		    		System.arraycopy(seedLen, 0, sendData, offset, seedLen.length);
		            
		            //create socket and dgram
		            DatagramSocket socket = new DatagramSocket(Constants.PORT);
		            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, host, Constants.PORT);
		            
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
    
    public void sendSeedData(){
    	
    }
}