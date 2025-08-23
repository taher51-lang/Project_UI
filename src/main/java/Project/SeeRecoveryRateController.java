package Project;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class SeeRecoveryRateController implements Initializable {

    @FXML
    private Label recoveryRateLabel;

    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadRecoveryRate();
    }

    private void loadRecoveryRate() {
        String totalItemsSql = "SELECT COUNT(*) FROM lostandfound";
        String verifiedItemsSql = "SELECT COUNT(*) FROM lostandfound WHERE status = 'Verified'";
        float recoveryRate = 0.0f;

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
             Statement st1 = con.createStatement();
             Statement st2 = con.createStatement()) {

            int totalItems = 0;
            int verifiedItems = 0;

            // Executing first query to get total items
            try (ResultSet rs1 = st1.executeQuery(totalItemsSql)) {
                if (rs1.next()) {
                    totalItems = rs1.getInt(1);
                }
            }

            // Executing second query to get verified items
            try (ResultSet rs2 = st2.executeQuery(verifiedItemsSql)) {
                if (rs2.next()) {
                    verifiedItems = rs2.getInt(1);
                }
            }

            // Calculate the recovery rate, avoiding division by zero
            if (totalItems > 0) {
                recoveryRate = ((float) verifiedItems / totalItems) * 100.0f;
                recoveryRateLabel.setText(String.format("%.2f%%", recoveryRate));
            } else {
                recoveryRateLabel.setText("N/A");
                statusLabel.setText("No items have been reported in the system yet.");
            }

        } catch (SQLException e) {
            statusLabel.setText("Error: Could not calculate the recovery rate.");
            recoveryRateLabel.setText("Error");
            e.printStackTrace();
        }
    }
}

