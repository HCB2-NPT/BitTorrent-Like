package srcSocket;

public interface IListenerEvent {
	public void ListenFail();
	public void ReceiveData(DownloadingFileInfo dfi);
	public void DownloadCompleted(DownloadingFileInfo dfi);
}
