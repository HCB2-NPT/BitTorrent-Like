package srcSocket;

public interface IServerEvent {
	public void ListenFail();
	public void ReceiveResponse();
	public void ReceiveData(DownloadingFileInfo dfi);
	public void DownloadCompleted(DownloadingFileInfo dfi);
}
