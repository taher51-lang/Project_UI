package Project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @FXML
    private BorderPane mainPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + Main.loggedInUserName + "!");
        } else {
            System.err.println("DashboardController: welcomeLabel is null. Check FXML file for fx:id=\"welcomeLabel\".");
        }
    }

    private void loadView(String fxmlFile) {
        try {
            Pane view = FXMLLoader.load(getClass().getResource(fxmlFile));
            mainPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Main Actions ---
    @FXML
    private void handleReportLostItem(ActionEvent event) {
        loadView("/gui/ReportLostItem.fxml");
    }

    @FXML
    private void handleReportFoundItem(ActionEvent event) {
        loadView("/gui/ReportFoundItem.fxml");
    }

    // --- NEW: Handlers for Information & Stats ---

    @FXML
    private void handleSeeRewards(ActionEvent event) {
        System.out.println("Loading 'See My Rewards' view...");
         loadView("/gui/SeeRewards.fxml");
    }

    @FXML
    private void handleSeeAdminResponse(ActionEvent event) {
        System.out.println("Loading 'See Admin Responses' view...");
        loadView("/gui/AdminResponses.fxml"); //
    }

    @FXML
    private void handleSeeRecoveryRate(ActionEvent event) {
        System.out.println("Loading 'Recovery Rate' view...");
        loadView("/gui/RecoveryRate.fxml"); //
    }

    @FXML
    private void handleSeeRecoveryTime(ActionEvent event) {
        System.out.println("Loading 'Average Recovery Time' view...");
        loadView("/gui/RecoveryTime.fxml"); //
    }

    @FXML
    private void handleSeeHotspotAreas(ActionEvent event) {
        System.out.println("Loading 'Loss Hotspots' view...");
        loadView("/gui/HotspotAreas.fxml"); //
    }

    // --- Logout ---
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/gui/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loginRoot);
            stage.setScene(scene);
            stage.setTitle("Lost and Found Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
