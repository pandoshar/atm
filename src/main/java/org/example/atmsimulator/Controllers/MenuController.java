package org.example.atmsimulator.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
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

public class MenuController{

    @FXML
    private AnchorPane menuPane;

    @FXML
    private TextField balanceField;

    @FXML
    private Text nameText, savingsText, checkingText;

    @FXML
    private Button withdrawalButton1, withdrawalButton2, withdrawalButton3, depositButton, transferButton, historyButton, PINButton, exitButton;

    @FXML
    void initialize() {
        updateUserInfo();
    }

    private void updateUserInfo() {
        UserSession userSession = UserSession.getInstance();
        nameText.setText("Hello, " + userSession.getName() + "!");

        try (Connection connection = DatabaseConn.getConnection()) {
            // Fetch total balance from person table
            String personBalanceQuery = "SELECT balance FROM person WHERE id = ?";
            try (PreparedStatement personBalanceStmt = connection.prepareStatement(personBalanceQuery)) {
                personBalanceStmt.setInt(1, userSession.getId());
                ResultSet personBalanceResult = personBalanceStmt.executeQuery();

                if (personBalanceResult.next()) {
                    double totalBalance = personBalanceResult.getDouble("balance");
                    balanceField.setText(String.format("%.2f", totalBalance));
                } else {
                    balanceField.setText("0.00");
                }
            }

            // Fetch savings and checking balances from account table
            String accountQuery = "SELECT account_type, balance FROM account WHERE person_id = ?";
            try (PreparedStatement accountStmt = connection.prepareStatement(accountQuery)) {
                accountStmt.setInt(1, userSession.getId());
                ResultSet accountResult = accountStmt.executeQuery();

                double savingsBalance = 0.0;
                double checkingBalance = 0.0;

                while (accountResult.next()) {
                    String accountType = accountResult.getString("account_type");
                    double balance = accountResult.getDouble("balance");

                    if ("Savings".equalsIgnoreCase(accountType)) {
                        savingsBalance = balance;
                    } else if ("Checking".equalsIgnoreCase(accountType)) {
                        checkingBalance = balance;
                    }
                }

                savingsText.setText(String.format("In Savings: %.2f", savingsBalance));
                checkingText.setText(String.format("In Checking: %.2f", checkingBalance));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            balanceField.setText("Error");
            savingsText.setText("In Savings: Error");
            checkingText.setText("In Checking: Error");
        }
    }


    @FXML
    void onWithdrawalButton(ActionEvent event) throws IOException {
        new SceneSwitch(menuPane, "withdrawal.fxml");

        updateUserInfo();
    }

    @FXML
    void onDepositButton(ActionEvent event) throws IOException {
        new SceneSwitch(menuPane, "deposit.fxml");
    }

    @FXML
    void onTransferButton(ActionEvent event) throws IOException {
        new SceneSwitch(menuPane, "transfer.fxml");
    }

    @FXML
    void onPINButton(ActionEvent event) throws IOException {
        new SceneSwitch(menuPane, "pin.fxml");
    }

    @FXML
    void onHistoryButton(ActionEvent event) throws IOException {
        new SceneSwitch(menuPane, "history.fxml");
    }

    @FXML
    void onExitButton(ActionEvent event) {
        System.exit(0);
    }
}