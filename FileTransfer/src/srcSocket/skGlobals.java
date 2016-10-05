package srcSocket;

public class skGlobals {
	public final static int 	dataType_Size 			= 1;
	public final static String 	dataType_Request 		= "0";
	public final static String 	dataType_RequestBack 	= "a";
	public final static String 	dataType_RequestSeed	= "1";
	public final static String 	dataType_SendData		= "b"; 
	public final static byte[] 	dataType_bRequest 		= dataType_Request.getBytes();
	public final static byte[] 	dataType_bRequestBack 	= dataType_RequestBack.getBytes();
	public final static byte[] 	dataType_bRequestSeed	= dataType_RequestSeed.getBytes();
	public final static byte[] 	dataType_bSendData		= dataType_SendData.getBytes();
}
