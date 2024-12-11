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
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransferController {

    @FXML
    private TextField cardField;

    @FXML
    private Text cardText, errorText;

    @FXML
    private Button checkButton;

    @FXML
    private Button exitButton;

    @FXML
    private TextField numberField;

    @FXML
    private Text numberText;

    @FXML
    private AnchorPane transferPane;

    @FXML
    private Text transferText;

    @FXML
    void onCheckButton(ActionEvent event) {
        String card = cardField.getText();
        String number = numberField.getText();

        try (Connection connection = DatabaseConn.getConnection()) {
            String query = "SELECT id, name, balance, telephone_number FROM person WHERE card_number = ? OR telephone_number = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, card);
                preparedStatement.setString(2, number);

                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    String name = rs.getString("name");
                    String telephoneNumber = rs.getString("telephone_number");
                    int id = rs.getInt("id");

                    // Store client data in UserSession for transfer
                    UserSession userSession = UserSession.getInstance();
                    userSession.setClientName(name);
                    userSession.setClientNumber(telephoneNumber);
                    userSession.setClientId(id);

                    // Switch to the `transfer2.fxml` scene
                    new SceneSwitch(transferPane, "transfer2.fxml");
                } else {
                    cardText.setText("Invalid card number or phone number.");
                    numberText.setText("Invalid card number or phone number.");
                    cardText.setVisible(true);
                    numberText.setVisible(true);
                }
            }
        } catch (SQLException | IOException e) {
            transferText.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onExitButton(ActionEvent event) throws IOException {
        new SceneSwitch(transferPane, "menu.fxml");
    }
}
