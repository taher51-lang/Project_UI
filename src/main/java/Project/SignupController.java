package Project;



import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.SQLException;
import java.sql.Statement;

public class SignupController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField mobileField;

    @FXML
    private TextField emailField;

    @FXML
    private Label messageLabel;

    @FXML
    public void handleSignupButtonAction() {
        String name = nameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String mobile = mobileField.getText();
        String email = emailField.getText();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || mobile.isEmpty() || email.isEmpty()) {
            messageLabel.setText("All fields are required.");
            return;
        }

        // 2. Validate the mobile number
        if (!mobile.matches("[8-9][0-9]{9}")) {
            messageLabel.setText("Invalid Mobile No. It must be 10 digits and start with 8 or 9.");
            return;
        }

        try {
            String result=User.registration(name, username, password, mobile, email);
            messageLabel.setText(result);
        } catch (SQLException e) {
            messageLabel.setText("Error during registration: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            messageLabel.setText("Database driver not found.");
            e.printStackTrace();
        }
    }
}