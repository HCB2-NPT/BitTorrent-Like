package helper;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

/*
 * Any class is extended by Window, 
 * fxml file must be remove handler
 */
public abstract class Window {
	private FXMLLoader _formLoader = null;
    private Stage _currentStage = null;
    
    public FXMLLoader getLoader(){
    	return _formLoader;
    }
    
    public Stage getStage(){
    	return _currentStage;
    }
	
	protected Window(String file, String css){
		_formLoader = AppUtil.callFXMLLoader(file);
		_formLoader.setController(this);
		if (_formLoader != null){
			_currentStage = AppUtil.callForm(_formLoader, css);
			if (_currentStage != null){
				onCreate();
		    }else{
		    	System.out.println("stage null?");
		    }
		}else{
			System.out.println("loader null?");
		}
	}
	
	protected void onCreate(){
		
	}
}
