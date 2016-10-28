package application;

import javafx.application.Application;
import javafx.stage.Stage;
import view.app;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		new app().getStage().show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
