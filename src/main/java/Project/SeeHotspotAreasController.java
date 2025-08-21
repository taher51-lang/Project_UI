package Project;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SeeHotspotAreasController implements Initializable {

    @FXML
    private TableView<Hotspot> hotspotTable;
    @FXML
    private TableColumn<Hotspot, String> locationColumn;
    @FXML
    private TableColumn<Hotspot, Integer> countColumn;
    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up the columns to accept data from the Hotspot class
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
        loadHotspotData();
    }

    private void loadHotspotData() {
        ObservableList<Hotspot> hotspots = FXCollections.observableArrayList();
        String sql = "SELECT plocation, COUNT(id) AS item_count FROM lostandfound GROUP BY plocation ORDER BY item_count DESC";

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String location = rs.getString("plocation");
                int count = rs.getInt("item_count");
                hotspots.add(new Hotspot(location, count));
            }

            if (hotspots.isEmpty()) {
                statusLabel.setText("No hotspot data is available yet.");
            }

        } catch (SQLException e) {
            statusLabel.setText("Error: Could not load hotspot data from the database.");
            e.printStackTrace();
        }

        hotspotTable.setItems(hotspots);
    }

    /**
     * A simple data class to hold the information for each row in the table.
     * The property names (location, count) must match the PropertyValueFactory strings.
     */
    public static class Hotspot {
        private final SimpleStringProperty location;
        private final SimpleIntegerProperty count;

        public Hotspot(String location, int count) {
            this.location = new SimpleStringProperty(location);
            this.count = new SimpleIntegerProperty(count);
        }

        public String getLocation() {
            return location.get();
        }

        public int getCount() {
            return count.get();
        }
    }
}
