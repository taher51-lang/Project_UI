
package Project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
        import java.util.ResourceBundle;

public class ReportFoundItemController implements Initializable {

    // --- FXML Variables ---
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField itemNameField;
    @FXML private TextField areaField;
    @FXML private DatePicker datePicker;
    @FXML private TextField colorField;
    @FXML private TextArea attributesArea;
    @FXML private VBox dynamicFieldsContainer;
    @FXML private Label statusLabel;
    @FXML private Label selectedImageLabel;

    // --- Backend Variable ---
    private File selectedImageFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryComboBox.getItems().addAll(
                "Electronics & Gadgets", "Bags & Carriers", "Clothing & Accessories",
                "Identity & Documents", "Academic Supplies", "Hobby & Entertainment Gear",
                "Children’s Belongings", "Eyewear & Vision Aids", "Keys & Access Devices"
        );

        categoryComboBox.valueProperty().addListener((obs, oldVal, newVal) -> loadCategorySpecificFields(newVal));
    }

    private void loadCategorySpecificFields(String category) {
        dynamicFieldsContainer.getChildren().clear();
        if (category == null) return;

        String fxmlFile = "";
        switch (category) {
            case "Electronics & Gadgets": fxmlFile = "/gui/fields_electronics.fxml"; break;
            case "Bags & Carriers": fxmlFile = "/gui/fields_bags.fxml"; break;
            case "Clothing & Accessories": fxmlFile = "/gui/fields_accessories.fxml"; break;
            case "Identity & Documents": fxmlFile = "/gui/fields_documents.fxml"; break;
            case "Academic Supplies": fxmlFile = "/gui/fields_academicsupplies.fxml"; break;
            case "Hobby & Entertainment Gear": fxmlFile = "/gui/fields_entertainmentgears.fxml"; break;
            case "Children’s Belongings": fxmlFile = "/gui/fields_childstuff.fxml"; break;
            case "Eyewear & Vision Aids": fxmlFile = "/gui/fields_eyeandvision.fxml"; break;
            case "Keys & Access Devices": fxmlFile = "/gui/fields_keys.fxml"; break;
        }

        if (!fxmlFile.isEmpty()) {
            try {
                VBox fields = FXMLLoader.load(getClass().getResource(fxmlFile));
                dynamicFieldsContainer.getChildren().add(fields);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Opens a file chooser for the user to select an image.
     */
    @FXML
    private void handleChooseImageAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Item Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            selectedImageFile = file;
            selectedImageLabel.setText(selectedImageFile.getName());
        }
    }

    @FXML
    private void handleSubmitButtonAction(ActionEvent event) {
        String category = categoryComboBox.getValue();
        if (category == null || itemNameField.getText().isEmpty() || areaField.getText().isEmpty() || datePicker.getValue() == null) {
            statusLabel.setText("Please fill in all required common fields.");
            return;
        }

        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
            con.setAutoCommit(false);

            String mainSql = "INSERT INTO Lostandfound(uid, name, area, date, color, attribute, status, plocation, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            int generatedId = -1;

            try (PreparedStatement psMain = con.prepareStatement(mainSql, Statement.RETURN_GENERATED_KEYS)) {
                String[] locationParts = areaField.getText().split(",");
                String primaryLocation = locationParts.length > 0 ? locationParts[locationParts.length - 1].trim() : areaField.getText();

                psMain.setInt(1, Main.id);
                psMain.setString(2, itemNameField.getText());
                psMain.setString(3, areaField.getText());
                psMain.setDate(4, Date.valueOf(datePicker.getValue()));
                psMain.setString(5, colorField.getText());
                psMain.setString(6, attributesArea.getText());
                psMain.setString(7, "found"); // Status is "found"
                psMain.setString(8, primaryLocation);

                if (selectedImageFile != null) {
                    FileInputStream fis = new FileInputStream(selectedImageFile);
                    psMain.setBlob(9, fis, selectedImageFile.length());
                } else {
                    psMain.setNull(9, Types.BLOB);
                }

                psMain.executeUpdate();

                ResultSet rs = psMain.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve generated ID.");
                }
            }

            // --- Insert into the category-specific table ---
            // This entire switch statement is identical to the one in ReportLostItemController
            switch (category) {
                case "Electronics & Gadgets":
                    TextField brandField = (TextField) dynamicFieldsContainer.lookup("#brandField");
                    TextField modelField = (TextField) dynamicFieldsContainer.lookup("#modelField");
                    if (brandField.getText().isEmpty() || modelField.getText().isEmpty()) throw new SQLException("Brand and Model are required.");

                    String electronicsSql = "INSERT INTO electronicsandgadgets (id, Brand, Model) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = con.prepareStatement(electronicsSql)) {
                        ps.setInt(1, generatedId);
                        ps.setString(2, brandField.getText());
                        ps.setString(3, modelField.getText());
                        ps.executeUpdate();
                    }
                    break;

                case "Bags & Carriers":
                    TextField bagBrandField = (TextField) dynamicFieldsContainer.lookup("#brandField");
                    TextField materialField = (TextField) dynamicFieldsContainer.lookup("#materialField");
                    if (materialField.getText().isEmpty()) throw new SQLException("Material is required.");

                    String bagsSql = "INSERT INTO bags (id, material, brand) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = con.prepareStatement(bagsSql)) {
                        ps.setInt(1, generatedId);
                        ps.setString(2, materialField.getText());
                        ps.setString(3, bagBrandField.getText());
                        ps.executeUpdate();
                    }
                    break;

                case "Clothing & Accessories":
                    TextField accBrandField = (TextField) dynamicFieldsContainer.lookup("#brandField");
                    String accSql = "INSERT INTO accessories (id, Brand) VALUES (?, ?)";
                    try (PreparedStatement ps = con.prepareStatement(accSql)) {
                        ps.setInt(1, generatedId);
                        ps.setString(2, accBrandField.getText());
                        ps.executeUpdate();
                    }
                    break;

                case "Identity & Documents":
                    TextField issueAuthField = (TextField) dynamicFieldsContainer.lookup("#issueAuthorityField");
                    if (issueAuthField.getText().isEmpty()) throw new SQLException("Issuing Authority is required.");

                    String docSql = "INSERT INTO Documents (id, issueauthority) VALUES (?, ?)";
                    try (PreparedStatement ps = con.prepareStatement(docSql)) {
                        ps.setInt(1, generatedId);
                        ps.setString(2, issueAuthField.getText());
                        ps.executeUpdate();
                    }
                    break;

                case "Academic Supplies":
                case "Hobby & Entertainment Gear":
                case "Children’s Belongings":
                    TextField genericBrandField = (TextField) dynamicFieldsContainer.lookup("#brandField");
                    String tableName = category.equals("Academic Supplies") ? "AcademicSupplies" :
                            category.equals("Hobby & Entertainment Gear") ? "entertainmentgears" : "Childstuff";
                    String genericSql = "INSERT INTO " + tableName + " (id, Brand) VALUES (?, ?)";
                    try (PreparedStatement ps = con.prepareStatement(genericSql)) {
                        ps.setInt(1, generatedId);
                        ps.setString(2, genericBrandField.getText());
                        ps.executeUpdate();
                    }
                    break;

                case "Eyewear & Vision Aids":
                    TextField eyeBrandField = (TextField) dynamicFieldsContainer.lookup("#brandField");
                    TextField frameTypeField = (TextField) dynamicFieldsContainer.lookup("#frameTypeField");
                    TextField lensGradeField = (TextField) dynamicFieldsContainer.lookup("#lensGradeField");

                    String eyeSql = "INSERT INTO eyeAndvision (id, frametype, lensgrade, brand) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = con.prepareStatement(eyeSql)) {
                        ps.setInt(1, generatedId);
                        ps.setString(2, frameTypeField.getText());
                        ps.setString(3, lensGradeField.getText());
                        ps.setString(4, eyeBrandField.getText());
                        ps.executeUpdate();
                    }
                    break;

                case "Keys & Access Devices":
                    TextField keyBrandField = (TextField) dynamicFieldsContainer.lookup("#brandField");
                    TextField keyTypeField = (TextField) dynamicFieldsContainer.lookup("#keyTypeField");
                    if (keyTypeField.getText().isEmpty()) throw new SQLException("Key Type is required.");

                    String keySql = "INSERT INTO Keys (id, keytype, Brand) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = con.prepareStatement(keySql)) {
                        ps.setInt(1, generatedId);
                        ps.setString(2, keyTypeField.getText());
                        ps.setString(3, keyBrandField.getText());
                        ps.executeUpdate();
                    }
                    break;
            }

            con.commit();
            // --- UPDATED: New appreciation message ---
            statusLabel.setText("Thank you for reporting! You will be rewarded for your support.");
            clearForm();

        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    private void clearForm() {
        categoryComboBox.setValue(null);
        itemNameField.clear();
        areaField.clear();
        datePicker.setValue(null);
        colorField.clear();
        attributesArea.clear();
        dynamicFieldsContainer.getChildren().clear();

        selectedImageFile = null;
        if (selectedImageLabel != null) {
            selectedImageLabel.setText("No file selected.");
        }
    }
}
