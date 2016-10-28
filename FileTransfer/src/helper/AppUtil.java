package helper;

import java.io.IOException;

import application.AppSession;
import config.AppConfig;
//import org.apache.log4j.Logger;
//import application.AppSession;
//import config.AppConfig;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AppUtil {
	//private static final Logger logger = Logger.getLogger(AppUtil.class);
	
	public static FXMLLoader callFXMLLoader(String resourcePath){
		FXMLLoader loader = null;
		if (resourcePath != null){
			try {
				loader = new FXMLLoader(AppSession._resourceProvider.getResource(resourcePath));
			}catch(Exception e){
				//logger.error("Can not create FXML. Path: " + resourcePath);
			}
		}
		return loader;
	}
	
	public static <T> T callPane(String panePath){
		T root = null;
		if (panePath != null){
			try {
				root = (T)callFXMLLoader(panePath).load();
			} catch (IOException e) {
				//logger.error("Can not create pane. Path: " + panePath);
			}
		}
		return root;
	}
	
	public static <T> T callPane(FXMLLoader loader){
		T root = null;
		if (loader != null){
			try {
				root = (T)loader.load();
			} catch (IOException e) {
				//logger.error("Can not create pane. Path: " + loader.getLocation().getPath());
			}
		}
		return root;
	}
	
	public static Stage callForm(String scenePath, String cssPath){
		Stage s = null;
		if (scenePath != null){
			Parent pane = callPane(scenePath);
			if (pane != null){
				Scene scene = new Scene(pane);
				if (cssPath != null)
					scene.getStylesheets().add(AppSession._resourceProvider.getResource(cssPath).toExternalForm());
				if (scene != null){
					s = new Stage();
					s.getIcons().add(new Image(AppSession._resourceProvider.getResource(AppConfig.APP_ICON).toExternalForm()));
					s.setScene(scene);
					s.setTitle(AppConfig.APP_NAME);
				}
			}
		}
		return s;
	}
	
	public static Stage callForm(FXMLLoader loader, String cssPath){
		Stage s = null;
		if (loader != null){
			Parent pane = callPane(loader);
			if (pane != null){
				Scene scene = new Scene(pane);
				if (cssPath != null)
					scene.getStylesheets().add(AppSession._resourceProvider.getResource(cssPath).toExternalForm());
				if (scene != null){
					s = new Stage();
					s.getIcons().add(new Image(AppSession._resourceProvider.getResource(AppConfig.APP_ICON).toExternalForm()));
					s.setScene(scene);
					s.setTitle(AppConfig.APP_NAME);
				}
			}
		}
		return s;
	}
}
