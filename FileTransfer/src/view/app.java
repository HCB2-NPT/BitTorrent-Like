package view;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;

import application.Main;
import config.Constants;
import helper.Debugger;
import helper.MessageBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import srcSocket.*;

public class app {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private JFXTextField requestField;

    @FXML
    private JFXListView<String> seedingFile;

    @FXML
    private Button btnRequest;
    
    @FXML
    private Button btnLoad;

    @FXML
    void request(ActionEvent event) {
    	if (Main.server != null && Main.server.isActive()){
	    	String fileName = requestField.getText();
	    	if (!MappingFiles.getMap().containsKey(fileName)){
		    	File f = new File(Constants.FOLDER_SEED + fileName);
		    	if (!f.exists()){
			    	Thread c = new Thread(new Runnable() {
						@Override
						public void run() {
							Client.sendRequest(f.getName());
						}
					});
			        c.start();
		    	}
		    	else{
		    		Debugger.log("File exists!");
		    		MessageBox.Show("File exists!", "Notifying");
		    	}
		    }
	    	else{
	    		Debugger.log("File being downloaded!");
	    		MessageBox.Show("File being downloaded!", "Notifying");
	    	}
    	}
    	else{
    		Debugger.log("Server is not opened!");
    		MessageBox.Show("Server is not opened!", "Notifying");
    	}
    }
    
    @FXML
    void load(ActionEvent event) {
    	seedingFile.getItems().clear();
    	try(Stream<Path> paths = Files.walk(Paths.get(Constants.FOLDER_SEED))) {
		    paths.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		        	String name = filePath.getFileName().toString();
		        	if (name.indexOf(Constants.PREFIX_EMPTY_FILE) == 0)
		        		seedingFile.getItems().add(String.format("%1$s%2$48s", name, "0%"));
		        	else
		        		seedingFile.getItems().add(String.format("%1$s%2$48s", name, "Seeding..."));
		        }
		    });
		} catch (IOException e) {
			//e.printStackTrace();
		}
    }

    @FXML
    void initialize() {
    	assert requestField != null : "fx:id=\"filepath\" was not injected: check your FXML file 'app.fxml'.";
    	assert btnRequest != null : "fx:id=\"btnSend\" was not injected: check your FXML file 'app.fxml'.";
    	assert btnLoad != null : "fx:id=\"btnSeed\" was not injected: check your FXML file 'app.fxml'.";
        assert seedingFile != null : "fx:id=\"seedingFile\" was not injected: check your FXML file 'app.fxml'.";
        
        //start server to listen
        Thread s = new Thread(new Runnable() {
			@Override
			public void run() {
				Main.server = new Server(new IServerEvent() {
					@Override
					public void ReceiveResponse() {
						Platform.runLater(new Runnable() {
			                @Override
			                public void run() {
			                	load(null);
			                }
			            });
					}
					
					@Override
					public void ReceiveData(DownloadingFileInfo dfi) {
						Platform.runLater(new Runnable() {
			                @Override
			                public void run() {
			                	int index = -1;
			                	for (String row : seedingFile.getItems()) {
									if ((index = row.indexOf(Constants.PREFIX_EMPTY_FILE + dfi.Name)) > -1){
										break;
									}
								}
			                	seedingFile.getItems().set(index, String.valueOf((dfi.LengthDownloaded() / dfi.FileLength) * 100d));
			                }
			            });
					}
					
					@Override
					public void DownloadCompleted(DownloadingFileInfo dfi) {
						Platform.runLater(new Runnable() {
			                @Override
			                public void run() {
			                	MessageBox.Show(dfi.Name + " is downloaded!", "Notify");
			                	load(null);
			                }
			            });
					}
				});
				if (!Main.server.listen())
					Main.server = null;
			}
		});
        s.start();
        
        //load
        load(null);
        
        //create folder "seeding files"
        new File(Constants.FOLDER_SEED).mkdir();
    }
}
