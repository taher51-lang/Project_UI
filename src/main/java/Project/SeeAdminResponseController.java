package Project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SeeAdminResponseController implements Initializable {

    @FXML
    private ListView<String> responseListView;

    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadAdminResponses();
    }

    private void loadAdminResponses() {
        ObservableList<String> responses = FXCollections.observableArrayList();
        // SQL query to get non-null admin responses for the current user
        String sql = "SELECT adminresponse FROM lostandfound WHERE uid = ? AND adminresponse IS NOT NULL";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, Main.id); // Use the logged-in user's ID

            try (ResultSet rs = pst.executeQuery()) {
                boolean hasResponses = false;
                while (rs.next()) {
                    String response = rs.getString("adminresponse");
                    if (response != null && !response.trim().isEmpty()) {
                        responses.add(response);
                        hasResponses = true;
                    }
                }

                if (!hasResponses) {
                    statusLabel.setText("You have no new responses from the admin yet.");
                }
            }

        } catch (SQLException e) {
            statusLabel.setText("Error: Could not load admin responses from the database.");
            e.printStackTrace();
        }

        responseListView.setItems(responses);
    }
}
