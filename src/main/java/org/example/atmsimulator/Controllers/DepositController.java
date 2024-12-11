package org.example.atmsimulator.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.example.atmsimulator.DatabaseConn;
import org.example.atmsimulator.SceneSwitch;
import org.example.atmsimulator.UserSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DepositController {
    @FXML
    private Button Menu;

    @FXML
    private AnchorPane depositWindow;

    @FXML
    private TextField moneyField;

    @FXML
    private Button fiveButton, tenButton, twentyButton, fiftyButton, hundredButton, twoHundredButton;

    @FXML
    private Button sendButton;

    @FXML
    private Text errorText;

    private double Sum = 0;

    @FXML
    void onButton(ActionEvent event) {
        String text = ((Button) event.getSource()).getText();

        // Extract the numeric part of the text by removing non-numeric characters except '.'
        String numericPart = text.replaceAll("[^\\d.]", "");

        double value = Double.parseDouble(numericPart);
        Sum += value;
        moneyField.setText(String.format("%.2f", Sum));

    }

    @FXML
    void onSendButton(ActionEvent event) {
        String text = moneyField.getText();

        try {
            double depositAmount = Double.parseDouble(text);

            if (depositAmount <= 0) {
                errorText.setText("Invalid deposit amount.");
                errorText.setVisible(true);
                return;
            }

            try (Connection connection = DatabaseConn.getConnection()) {
                connection.setAutoCommit(false); // Enable transaction management

                UserSession userSession = UserSession.getInstance();
                String accountType = "Savings"; // Replace with dynamic selection if needed

                // Fetch account details
                String fetchAccountQuery = "SELECT a.account_id, a.balance, p.id as person_id " +
                        "FROM account a " +
                        "JOIN person p ON a.person_id = p.id " +
                        "WHERE p.name = ? AND a.account_type = ?";

                try (PreparedStatement fetchAccountStmt = connection.prepareStatement(fetchAccountQuery)) {
                    fetchAccountStmt.setString(1, userSession.getName());
                    fetchAccountStmt.setString(2, accountType);

                    ResultSet rs = fetchAccountStmt.executeQuery();
                    if (rs.next()) {
                        int accountId = rs.getInt("account_id");
                        double currentBalance = rs.getDouble("balance");
                        int personId = rs.getInt("person_id");

                        // Update account balance
                        String updateBalanceQuery = "UPDATE account SET balance = balance + ? WHERE account_id = ?";
                        try (PreparedStatement updateBalanceStmt = connection.prepareStatement(updateBalanceQuery)) {
                            updateBalanceStmt.setDouble(1, depositAmount);
                            updateBalanceStmt.setInt(2, accountId);
                            updateBalanceStmt.executeUpdate();
                        }

                        // Update person table's balance
                        String updatePersonBalanceQuery = "UPDATE person SET balance = " +
                                "(SELECT SUM(balance) FROM account WHERE person_id = ?) " +
                                "WHERE id = ?";
                        try (PreparedStatement updatePersonStmt = connection.prepareStatement(updatePersonBalanceQuery)) {
                            updatePersonStmt.setInt(1, personId);
                            updatePersonStmt.setInt(2, personId);
                            updatePersonStmt.executeUpdate();
                        }

                        // Insert transaction record
                        String insertTransactionQuery = "INSERT INTO transaction (account_id, transaction_type, amount) VALUES (?, 'Deposit', ?)";
                        try (PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {
                            insertTransactionStmt.setInt(1, accountId);
                            insertTransactionStmt.setDouble(2, depositAmount);
                            insertTransactionStmt.executeUpdate();
                        }

                        connection.commit();

                        // Update UserSession balance
                        double updatedBalance = currentBalance + depositAmount;
                        userSession.setBalance(updatedBalance);

                        // Display success message
                        errorText.setText("Deposit successful.");
                        errorText.setVisible(true);

                    } else {
                        errorText.setText("Account not found.");
                        errorText.setVisible(true);
                    }
                } catch (SQLException e) {
                    connection.rollback();
                    errorText.setText("Database error: " + e.getMessage());
                    errorText.setVisible(true);
                    e.printStackTrace();
                }
            }
        } catch (NumberFormatException e) {
            errorText.setText("Invalid amount entered.");
            errorText.setVisible(true);
        } catch (SQLException e) {
            errorText.setText("Database connection error.");
            errorText.setVisible(true);
            e.printStackTrace();
        } finally {
            moneyField.clear();
            Sum = 0;
        }
    }


    @FXML
    void onMenuButton(ActionEvent event) throws IOException {
        new SceneSwitch(depositWindow, "menu.fxml");
    }

}
