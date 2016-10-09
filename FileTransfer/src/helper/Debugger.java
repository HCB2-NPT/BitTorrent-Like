package helper;

public class Debugger {
	public final static boolean IsEnable = true;
	
	public static void log(String msg){
		if (IsEnable)
			System.out.println(msg);
	}
}
