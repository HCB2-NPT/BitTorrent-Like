package srcSocket;

import java.util.ArrayList;
import java.util.Comparator;

public class DownloadingFileInfo {
	public Object readLocker = new Object();
	public Object writeLocker = new Object();
	
	public String Name = null;
	public long Offset = 0;
	public long FileLength = 0;
	public int  NSeeders = 0;
	public int 	MaxLengthForSending = 0;
	public ArrayList<SentData> ListSentData = new ArrayList<SentData>();
	
	public void init(){
		SentData start = new SentData();
		SentData end = new SentData();
		start.Offset = 0;
		start.Length = 0;
		end.Offset = FileLength;
		end.Length = 0;
		ListSentData.add(start);
		ListSentData.add(end);
	}
	
	private Comparator<SentData> comparator = new Comparator<SentData>() {
		@Override
		public int compare(SentData a, SentData b) {
			if (a.Offset < b.Offset)
				return -1;
			if (a.Offset > b.Offset)
				return 1;
			return 0;
		};
	};
	
	public void Complete(long off, int len){
		ListSentData.add(new SentData(off, len));
		ListSentData.sort(comparator);
		merge();
	}
	
	private void merge(){
		if (ListSentData.size() > 1){
			SentData a = null, b = null;
			boolean k;
			while(true){
				k = false;
				for (int i = 1; i < ListSentData.size(); i++) {
					a = ListSentData.get(i - 1);
					b = ListSentData.get(i);
					if (b.Offset <= a.Offset + a.Length && b.Offset >= a.Offset){
						k = true;
						break;
					}
				}
				if (k){
					if (a != null && b != null){
						ListSentData.remove(b);
						a.Length = (int) (b.Offset - a.Offset + b.Length);
					}
				}else{
					break;
				}
			}
		}
	}
	
	public SentData getARangeLoss(){
		if (ListSentData.size() <= 1)
			return null;
		SentData newI = new SentData();
		SentData a = ListSentData.get(0);
		SentData b = ListSentData.get(1);
		newI.Offset = a.Offset + a.Length;
		newI.Length = (int) Math.min(b.Offset - newI.Offset, MaxLengthForSending);
		return newI;
	}
}
