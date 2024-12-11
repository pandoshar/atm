package org.example.atmsimulator;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

public class SceneSwitch {

    public SceneSwitch(AnchorPane currentAnchorPane, String fxml) throws IOException {
        URL resourceUrl = ATMApplication.class.getResource(fxml);
        if (resourceUrl == null) {
            throw new IOException("FXML file not found: " + fxml);
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        AnchorPane nextAnchorPane = loader.load();
        currentAnchorPane.getChildren().clear();
        currentAnchorPane.getChildren().setAll(nextAnchorPane);

    }
}
