package view;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import config.AppConfig;
import helper.Debugger;
import helper.MessageBox;
import helper.Window;
import java.io.File;
import java.util.List;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import srcSocket.*;

public class app extends Window {
	private Listener listener = null;
	
	public app(){
		super("../view/app.fxml", "application.css");
	}
	
	@Override
	protected void onCreate() {
		Stage s = getStage();
		s.setWidth(600);
		s.setHeight(400);
		s.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (listener != null)
					listener.stop();
				Platform.exit();
				System.exit(0);
			}
		});
	}
	
    @FXML
    private JFXTextField requestField;

    @FXML
    private JFXListView<SeedFile> seedingFile;

    @FXML
    void request() {
    	String fileName = requestField.getText();
    	if (!MappingFiles.getMap().containsKey(fileName)){
    		SeedFile sf = listener.getSeedFileBy(fileName);
	    	if (sf == null){
	    		Sender.sendRequest(fileName);
	    	}
	    	else{
	    		Debugger.log("File not exists!");
	    		MessageBox.Show("File not exists!", "Notifying");
	    	}
	    }
    	else{
    		Debugger.log("File being downloaded!");
    		MessageBox.Show("File being downloaded!", "Notifying");
    	}
    }
    
    @FXML
    void load() {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Select File");
    	fileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));
    	List<File> selectedFiles = fileChooser.showOpenMultipleDialog(getStage());
    	if (selectedFiles == null)
    		return;
    	for (File selectedFile : selectedFiles) {
    		SeedFile sf = listener.getSeedFileBy(selectedFile.getName());
    		if (sf == null){
    			seedingFile.getItems().add(new SeedFile(selectedFile.getName(), selectedFile.getPath()));
    		}
    	}
    }
    
    @FXML
    void pauseplay() {
    	SeedFile sf = seedingFile.getSelectionModel().getSelectedItem();
    	if (sf != null){
	    	DownloadingFileInfo dfi = MappingFiles.getMap().get(seedingFile.getSelectionModel().getSelectedItem().getFileName().replace(AppConfig.PREFIX_EMPTY_FILE, ""));
	    	if (dfi != null)
	    		dfi.isRun = !dfi.isRun;
	    	if (dfi.isRun){
	    		dfi.Seeders.clear();
	    		Sender.sendRequest(sf.getFileName());
	    		dfi.TimeStamp = System.currentTimeMillis();
	    	}
    	}
    }

    @FXML
    void delete() {
    	SeedFile sf = seedingFile.getSelectionModel().getSelectedItem();
    	if (sf != null){
	    	MappingFiles.getMap().remove(sf.getFileName().replace(AppConfig.PREFIX_EMPTY_FILE, ""));
	    	new File(sf.getFilePath()).deleteOnExit();
	    	seedingFile.getItems().remove(sf);
    	}
    }

    @FXML
    void initialize() {
    	assert requestField != null : "fx:id=\"filepath\" was not injected: check your FXML file 'app.fxml'.";
        assert seedingFile != null : "fx:id=\"seedingFile\" was not injected: check your FXML file 'app.fxml'.";
        
        //start server to listen
        new Thread(new Runnable() {
			@Override
			public void run() {
				listener = new Listener(new IListenerEvent() {
					@Override
					public void ListenFail() {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								MessageBox.Show("Problem creating socket on port: " + AppConfig.PORT, "Shutdown...");
				            	Platform.exit();
								System.exit(0);
							}
						});
					}
					
					@Override
					public void ReceiveData(DownloadingFileInfo dfi) {
						Platform.runLater(new Runnable() {
			                @Override
			                public void run() {
			                	seedingFile.refresh();
			                }
			            });
					}
					
					@Override
					public void DownloadCompleted(DownloadingFileInfo dfi) {
						Platform.runLater(new Runnable() {
			                @Override
			                public void run() {
			                	//MessageBox.Show(dfi.Name + " is downloaded!", "Notify");
			                	seedingFile.refresh();
			                }
			            });
					}
				});
				listener.setSeedFiles(seedingFile.getItems());
				listener.listen();
			}
		}).start();
        
        //create folder "seeding files"
        new File(AppConfig.FOLDER_SEED).mkdir();
        
        //load
        loadSeedingFile();
        
        Sender.AutoRepair();
    }
    
    void loadSeedingFile(){
    	File folder = new File(AppConfig.FOLDER_SEED);
    	File[] listOfFiles = folder.listFiles();
    	String name;
    	for (File file : listOfFiles) {
    		if (file.isFile() && !file.isHidden()) {
    			name = file.getName();
	        	if (name.indexOf(AppConfig.PREFIX_EMPTY_FILE) == 0){
	        		//seedingFile.getItems().add(new SeedFile(name, file.getPath()));
	        		Sender.sendRequest(name.replace(AppConfig.PREFIX_EMPTY_FILE, ""));
	        	}
	        	else{
	        		seedingFile.getItems().add(new SeedFile(name, file.getPath()));
	        	}
	        }
		}
    }
}
