package Project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class Main extends Application {
    static int id;
    static String loggedInUserName;
    static Connection con;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/gui/RoleSelection.fxml"));
            primaryStage.setTitle("Welcome - Select Your Role");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean login(String userName, String pass) throws SQLException {
        // ... (existing user login logic)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "SELECT userId, name, user_pass FROM user WHERE user_name = ?";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, userName);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("userId");
                    String name = rs.getString("name");
                    String storedHash = rs.getString("user_pass");

                    if (BCrypt.checkpw(pass, storedHash)) {
                        id = userId;
                        loggedInUserName = name; // Store the user's name
                        System.out.println("Hi " + name + "!!");
                        System.out.println("You have logged in successfully!");
                        return true;
                    } else {
                        System.out.println("Incorrect password.");
                        return false;
                    }
                } else {
                    System.out.println("User not found.");
                }
            }
        } finally {
            if (con != null) {
                con.close();
            }
        }
        return false;
    }


    public static boolean adminLogin(String adminName, String password) {
        String sql = "SELECT * FROM admin WHERE name_admin = ? AND password = ?";
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, adminName);
            pstmt.setString(2, password); // Note: Storing passwords in plaintext is not secure.

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("Admin login successful.");
                return true;
            } else {
                System.out.println("Invalid admin name or password.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error during admin login: " + e.getMessage());
            return false;
        }
    }

    /**
     * NEW METHOD: Handles the registration logic for a new admin.
     */
    public static boolean adminRegistration(String username, String name, String email, String password, long contact) {
        String sql = "INSERT INTO admin (username_admin, name_admin, email_admin, password, contactno) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            pstmt.setString(4, password); // Note: Storing passwords in plaintext is not secure
            pstmt.setLong(5, contact);

            int rowsInserted = pstmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println("Error during admin registration: " + e.getMessage());
            return false;
        }
    }
    public static boolean approveVerification(int lostUserId, int foundUserId, int foundItemId) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
             CallableStatement cst = con.prepareCall("call afterVerification(?,?,?)")) {

            cst.setInt(1, lostUserId);
            cst.setInt(2, foundUserId);
            cst.setInt(3, foundItemId);
            cst.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Denies a verification request by calling the afterRejection stored procedure.
     */
    public static boolean denyVerification(int lostUserId, int foundUserId) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/project", "root", "");
             CallableStatement cst = con.prepareCall("call afterRejection(?,?)")) {

            cst.setInt(1, lostUserId);
            cst.setInt(2, foundUserId);
            cst.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}


