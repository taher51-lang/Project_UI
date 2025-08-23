package Project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class User {
    static Connection con;



    //  the  method that accepts parameters from the UI
    static String registration(String name, String username, String pass, String mobile, String email_id) throws ClassNotFoundException, SQLException {
        String driverName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverName);
        String dbUrl = "jdbc:mysql://localhost:3306/project";
        String dbUser = "root";
        String dpPass = "";
        con = DriverManager.getConnection(dbUrl, dbUser, dpPass);


        String checkSql = "select count(*) from user where user_name = ?";
        try (PreparedStatement checkSt = con.prepareStatement(checkSql)) {
            checkSt.setString(1, username);
            try (ResultSet rs = checkSt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return "Error: The username is already taken! Kindly choose a different one.";
                }
            }
        }

        String sql = "insert into user(user_name, Name, mobileNo, email_id, user_pass) values(?, ?, ?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, username);
            pst.setString(2, name);
            pst.setLong(3, Long.parseLong(mobile)); // Parse string to long
            pst.setString(4, email_id);
            String hashedPassword = BCrypt.hashpw(pass, BCrypt.gensalt());
            pst.setString(5, hashedPassword); // *REMINDER: HASH THIS PASSWORD FOR SECURITY*
            pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    Main.id = rs.getInt(1);
                }
            }
        }
        return "Registration successful! You can now log in.";
    }

    public static class ReportLostItemController {

        @FXML
        private ComboBox<String> categoryComboBox;

        @FXML
        private TextField itemNameField;

        @FXML
        private TextField areaField;

        @FXML
        private DatePicker datePicker;

        @FXML
        private TextField colorField;

        @FXML
        private TextArea attributesArea;

        @FXML
        private Label statusLabel;


        @FXML
        public void initialize() {
            // Populate the category ComboBox with the item types from your console app
            categoryComboBox.getItems().addAll(
                    "Electronics & Gadgets",
                    "Bags & Carriers",
                    "Clothing & Accessories",
                    "Identity & Documents",
                    "Academic Supplies",
                    "Hobby & Entertainment Gear",
                    "Children’s Belongings",
                    "Eyewear & Vision Aids",
                    "Keys & Access Devices"
            );
        }


        @FXML
        private void handleSubmitButtonAction(ActionEvent event) {
            // --- 1. Get User Input ---
            String category = categoryComboBox.getValue();
            String itemName = itemNameField.getText();
            String area = areaField.getText();
            String color = colorField.getText();
            String attributes = attributesArea.getText();

            // --- 2. Basic Validation ---
            if (category == null || itemName.isEmpty() || area.isEmpty() || datePicker.getValue() == null || color.isEmpty()) {
                statusLabel.setText("Please fill in all required fields.");
                return;
            }

            // --- 3. Database Insertion ---
            String sql = "INSERT INTO Lostandfound(uid, name, area, date, color, attribute, status, plocation) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
                 PreparedStatement ps = con.prepareStatement(sql)) {

                String[] locationParts = area.split(",");
                String primaryLocation = locationParts.length > 0 ? locationParts[locationParts.length - 1].trim() : area;

                ps.setInt(1, Main.id); // The ID of the logged-in user
                ps.setString(2, itemName);
                ps.setString(3, area);
                ps.setDate(4, Date.valueOf(datePicker.getValue()));
                ps.setString(5, color);
                ps.setString(6, attributes);
                ps.setString(7, "lost"); // The status is 'lost'
                ps.setString(8, primaryLocation);

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    statusLabel.setText("Report submitted successfully!");
                    clearForm();
                } else {
                    statusLabel.setText("Failed to submit report. Please try again.");
                }

            } catch (SQLException e) {
                statusLabel.setText("Database error occurred.");
                e.printStackTrace();
            }
        }


        private void clearForm() {
            categoryComboBox.setValue(null);
            itemNameField.clear();
            areaField.clear();
            datePicker.setValue(null);
            colorField.clear();
            attributesArea.clear();
        }
    }
}