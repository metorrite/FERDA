package com.avl;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

	
	public static Stage stage;
	
	public static void main(String[] args) {
		launch(args);
	 }
	
    @Override
    public void start(Stage stage) throws Exception {
    	Launcher.stage = stage;
    	stage.setTitle("Forness's Excellent Reliable Diving Assistant");
    	openPage(Launcher.class.getResource("/ferda.fxml"));
    }
		
	public static void openPage(URL url) {
		try {
			Parent root = FXMLLoader.load(url);
			Scene scene = new Scene(root);
	        stage.setScene(scene);
	        stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
