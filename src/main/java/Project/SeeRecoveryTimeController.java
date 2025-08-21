package Project;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SeeRecoveryTimeController implements Initializable {

    @FXML
    private Label recoveryTimeLabel;

    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadRecoveryTime();
    }

    private void loadRecoveryTime() {
        String sql = "{CALL avgRecoveryTime(?)}"; // SQL to call the stored procedure

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
             CallableStatement cs = con.prepareCall(sql)) {

            // Register the OUT parameter before executing the procedure
            cs.registerOutParameter(1, java.sql.Types.INTEGER);

            cs.execute();

            // Retrieve the value from the OUT parameter
            int avgTimeInDays = cs.getInt(1);

            if (avgTimeInDays > 0) {
                recoveryTimeLabel.setText(avgTimeInDays + " Days");
            } else {
                recoveryTimeLabel.setText("N/A");
                statusLabel.setText("Not enough data to calculate an average time yet.");
            }

        } catch (SQLException e) {
            statusLabel.setText("Error: Could not calculate average recovery time.");
            recoveryTimeLabel.setText("Error");
            e.printStackTrace();
        }
    }
}
