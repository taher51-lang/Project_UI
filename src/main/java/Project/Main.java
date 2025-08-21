package Project;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main extends Application {
    public static int id; // Assuming this is used to store user ID
    static Connection con; // To be used by other parts of the application

    public static void main(String[] args) {
        launch(args); // This calls the start method
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui/Login.fxml"));
        primaryStage.setTitle("Lost and Found Login");
        primaryStage.setScene(new Scene(root)); //creates a new Scene (the content of the window) with the loaded UI and sets it on the main window,
        primaryStage.show(); //makes window visible to user
    }

    // Static login method to be called from the controller
    public static boolean login(String userName, String pass) throws SQLException {
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
}