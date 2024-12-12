package org.example.atmsimulator.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.example.atmsimulator.DatabaseConn;
import org.example.atmsimulator.SceneSwitch;
import org.example.atmsimulator.UserSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Transfer2Controller {

    @FXML
    private TextField clientNameField, clientNumberField, moneyField;

    @FXML
    private Text errorText;

    @FXML
    private Button sendButton, exitButton;

    @FXML
    private AnchorPane transfer2Pane;

    @FXML
    void onExitButton(ActionEvent event) throws IOException {
        new SceneSwitch(transfer2Pane, "menu.fxml");
    }

    @FXML
    public void initialize() {
        UserSession userSession = UserSession.getInstance();
        clientNameField.setText(userSession.getClientName());
        clientNumberField.setText(userSession.getClientNumber());
    }

    @FXML
    void onSendButton(ActionEvent event) {
        String moneyString = moneyField.getText();
        double money = Double.parseDouble(moneyString);

        try (Connection connection = DatabaseConn.getConnection()) {
            connection.setAutoCommit(false);

            UserSession userSession = UserSession.getInstance();
            int senderId = userSession.getId();
            int receiverId = userSession.getClientId();

            String checkBalanceQuery = "SELECT balance FROM person WHERE id = ?";
            try (PreparedStatement checkBalanceStmt = connection.prepareStatement(checkBalanceQuery)) {
                checkBalanceStmt.setInt(1, senderId);
                ResultSet rs = checkBalanceStmt.executeQuery();
                if (rs.next()) {
                    double currentBalance = rs.getDouble("balance");
                    if (money > currentBalance) {
                        errorText.setText("Insufficient balance.");
                        errorText.setVisible(true);
                        connection.rollback();
                        return;
                    }
                }
            }

            String updateBalanceQuery = "UPDATE account SET balance = balance - ? WHERE account_id = ?";
            try (PreparedStatement updateBalanceStmt = connection.prepareStatement(updateBalanceQuery)) {
                updateBalanceStmt.setDouble(1, money);
                updateBalanceStmt.setInt(2, senderId);
                updateBalanceStmt.executeUpdate();
            }

            String updatePersonBalanceQuery = "UPDATE person SET balance = " +
                    "(SELECT SUM(balance) FROM account WHERE person_id = ?) " +
                    "WHERE id = ?";
            try (PreparedStatement updatePersonStmt = connection.prepareStatement(updatePersonBalanceQuery)) {
                updatePersonStmt.setInt(1, senderId);
                updatePersonStmt.setInt(2, senderId);
                updatePersonStmt.executeUpdate();
            }

            String updateReceiverBalanceQuery = "UPDATE account SET balance = balance + ? WHERE account_id = ?";
            try (PreparedStatement updateReceiverStmt = connection.prepareStatement(updateReceiverBalanceQuery)) {
                updateReceiverStmt.setDouble(1, money);
                updateReceiverStmt.setInt(2, receiverId);
                updateReceiverStmt.executeUpdate();
            }

            String updateReceiverPersonBalanceQuery = "UPDATE person SET balance = " +
                    "(SELECT SUM(balance) FROM account WHERE person_id = ?) " +
                    "WHERE id = ?";
            try (PreparedStatement updateReceiverPersonStmt = connection.prepareStatement(updateReceiverPersonBalanceQuery)) {
                updateReceiverPersonStmt.setInt(1, receiverId);
                updateReceiverPersonStmt.setInt(2, receiverId);
                updateReceiverPersonStmt.executeUpdate();
            }

            String insertTransactionQuery = "INSERT INTO transaction (account_id, transaction_type, amount) VALUES (?, 'Transfer', ?)";
            try (PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {
                insertTransactionStmt.setInt(1, senderId);
                insertTransactionStmt.setDouble(2, money);
                insertTransactionStmt.executeUpdate();
            }

            connection.commit();

            errorText.setText("Transfer successful.");
            errorText.setVisible(true);
        } catch (SQLException e) {
            errorText.setText("Database error: " + e.getMessage());
            errorText.setVisible(true);
            e.printStackTrace();
        } finally {
            moneyField.clear();
            clientNameField.clear();
            clientNumberField.clear();
        }
    }
}
