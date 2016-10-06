package srcSocket;

import java.util.ArrayList;
import java.util.Comparator;

public class DownloadingFileInfo {
	public String Name = null;
	public long Offset = 0;
	public long FileLength = 0;
	public int  NSeeders = 0;
	public int 	MaxLengthForSending = 0;
	public ArrayList<SentData> ListSentData = new ArrayList<SentData>();
	
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
		_complete(off, len);
		ListSentData.sort(comparator);
	}
	
	private void _complete(long off, int len){
		for (SentData each : ListSentData) {
			if (each.Offset + each.Length == off){
				each.Length += len;
				_complete(each);
				return;
			}
			if (off + len == each.Offset){
				each.Offset = off;
				each.Length += len;
				_complete(each);
				return;
			}
		}
		SentData newI = new SentData();
		newI.Offset = off;
		newI.Length = len;
		ListSentData.add(newI);
	}
	
	private void _complete(SentData item){
		for (SentData each : ListSentData) {
			if (each.Offset + each.Length == item.Offset){
				each.Length += item.Length;
				ListSentData.remove(item);
				_complete(each);
				return;
			}
			if (item.Offset + item.Length == each.Offset){
				item.Length += each.Length;
				ListSentData.remove(each);
				_complete(item);
				return;
			}
		}
	}
	
	public SentData getARangeLoss(){
		if (ListSentData.size() > 1){
			SentData newI = new SentData();
			SentData a = ListSentData.get(0);
			SentData b = ListSentData.get(1);
			newI.Offset = a.Offset + a.Length;
			newI.Length = b.Offset - newI.Offset;
			return newI;
		}
		else{
			return null;
		}
	}
}
