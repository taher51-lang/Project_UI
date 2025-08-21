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

    // This annotation links this variable to the Label with fx:id="welcomeLabel" in the FXML
    @FXML
    private Label welcomeLabel;

    @FXML
    private BorderPane mainPane;

    /**
     * This method runs automatically after the FXML is loaded.
     * Because of the @FXML annotation above, 'welcomeLabel' should now be correctly linked and not null.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // This line was causing the crash because welcomeLabel was null.
        // With the FXML and this controller correctly linked, it should now work.
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, User " + Main.id + "!");
        } else {
            System.err.println("DashboardController: welcomeLabel is null. Check FXML file for fx:id=\"welcomeLabel\".");
        }
    }

    /**
     * A helper method to load different views into the center of the dashboard.
     */
    private void loadView(String fxmlFile) {
        try {
            Pane view = FXMLLoader.load(getClass().getResource(fxmlFile));
            mainPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReportLostItem(ActionEvent event) {
        loadView("/gui/ReportLostItem.fxml");
    }

    @FXML
    private void handleReportFoundItem(ActionEvent event) {
        loadView("/gui/ReportFoundItem.fxml");
    }

    @FXML
    private void handleSearchItems(ActionEvent event) {
        // loadView("/gui/SearchItems.fxml");
    }

    @FXML
    private void handleViewMyReports(ActionEvent event) {
        // loadView("/gui/ViewMyReports.fxml");
    }

    @FXML
    private void handleItemRecovery(ActionEvent event) {
        // loadView("/gui/ItemRecovery.fxml");
    }

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
