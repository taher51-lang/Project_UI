package Project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Username and password cannot be empty.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        try {
            // Re-integrating your actual login logic from Main.java
            if (Main.login(username, password)) {
                statusLabel.setText("Login Successful!");
                statusLabel.setTextFill(Color.GREEN);
                switchToDashboard(event);
            } else {
                statusLabel.setText("Invalid username or password.");
                statusLabel.setTextFill(Color.RED);
            }
        } catch (SQLException e) {
            statusLabel.setText("Database error. Please try again.");
            statusLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }
    }

    /**
     * This is the handler for the signup link/button.
     * It loads the signup.fxml scene.
     */
    @FXML
    private void handleSignupLinkAction(ActionEvent event) {
        try {
            Parent signupRoot = FXMLLoader.load(getClass().getResource("/gui/signup.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(signupRoot);
            stage.setScene(scene);
            stage.setTitle("Sign Up");
            stage.show();
        } catch (IOException e) {
            // CORRECTED: Removed the line that caused the NullPointerException.
            // If loading the signup page fails, we'll just print the error
            // instead of trying to update a label that might not exist.
            e.printStackTrace();
        }
    }

    private void switchToDashboard(ActionEvent event) {
        try {
            // Make sure you have a Dashboard.fxml file in your resources/gui folder
            Parent dashboardRoot = FXMLLoader.load(getClass().getResource("/gui/Dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(dashboardRoot);
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            if (statusLabel != null) {
                statusLabel.setText("Error: Could not load the dashboard.");
                statusLabel.setTextFill(Color.RED);
            }
        }
    }
}
