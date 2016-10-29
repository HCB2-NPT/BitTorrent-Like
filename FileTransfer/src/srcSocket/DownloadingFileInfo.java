package srcSocket;

import java.util.ArrayList;
import java.util.Comparator;

public class DownloadingFileInfo {
	public Object readLocker = new Object();
	public Object writeLocker = new Object();
	
	public String Name = null;
	public long Offset = 0;
	public long FileLength = 0;
	public int 	MaxLengthForSending = 0;
	public ArrayList<String> Seeders = new ArrayList<String>();
	public ArrayList<RangeDataSent> ListSentData = new ArrayList<RangeDataSent>();
	
	public long TimeStamp = System.currentTimeMillis();
	
	public boolean isRun = true;
	
	public DownloadingFileInfo(){
		init();
	}
	
	private void init(){
		RangeDataSent start = new RangeDataSent();
		start.Offset = 0;
		start.Length = 0;
		ListSentData.add(start);
	}
	
	private Comparator<RangeDataSent> comparator = new Comparator<RangeDataSent>() {
		@Override
		public int compare(RangeDataSent a, RangeDataSent b) {
			if (a.Offset < b.Offset)
				return -1;
			if (a.Offset > b.Offset)
				return 1;
			return 0;
		};
	};
	
	public void Complete(long off, int len){
		ListSentData.add(new RangeDataSent(off, len));
		ListSentData.sort(comparator);
		merge();
	}
	
	private void merge(){
		RangeDataSent a = null, b = null;
		boolean k;
		while(ListSentData.size() > 1){
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
					if (b.Offset + b.Length > a.Offset + a.Length)
						a.Length = (int) (b.Offset - a.Offset + b.Length);
				}
			}else{
				break;
			}
		}
	}
	
	public RangeDataSent getARangeLoss(){
		if (ListSentData.size() >= 1){
			RangeDataSent a = ListSentData.get(0);
			if (a.Offset == 0 && a.Length == FileLength)
				return null;
			RangeDataSent newI = new RangeDataSent();
			RangeDataSent b = ListSentData.get(1);
			newI.Offset = a.Offset + a.Length;
			newI.Length = (int) Math.min(b.Offset - newI.Offset, MaxLengthForSending);
			return newI;
		}
		return null;
	}
	
	public long LengthDownloaded(){
		long sum = 0;
		for (RangeDataSent sentData : ListSentData) {
			sum += sentData.Length;
		}
		return sum;
	}
	
	public boolean IsReceived(long off, int len){
		for (RangeDataSent sentData : ListSentData) {
			if (off >= sentData.Offset && off < sentData.Offset + sentData.Length
					&& off + len <= sentData.Offset + sentData.Length)
				return true;
		}
		return false;
	}
}
