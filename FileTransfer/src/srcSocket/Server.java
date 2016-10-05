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
    	String type;
    	
    	active = true;
        while (active)
        {
            try
            {
            	packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
                
                receivedData = packet.getData();
                data = new byte[receivedData.length - 1];
                System.arraycopy(receivedData, 1, data, 0, receivedData.length - 1);
                
                System.arraycopy(receivedData, 0, dataType, 0, 1);
                type = new String(dataType);
                
                switch(type){
	                case skGlobals.dataType_Request:
	                	ReceiveRequest(data, socket);
	            		break;
	            	case skGlobals.dataType_RequestBack:
	            		ReceiveResponse(data, socket);
	            		break;
	            	case skGlobals.dataType_RequestSeed:
	            		ReceiveSeedInfo(data, socket);
	            		break;
	            	case skGlobals.dataType_SendData:
	            		ReceiveData(data, socket);
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
    
    void ReceiveRequest(byte[] data, DatagramSocket from) throws UnsupportedEncodingException{
    	System.out.println("1");
    	if (from.getInetAddress().getHostAddress() != from.getLocalAddress().getHostAddress()){
	    	byte[] fileNameLen = new byte[4];
			System.arraycopy(data, 0, fileNameLen, 0, 4);
			int len = ByteBuffer.wrap(fileNameLen).getInt();
			
			byte[] fileName = new byte[len];
			System.arraycopy(data, 4, fileName, 0, len);
			String name = new String(fileName, "UTF-8");
			
			File f = new File(Constants.FOLDER_SEED + name);
			if (f.exists() && !f.isDirectory()){
				new Client().sendRequestBack(from.getInetAddress(), name);
			}
    	}
    }
    
	void ReceiveResponse(byte[] data, DatagramSocket from){
		System.out.println("A");
	}
	
	void ReceiveSeedInfo(byte[] data, DatagramSocket from){
		
	}
	
	void ReceiveData(byte[] data, DatagramSocket from){
		
	}
}