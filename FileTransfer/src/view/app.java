package view;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;

import application.Main;
import config.Constants;
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
    private Button btnSend;
    
    @FXML
    private Button btnSeed;

    @FXML
    void sender_Enter(ActionEvent event) {
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
		    		//System.out.println("File exists!");
		    		MessageBox.Show("File exists!", "Notifying");
		    	}
		    }
	    	else{
	    		//System.out.println("File being downloaded!");
	    		MessageBox.Show("File being downloaded!", "Notifying");
	    	}
    	}
    	else{
    		//System.out.println("Server is not opened!");
    		MessageBox.Show("Server is not opened!", "Notifying");
    	}
    }
    
    @FXML
    void seed_Enter(ActionEvent event) {
    	seedingFile.getItems().clear();
    	try(Stream<Path> paths = Files.walk(Paths.get(Constants.FOLDER_SEED))) {
		    paths.forEach(filePath -> {
		        if (Files.isRegularFile(filePath)) {
		            seedingFile.getItems().add(filePath.getFileName().toString());
		        }
		    });
		} catch (IOException e) {
			//e.printStackTrace();
		}
    }

    @FXML
    void initialize() {
    	assert requestField != null : "fx:id=\"filepath\" was not injected: check your FXML file 'app.fxml'.";
    	assert btnSend != null : "fx:id=\"btnSend\" was not injected: check your FXML file 'app.fxml'.";
    	assert btnSeed != null : "fx:id=\"btnSeed\" was not injected: check your FXML file 'app.fxml'.";
        assert seedingFile != null : "fx:id=\"seedingFile\" was not injected: check your FXML file 'app.fxml'.";
        
        Thread s = new Thread(new Runnable() {
			@Override
			public void run() {
				Main.server = new Server();
				if (!Main.server.listen())
					Main.server = null;
				if (Main.server == null)
				{
					Platform.exit();
					System.exit(0);
				}
			}
		});
        s.start();
        
        //load
        seed_Enter(null);
    }
}
