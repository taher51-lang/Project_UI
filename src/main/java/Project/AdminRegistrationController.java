package Project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.sql.SQLException;

public class AdminRegistrationController {

    @FXML private TextField usernameField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField contactField;
    @FXML private Label statusLabel;

    @FXML
    private void handleRegisterButtonAction(ActionEvent event) {
        try {
            String username = usernameField.getText();
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            long contact = Long.parseLong(contactField.getText());

            if (username.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please fill in all fields.");
                statusLabel.setTextFill(Color.RED);
                return;
            }

            // Calling the new registration method in the Main class
            if (Main.adminRegistration(username, name, email, password, contact)) {
                statusLabel.setText("Admin registered successfully!");
                statusLabel.setTextFill(Color.GREEN);
            } else {
                statusLabel.setText("Registration failed. Please try again.");
                statusLabel.setTextFill(Color.RED);
            }

        } catch (NumberFormatException e) {
            statusLabel.setText("Contact number must be a valid number.");
            statusLabel.setTextFill(Color.RED);
        }
    }
}
