package org.example.atmsimulator.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.example.atmsimulator.DatabaseConn;
import org.example.atmsimulator.SceneSwitch;
import org.example.atmsimulator.Transaction;
import org.example.atmsimulator.UserSession;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class HistoryController implements Initializable {

    @FXML
    private Text PINText, balanceText, cardNumberText, dateText, genderText, idText, nameText, surnameText, telephoneText;

    @FXML
    private TableView<Transaction> TransactionTable;

    @FXML
    private TableColumn<Transaction, Integer> idColumn;

    @FXML
    private TableColumn<Transaction, String> typeColumn;

    @FXML
    private TableColumn<Transaction, Double> amountColumn;

    @FXML
    private TableColumn<Transaction, String> dateColumn;

    @FXML
    private AnchorPane historyPane;

    @FXML
    void onExitButton(ActionEvent event) throws IOException {
        new SceneSwitch(historyPane, "menu.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserSession userSession = UserSession.getInstance();
        idText.setText(String.valueOf(userSession.getId()));
        nameText.setText(userSession.getName());
        surnameText.setText(userSession.getSurname());
        dateText.setText(userSession.getDateOfBirth().toString());
        genderText.setText(userSession.getGender());
        cardNumberText.setText(userSession.getCardNumber());
        PINText.setText(userSession.getPINCode());
        telephoneText.setText(userSession.getTelephone());
        balanceText.setText(String.format("%.2f", userSession.getBalance()));

        idColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("transactionType"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));

        loadTransactionData(userSession.getId());
    }

    private void loadTransactionData(int accountId) {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();

        String query = "SELECT transaction_id, transaction_type, amount, transaction_date FROM transaction WHERE account_id = ?";
        try (Connection connection = DatabaseConn.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, accountId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("transaction_id");
                String type = resultSet.getString("transaction_type");
                double amount = resultSet.getDouble("amount");
                String date = resultSet.getString("transaction_date");

                transactions.add(new Transaction(id, type, amount, date));
            }

            TransactionTable.setItems(transactions);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
