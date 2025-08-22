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
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class AdminLoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    /**
     * Handles the action when the admin login button is clicked.
     */
    @FXML
    private void handleAdminLoginButtonAction(ActionEvent event) {
        String adminName = usernameField.getText();
        String password = passwordField.getText();

        if (adminName.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Admin name and password cannot be empty.");
            statusLabel.setTextFill(Color.RED);
            return;
        }

        // Call the adminLogin method from the Main class
        if (Main.adminLogin(adminName, password)) {
            statusLabel.setText("Admin Login Successful!");
            statusLabel.setTextFill(Color.GREEN);
            // Navigate to the admin dashboard after successful login
            switchToAdminDashboard(event);
        } else {
            statusLabel.setText("Invalid admin name or password.");
            statusLabel.setTextFill(Color.RED);
        }
    }

    /**
     * NEW: Handles the action for the 'Register New Admin' link.
     * It prompts for a system password before proceeding.
     */
    @FXML
    private void handleRegisterLinkAction(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("System Verification");
        dialog.setHeaderText("New Admin Registration");
        dialog.setContentText("Please enter the system password to proceed:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(password -> {
            if ("admin123".equals(password)) { // Using the system password from your console app
                // Password is correct, switch to registration scene
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/gui/AdminRegistration.fxml"));
                    Stage stage = (Stage) statusLabel.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("New Admin Registration");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    statusLabel.setText("Error: Could not load registration page.");
                }
            } else {
                // Incorrect password
                statusLabel.setText("Incorrect system password. Access denied.");
                statusLabel.setTextFill(Color.RED);
            }
        });
    }

    /**
     * Switches the scene to the Admin Dashboard.
     */
    private void switchToAdminDashboard(ActionEvent event) {
        try {
            // We will need to create AdminDashboard.fxml next
            Parent dashboardRoot = FXMLLoader.load(getClass().getResource("/gui/AdminDashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(dashboardRoot);
            stage.setScene(scene);
            stage.setTitle("Admin Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not load the admin dashboard.");
        }
    }
}
