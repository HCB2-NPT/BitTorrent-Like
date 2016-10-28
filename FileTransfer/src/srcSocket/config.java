package srcSocket;

public class config {
	public final static int 	dataType_Size 				= 1;
	public final static String 	dataType_Request 			= "0";
	public final static String 	dataType_Response	 		= "a";
	public final static String 	dataType_SendSeedInfo		= "1";
	public final static String 	dataType_SendData_Continue	= "b";
	public final static String 	dataType_SendData_End		= "B";
	public final static byte[] 	dataType_bRequest 			= dataType_Request.getBytes();
	public final static byte[] 	dataType_bResponse	 		= dataType_Response.getBytes();
	public final static byte[] 	dataType_bSendSeedInfo		= dataType_SendSeedInfo.getBytes();
	public final static byte[] 	dataType_bSendData_Continue	= dataType_SendData_Continue.getBytes();
	public final static byte[] 	dataType_bSendData_End		= dataType_SendData_End.getBytes();
}
