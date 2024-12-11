    package org.example.atmsimulator.Controllers;

    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.scene.control.TextField;
    import javafx.scene.layout.AnchorPane;
    import javafx.scene.text.Text;
    import org.example.atmsimulator.DatabaseConn;
    import org.example.atmsimulator.SceneSwitch;
    import org.example.atmsimulator.UserSession;

    import java.io.IOException;
    import java.sql.*;

    public class StartController {

        @FXML
        private TextField cardField, numberField, pinField;

        @FXML
        private Text errorText;

        @FXML
        private AnchorPane startPane;

        @FXML
        void onCheckButton(ActionEvent event) {
            String cardNumber = cardField.getText().trim();
            String telephoneNumber = numberField.getText().trim();
            String pinCode = pinField.getText().trim();

            if (cardNumber.isEmpty() && telephoneNumber.isEmpty()) {
                errorText.setText("Please enter card number or telephone number.");
                errorText.setVisible(true);
                return;
            }

            try (Connection connection = DatabaseConn.getConnection()) {
                String query = "SELECT id, name, surname, date_of_birth, gender, card_number, balance, pin FROM person WHERE card_number = ? OR telephone_number = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, cardNumber);
                    preparedStatement.setString(2, telephoneNumber);

                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String name = resultSet.getString("name");
                        String surname = resultSet.getString("surname");
                        Date dateOfBirth = resultSet.getDate("date_of_birth");
                        String gender = resultSet.getString("gender");
                        String CardNumber = resultSet.getString("card_number");
                        double balance = resultSet.getDouble("balance");
                        String storedPin = resultSet.getString("pin");

                        if (storedPin.equals(pinCode)) {
                            // Save data to UserSession
                            UserSession userSession = UserSession.getInstance();
                            userSession.setId(id);
                            userSession.setName(name);
                            userSession.setSurname(surname);
                            userSession.setDateOfBirth(dateOfBirth);
                            userSession.setGender(gender);
                            userSession.setCardNumber(CardNumber);
                            userSession.setPINCode(storedPin);
                            userSession.setTelephone(telephoneNumber);
                            userSession.setBalance(balance);

                            // Navigate to menu.fxml
                            new SceneSwitch(startPane, "menu.fxml");

                        } else {
                            errorText.setText("Incorrect PIN.");
                            errorText.setVisible(true);
                        }
                    } else {
                        errorText.setText("No record found.");
                        errorText.setVisible(true);
                    }
                }
            } catch (SQLException | IOException e) {
                errorText.setText("Error: " + e.getMessage());
                errorText.setVisible(true);
            }
        }
    }
