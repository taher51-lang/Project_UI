package Project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.InputStream;
import java.sql.*;

public class VerificationDetailsController {

    @FXML private Label lostUserIdLabel;
    @FXML private Label lostItemIdLabel;
    @FXML private Label lostUserFlagLabel;
    @FXML private TextArea lostAttributesArea;
    @FXML private ImageView lostImageView;

    @FXML private Label foundUserIdLabel;
    @FXML private Label foundItemIdLabel;
    @FXML private TextArea foundAttributesArea;
    @FXML private ImageView foundImageView;

    @FXML private Label statusLabel;
    @FXML private Button approveButton;
    @FXML private Button denyButton;

    private AdminDashboardController.VerificationRequest request;


    public void loadRequestDetails(AdminDashboardController.VerificationRequest request) {
        this.request = request;

        // Populating labels with basic info
        lostUserIdLabel.setText("User ID: " + request.getLostUserId());
        lostItemIdLabel.setText("Item ID: " + request.getLostItemId());
        foundUserIdLabel.setText("User ID: " + request.getFoundUserId());
        foundItemIdLabel.setText("Item ID: " + request.getFoundItemId());

        // Fetching detailed info from the database
        fetchItemDetails(request.getLostItemId(), lostAttributesArea, lostImageView);
        fetchItemDetails(request.getFoundItemId(), foundAttributesArea, foundImageView);
        fetchUserFlag(request.getLostUserId(), lostUserFlagLabel);
    }

    private void fetchItemDetails(int itemId, TextArea attributesArea, ImageView imageView) {
        String sql = "SELECT attribute, image FROM lostandfound WHERE id = ?";
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, itemId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                attributesArea.setText(rs.getString("attribute"));
                Blob imageBlob = rs.getBlob("image");
                if (imageBlob != null) {
                    InputStream is = imageBlob.getBinaryStream();
                    imageView.setImage(new Image(is));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            attributesArea.setText("Error loading details.");
        }
    }

    private void fetchUserFlag(int userId, Label flagLabel) {
        String sql = "SELECT flag FROM user WHERE userId = ?";
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                // CORRECTED: Read the flag as a String to handle text values
                String flag = rs.getString("flag");
                flagLabel.setText("User Flag: " + flag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            flagLabel.setText("User Flag: Error");
        }
    }

    @FXML
    private void handleApproveAction(ActionEvent event) {
        if (Main.approveVerification(request.getLostUserId(), request.getFoundUserId(), request.getFoundItemId())) {
            statusLabel.setText("Request Approved Successfully!");
            approveButton.setDisable(true);
            denyButton.setDisable(true);
        } else {
            statusLabel.setText("Error: Could not approve request.");
        }
    }

    @FXML
    private void handleDenyAction(ActionEvent event) {
        if (Main.denyVerification(request.getLostUserId(), request.getFoundUserId())) {
            statusLabel.setText("Request Denied Successfully!");
            approveButton.setDisable(true);
            denyButton.setDisable(true);
        } else {
            statusLabel.setText("Error: Could not deny request.");
        }
    }
}
