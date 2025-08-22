package Project;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RoleSelectionController {

    /**
     * Handles the action when the "I am a User" button is clicked.
     * Navigates to the standard user login screen.
     */
    public void handleUserButtonAction(ActionEvent event) {
        switchScene(event, "/gui/login.fxml", "User Login");
    }

    /**
     * Handles the action when the "I am an Admin" button is clicked.
     * Navigates to the admin login screen.
     */
    public void handleAdminButtonAction(ActionEvent event) {
        // We will need to create AdminLogin.fxml and its controller next
        switchScene(event, "/gui/AdminLogin.fxml", "Admin Login");
    }

    /**
     * A helper method to switch scenes cleanly.
     * @param event The event that triggered the action.
     * @param fxmlFile The path to the new FXML file to load.
     * @param title The title for the new window.
     */
    private void switchScene(ActionEvent event, String fxmlFile, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
