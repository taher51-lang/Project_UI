package Project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SignupController {

    @FXML private TextField usernameField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField contactField;
    @FXML private Label statusLabel;

    @FXML
    private void handleSignupButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String contact = contactField.getText();

        if (username.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty() || contact.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "")) {

            // --- Tree-based Username Validation Logic ---
            String sql1 = "SELECT user_name FROM user";
            Statement st = con.createStatement();
            ResultSet rs1 = st.executeQuery(sql1);

            Tree existingUsers = new Tree();
            while (rs1.next()) {
                existingUsers.insert(rs1.getString(1));
            }

            // The insert method returns true if the username is a duplicate
            if (existingUsers.insert(username)) {
                statusLabel.setText("Error: The username is already taken! Kindly choose a different one.");
                statusLabel.setTextFill(Color.RED);
                return; // Stop the registration process
            }
            // --- End of Tree Logic ---

            // If username is unique, proceed with registration by calling the static method in User class
            String resultMessage = User.registration(name, username, password, contact, email);

            // Display the result message from the User class
            statusLabel.setText(resultMessage);

            if (resultMessage.startsWith("Error:")) {
                statusLabel.setTextFill(Color.RED);
            } else {
                statusLabel.setTextFill(Color.GREEN);
            }

        } catch (ClassNotFoundException | SQLException e) {
            statusLabel.setText("A database error occurred during registration.");
            statusLabel.setTextFill(Color.RED);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            statusLabel.setText("Error: Contact number must be a valid number.");
            statusLabel.setTextFill(Color.RED);
        }
    }
}
