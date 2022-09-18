package ru.pashabezborod.JavaFxClient;

import ru.pashabezborod.JavaFxClient.controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    public static void main(String[] args) {
        MainApp.launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            final FXMLLoader loader = new FXMLLoader(MainApp.class.getClassLoader().getResource("MainScene.fxml"));
            stage.setScene(new Scene(loader.load()));
            LoginController.makeLoginDialog(stage, loader.getController());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
