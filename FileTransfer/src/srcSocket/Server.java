package srcSocket;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import application.Main;
import config.Constants;

public class Server{
	private DatagramSocket socket;
    private DatagramPacket packet;
    private boolean active = true;
    
    public void stop(){
    	active = false;
    }
    
    public boolean listen(){
    	try
        {
            socket = new DatagramSocket(Constants.PORT);
        }
        catch( Exception ex )
        {
            System.out.println("Problem creating socket on port: " + Constants.PORT);
            return false;
        }
    	
    	byte[] buffer = new byte[Constants.BUFFER_SIZE];
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
	            	case skGlobals.dataType_RequestBack:
	            		ReceiveResponse(data, Ip);
	            		break;
	            	case skGlobals.dataType_RequestSeed:
	            		ReceiveSeedInfo(data, Ip);
	            		break;
	            	case skGlobals.dataType_SendData:
	            		ReceiveData(data, Ip);
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
        return true;
    }
    
    void ReceiveRequest(byte[] data, String from) throws UnsupportedEncodingException{
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
					new Client().sendRequestBack(from, name);
					System.out.println("send response success!");
				}
	    	}
	    	System.out.println("end receive-request");
    	}
    	catch (Exception e){
    		e.printStackTrace();
    	}
    }
    
	void ReceiveResponse(byte[] data, String from){
		try{
			System.out.println("start receive-response.... undone!");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	void ReceiveSeedInfo(byte[] data, String from){
		
	}
	
	void ReceiveData(byte[] data, String from){
		
	}
}