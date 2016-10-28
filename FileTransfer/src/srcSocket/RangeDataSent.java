package srcSocket;

public class RangeDataSent {
	public long Offset = 0;
	public int Length = 0;
	
	public RangeDataSent() { super(); }
	
	public RangeDataSent(long off, int len) {
		Offset = off;
		Length = len;
	}
}
