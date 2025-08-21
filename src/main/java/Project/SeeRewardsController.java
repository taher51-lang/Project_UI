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

public class SeeRewardsController implements Initializable {

    @FXML
    private ListView<String> rewardsListView;

    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadRewards();
    }

    private void loadRewards() {
        ObservableList<String> rewards = FXCollections.observableArrayList();
        String sql = "SELECT rewards FROM user WHERE userId = ?";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, Main.id); // Get the logged-in user's ID

            try (ResultSet rs = pst.executeQuery()) {
                boolean hasRewards = false;
                while (rs.next()) {
                    String rewardAmount = rs.getString("rewards");
                    // Assuming 'rewards' is a numeric value, format it nicely.
                    // If it can be other text, you can just use rs.getString(1);
                    if (rewardAmount != null && !rewardAmount.isEmpty()) {
                        rewards.add("You have earned a reward of Rs. " + rewardAmount);
                        hasRewards = true;
                    }
                }

                if (!hasRewards) {
                    statusLabel.setText("You have no rewards yet. Keep up the good work!");
                }
            }

        } catch (SQLException e) {
            statusLabel.setText("Error: Could not load rewards from the database.");
            e.printStackTrace();
        }

        rewardsListView.setItems(rewards);
    }
}
