package org.example.atmsimulator.Controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.example.atmsimulator.SceneSwitch;
import org.example.atmsimulator.UserSession;
import org.example.atmsimulator.DatabaseConn;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PinChangeController {

    @FXML
    private TextField PINfield;

    @FXML
    private AnchorPane PINPane;

    @FXML
    private Text errorText;

    @FXML
    private Button exitButton, saveButton;

    @FXML
    private Button oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton, sevenButton, eightButton, nineButton, zeroButton;

    // StringBuilder to track current input
    StringBuilder currentText = new StringBuilder();

    @FXML
    void onNumber(ActionEvent event) {
        String value = ((Button)event.getSource()).getText();
        currentText.append(value);
        PINfield.setText(currentText.toString());
    }

    @FXML
    void onSaveButton(ActionEvent event) {
        String PIN = PINfield.getText();

        try {
            int newPIN = Integer.parseInt(PIN);

            if (PIN.length() != 4) {
                errorText.setText("PIN must be 4 digits.");
                errorText.setVisible(true);
                return;
            }

            try (Connection connection = DatabaseConn.getConnection()) {
                String updatePINQuery = "UPDATE person SET pin = ? WHERE name = ?";

                try (PreparedStatement updatePINStmt = connection.prepareStatement(updatePINQuery)) {
                    UserSession userSession = UserSession.getInstance();
                    updatePINStmt.setInt(1, newPIN);
                    updatePINStmt.setString(2, userSession.getName());
                    int rowsAffected = updatePINStmt.executeUpdate();

                    if (rowsAffected > 0) {
                        errorText.setText("PIN updated successfully.");
                        errorText.setVisible(true);
                    } else {
                        errorText.setText("Failed to update PIN. User not found.");
                        errorText.setVisible(true);
                    }
                }
            } catch (SQLException e) {
                errorText.setText("Database error: " + e.getMessage());
                errorText.setVisible(true);
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            errorText.setText("Invalid PIN format. Only numbers are allowed.");
            errorText.setVisible(true);
        } finally {
            currentText.setLength(0);
            PINfield.clear();
        }
    }

    @FXML
    void onExitButton(ActionEvent event) throws IOException {
        new SceneSwitch(PINPane, "menu.fxml");
    }

}
