package Project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

// NOTE: You will need to add the String Similarity library to your project (see pom.xml dependency)



public class ReportLostItemController implements Initializable {

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

    @FXML
    private void handleChooseImageAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Item Image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
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
        int generatedId = -1;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
            con.setAutoCommit(false);

            String mainSql = "INSERT INTO Lostandfound(uid, name, area, date, color, attribute, status, plocation, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement psMain = con.prepareStatement(mainSql, Statement.RETURN_GENERATED_KEYS)) {
                String[] locationParts = areaField.getText().split(",");
                String primaryLocation = locationParts.length > 0 ? locationParts[locationParts.length - 1].trim() : areaField.getText();
                psMain.setInt(1, Main.id);
                psMain.setString(2, itemNameField.getText());
                psMain.setString(3, areaField.getText());
                psMain.setDate(4, Date.valueOf(datePicker.getValue()));
                psMain.setString(5, colorField.getText());
                psMain.setString(6, attributesArea.getText());
                psMain.setString(7, "lost");
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

            con.commit();
            statusLabel.setText("Report submitted successfully! Now searching for matches...");

            findMatchesAndProcess(con, generatedId);

        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
            if (con != null) { try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
        } finally {
            if (con != null) { try { con.setAutoCommit(true); con.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    private void findMatchesAndProcess(Connection con, int lostItemId) throws SQLException {
        Item subject = createItemFromForm();
        if (subject == null) {
            statusLabel.setText("Could not process item details for matching.");
            return;
        }

        DoublyLinkedList stage1 = searchForFoundItems(con, subject);
        if (stage1 == null || !stage1.exists()) {
            statusLabel.setText("Report submitted. No potential matches found at this time.");
            return;
        }

        DoublyLinkedList stage2 = stage1.flFilter(subject);
        if (!stage2.exists()) {
            statusLabel.setText("Report submitted. No close matches found.");
            return;
        }

        DoublyLinkedList stage3 = stage2.filterMoreAttributes(subject, subject.getClassName());

        if (stage3.exists()) {
            // --- Show Payment Dialog FIRST ---
            TextInputDialog dialog = new TextInputDialog("250");
            dialog.setTitle("Payment Required");
            dialog.setHeaderText("Potential Match Found!");
            dialog.setContentText("Please enter the service amount (min. Rs 250) to proceed with verification:");

            Optional<String> result = dialog.showAndWait();

            // --- Process Payment and THEN set verification ---
            result.ifPresent(amountStr -> {
                try {
                    int amount = Integer.parseInt(amountStr);
                    if (amount >= 250) {
                        // --- Update User's Wallet in a new, clean transaction ---
                        String updateSql = "UPDATE user SET Wallet = ? WHERE userId = ?";
                        // Use a new try-with-resources block to manage the connection
                        try (Connection paymentCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
                             PreparedStatement pst = paymentCon.prepareStatement(updateSql)) {

                            pst.setInt(1, amount);
                            pst.setInt(2, Main.id);
                            pst.executeUpdate();

                            // Now that payment is confirmed, set for verification
                            stage3.setAdminVerification(lostItemId, Main.id);
                            statusLabel.setText("Match found! Payment successful. Awaiting admin verification.");

                        } catch (SQLException dbError) {
                            statusLabel.setText("Database error during payment. Verification cancelled.");
                            System.out.println(dbError.getMessage());
                        }
                    } else {
                        statusLabel.setText("Payment must be at least Rs 250. Verification cancelled.");
                    }
                } catch (NumberFormatException e) {
                    statusLabel.setText("Invalid amount or database error. Verification cancelled.");
                    e.printStackTrace();
                }
            });
        } else {
            statusLabel.setText("Report submitted. No strong matches found after detailed filtering.");
        }
    }

    private Item createItemFromForm() {
        String category = categoryComboBox.getValue();
        Item item = null;
        try {
            switch (category) {
                case "Electronics & Gadgets":
                    Electronics e = new Electronics();
                    e.brand = ((TextField) dynamicFieldsContainer.lookup("#brandField")).getText();
                    e.model = ((TextField) dynamicFieldsContainer.lookup("#modelField")).getText();
                    item = e;
                    break;
                case "Bags & Carriers":
                    Bags b = new Bags();
                    b.brand = ((TextField) dynamicFieldsContainer.lookup("#brandField")).getText();
                    b.material = ((TextField) dynamicFieldsContainer.lookup("#materialField")).getText();
                    item = b;
                    break;
                case "Clothing & Accessories":
                    Accessories a = new Accessories();
                    a.brand = ((TextField) dynamicFieldsContainer.lookup("#brandField")).getText();
                    item = a;
                    break;
                case "Identity & Documents":
                    Documents d = new Documents();
                    d.issueAuthority = ((TextField) dynamicFieldsContainer.lookup("#issueAuthorityField")).getText();
                    item = d;
                    break;
                case "Academic Supplies":
                    AcademicSupplies as = new AcademicSupplies();
                    as.brand = ((TextField) dynamicFieldsContainer.lookup("#brandField")).getText();
                    item = as;
                    break;
                case "Hobby & Entertainment Gear":
                    EntertainmentGears eg = new EntertainmentGears();
                    eg.brand = ((TextField) dynamicFieldsContainer.lookup("#brandField")).getText();
                    item = eg;
                    break;
                case "Children’s Belongings":
                    ChildStuff cs = new ChildStuff();
                    cs.brand = ((TextField) dynamicFieldsContainer.lookup("#brandField")).getText();
                    item = cs;
                    break;
                case "Eyewear & Vision Aids":
                    eyeAndVision ev = new eyeAndVision();
                    ev.brand = ((TextField) dynamicFieldsContainer.lookup("#brandField")).getText();
                    ev.FrameType = ((TextField) dynamicFieldsContainer.lookup("#frameTypeField")).getText();
                    ev.lensGrade = ((TextField) dynamicFieldsContainer.lookup("#lensGradeField")).getText();
                    item = ev;
                    break;
                case "Keys & Access Devices":
                    Keys k = new Keys();
                    k.brand = ((TextField) dynamicFieldsContainer.lookup("#brandField")).getText();
                    k.keyType = ((TextField) dynamicFieldsContainer.lookup("#keyTypeField")).getText();
                    item = k;
                    break;
            }

            if (item != null) {
                item.name = itemNameField.getText();
                item.area = areaField.getText();
                item.color = colorField.getText();
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error reading item details from form.");
            return null;
        }
        return item;
    }

    private DoublyLinkedList searchForFoundItems(Connection con, Item subject) throws SQLException {
        String className = subject.getClassName();
        String sqlQuery = "";

        String baseSelect = "SELECT l.uid, l.id, l.name, l.area, l.date, l.color, l.attribute, ";

        switch (className) {
            case "Main.Electronics":
                sqlQuery = baseSelect + "e.Brand, e.Model FROM Lostandfound l JOIN electronicsandgadgets e ON l.id = e.id";
                break;
            case "Main.Bags":
                sqlQuery = baseSelect + "b.material, b.brand FROM Lostandfound l JOIN bags b ON l.id = b.id";
                break;
            case "Main.Accessories":
                sqlQuery = baseSelect + "a.Brand FROM Lostandfound l JOIN accessories a ON l.id = a.id";
                break;
            case "Main.Documents":
                sqlQuery = baseSelect + "d.isssueauthority FROM Lostandfound l JOIN Documents d ON l.id = d.id";
                break;
            case "Main.AcademicSupplies":
                sqlQuery = baseSelect + "a.Brand FROM Lostandfound l JOIN AcademicSupplies a ON l.id = a.id";
                break;
            case "Main.EntertainmentGears":
                sqlQuery = baseSelect + "e.Brand FROM Lostandfound l JOIN entertainmentgears e ON l.id = e.id";
                break;
            case "Main.ChildStuff":
                sqlQuery = baseSelect + "c.Brand FROM Lostandfound l JOIN Childstuff c ON l.id = c.id";
                break;
            case "Main.eyeAndVision":
                sqlQuery = baseSelect + "e.frametype, e.lensgrade, e.brand FROM Lostandfound l JOIN eyeAndVision e ON l.id = e.id";
                break;
            case "Main.Keys":
                sqlQuery = baseSelect + "k.keytype, k.Brand FROM Lostandfound l JOIN KeyAccess k ON l.id = k.id";
                break;
            default:
                throw new SQLException("Unknown item type for search: " + className);
        }

        sqlQuery += " WHERE l.status = 'found' AND l.date BETWEEN CURDATE() - INTERVAL 1 MONTH AND CURDATE()";

        DoublyLinkedList list = new DoublyLinkedList();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sqlQuery)) {

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            while (rs.next()) {
                HashMap<String, String> record = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    record.put(rsmd.getColumnName(i), rs.getString(i));
                }
                list.insertAtLast(record);
            }
        }
        return list;
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
