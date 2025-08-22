package Project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML private TableView<VerificationRequest> verificationTable;
    @FXML private TableColumn<VerificationRequest, Integer> lostUserIdCol;
    @FXML private TableColumn<VerificationRequest, Integer> lostItemIdCol;
    @FXML private TableColumn<VerificationRequest, Integer> foundUserIdCol;
    @FXML private TableColumn<VerificationRequest, Integer> foundItemIdCol;
    @FXML private TableColumn<VerificationRequest, Void> actionCol;
    @FXML private Label statusLabel;

    private ObservableList<VerificationRequest> requestList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lostUserIdCol.setCellValueFactory(new PropertyValueFactory<>("lostUserId"));
        lostItemIdCol.setCellValueFactory(new PropertyValueFactory<>("lostItemId"));
        foundUserIdCol.setCellValueFactory(new PropertyValueFactory<>("foundUserId"));
        foundItemIdCol.setCellValueFactory(new PropertyValueFactory<>("foundItemId"));
        setupActionColumn();
        loadPendingRequests();
    }

    private void loadPendingRequests() {
        requestList.clear();
        String sql = "SELECT * FROM adminverification WHERE status = 'Pending'";
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                requestList.add(new VerificationRequest(
                        rs.getInt("lostUserID"),
                        rs.getInt("lostId"),
                        rs.getInt("foundUserID"),
                        rs.getInt("fid")
                ));
            }
            verificationTable.setItems(requestList);
            if (requestList.isEmpty()) {
                statusLabel.setText("No pending verification requests.");
            }
        } catch (SQLException e) {
            statusLabel.setText("Error loading verification requests.");
            e.printStackTrace();
        }
    }

    private void setupActionColumn() {
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button reviewButton = new Button("Review");

            {
                reviewButton.setOnAction(event -> {
                    VerificationRequest request = getTableView().getItems().get(getIndex());
                    openDetailsWindow(request);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(reviewButton);
                }
            }
        });
    }

    private void openDetailsWindow(VerificationRequest request) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/VerificationDetails.fxml"));
            Parent root = loader.load();

            // Get the controller of the new window
            VerificationDetailsController controller = loader.getController();
            // Pass the data to the new controller
            controller.loadRequestDetails(request);

            Stage stage = new Stage();
            stage.setTitle("Review Verification Request");
            stage.setScene(new Scene(root));
            // Add a listener to refresh the table when the details window is closed
            stage.setOnHidden(e -> loadPendingRequests());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: Could not open details window.");
        }
    }

    // Helper class to represent a row in the TableView
    public static class VerificationRequest {
        private final int lostUserId;
        private final int lostItemId;
        private final int foundUserId;
        private final int foundItemId;

        public VerificationRequest(int lostUserId, int lostItemId, int foundUserId, int foundItemId) {
            this.lostUserId = lostUserId;
            this.lostItemId = lostItemId;
            this.foundUserId = foundUserId;
            this.foundItemId = foundItemId;
        }

        public int getLostUserId() { return lostUserId; }
        public int getLostItemId() { return lostItemId; }
        public int getFoundUserId() { return foundUserId; }
        public int getFoundItemId() { return foundItemId; }
    }
}
