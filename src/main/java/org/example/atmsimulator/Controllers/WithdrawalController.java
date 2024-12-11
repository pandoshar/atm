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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import java.io.IOException;

public class WithdrawalController {

    private double money;

    @FXML
    private Text ErrorText, balanceText;

    @FXML
    private TextField amountField;

    @FXML
    private Button menuButton;

    @FXML
    private Button fiftyButton, onehunButton, twohunButton, fivehunButton;

    @FXML
    private Button withdrawButton;

    @FXML
    private AnchorPane withdrawalWindow;

    @FXML
    void initialize() {
        updateUserInfo();
    }

    private void updateUserInfo() {
        UserSession userSession = UserSession.getInstance();
        balanceText.setText(String.format("%.2f", userSession.getBalance()));
    }

    @FXML
    void onWithdrawButton(ActionEvent event) {
        String amountText = amountField.getText();

        try {
            double amount = Double.parseDouble(amountText);

            try (Connection connection = DatabaseConn.getConnection() ) {
                connection.setAutoCommit(false); // Enable transaction management

                // Fetch the account details
                String fetchAccountQuery = "SELECT a.account_id, a.balance, a.account_type, p.id as person_id " +
                        "FROM account a " +
                        "JOIN person p ON a.person_id = p.id " +
                        "WHERE p.name = ? AND a.account_type = ?"; // Fetch based on account type

                try (PreparedStatement fetchAccountStmt = connection.prepareStatement(fetchAccountQuery)) {
                    UserSession userSession = UserSession.getInstance();
                    String accountType = "Savings"; // Replace this with user input for account type selection
                    fetchAccountStmt.setString(1, userSession.getName());
                    fetchAccountStmt.setString(2, accountType);

                    ResultSet rs = fetchAccountStmt.executeQuery();
                    if (rs.next()) {
                        int accountId = rs.getInt("account_id");
                        double currentBalance = rs.getDouble("balance");
                        int personId = rs.getInt("person_id");

                        if (amount <= currentBalance) {
                            // Deduct the amount
                            String updateBalanceQuery = "UPDATE account SET balance = balance - ? WHERE account_id = ?";
                            try (PreparedStatement updateBalanceStmt = connection.prepareStatement(updateBalanceQuery)) {
                                updateBalanceStmt.setDouble(1, amount);
                                updateBalanceStmt.setInt(2, accountId);
                                updateBalanceStmt.executeUpdate();
                            }

                            // Update the person table's balance
                            String updatePersonBalanceQuery = "UPDATE person SET balance = " +
                                    "(SELECT SUM(balance) FROM account WHERE person_id = ?) " +
                                    "WHERE id = ?";
                            try (PreparedStatement updatePersonStmt = connection.prepareStatement(updatePersonBalanceQuery)) {
                                updatePersonStmt.setInt(1, personId);
                                updatePersonStmt.setInt(2, personId);
                                updatePersonStmt.executeUpdate();
                            }

                            // Insert into transaction table
                            String insertTransactionQuery = "INSERT INTO transaction (account_id, transaction_type, amount) VALUES (?, 'Withdrawal', ?)";
                            try (PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {
                                insertTransactionStmt.setInt(1, accountId);
                                insertTransactionStmt.setDouble(2, amount);
                                insertTransactionStmt.executeUpdate();
                            }

                            connection.commit();

                            userSession.setBalance(currentBalance - amount);

                            // Update UI feedback
                            balanceText.setText(String.format("%.2f", currentBalance - amount));
                            ErrorText.setText("Withdrawal successful.");
                            ErrorText.setVisible(true);
                            Sum = 0;
                        } else {
                            ErrorText.setText("Insufficient funds in the selected account.");
                            ErrorText.setVisible(true);
                            Sum = 0;
                        }
                    } else {
                        ErrorText.setText("Account not found or invalid account type.");
                        ErrorText.setVisible(true);
                    }
                } catch (SQLException e) {
                    connection.rollback();
                    ErrorText.setText("Database error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (NumberFormatException e) {
            ErrorText.setText("Invalid amount entered.");
        } catch (SQLException e) {
            ErrorText.setText("Database connection error.");
            e.printStackTrace();
        } finally {
            amountField.clear();
        }
    }


    private double Sum = 0;

    @FXML
    void onMoneyButton(ActionEvent event) {
        String text = ((Button) event.getSource()).getText();

        String numericPart = text.replaceAll("[^\\d.]", "");

        double value = Double.parseDouble(numericPart);

        Sum += value;
        amountField.setText(String.format("%.2f", Sum));
    }

    @FXML
    void onMenuButton(ActionEvent event) throws IOException {
        new SceneSwitch(withdrawalWindow, "menu.fxml");
    }

}
