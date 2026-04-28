package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import model.LoginResponse;
import model.User;
import service.UserService;
import util.SessionUser;

public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin() {

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        LoginResponse response = userService.login(username, password);

        if (response != null && response.isSuccess()) {

            User user = response.getUser();
            SessionUser.setUser(user);

            System.out.println("Connecté en tant que : " + user.getUsername() + " [" + user.getRole() + "]");

            openMain();

        } else {
            showAlert("Erreur", "Identifiants incorrects.");
        }
    }

    private void openMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Main.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1024, 700);
            scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("SappFX - Catalogue");
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setScene(scene);
            stage.show();

            usernameField.getScene().getWindow().hide();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
