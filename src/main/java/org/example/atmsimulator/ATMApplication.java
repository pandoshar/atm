package org.example.atmsimulator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ATMApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ATMApplication.class.getResource("start.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 450);
        stage.setTitle("ATM Simulator");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}

