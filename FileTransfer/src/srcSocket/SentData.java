package srcSocket;

public class SentData {
	public long Offset = 0;
	public int Length = 0;
	
	public SentData() { super(); }
	
	public SentData(long off, int len) {
		Offset = off;
		Length = len;
	}
}
