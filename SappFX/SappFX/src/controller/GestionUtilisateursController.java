package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.User;
import service.UserService;

import java.util.List;

public class GestionUtilisateursController {

    @FXML private TableView<User>            tableUsers;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String>  colUsername;
    @FXML private TableColumn<User, String>  colRole;

    @FXML private TextField     fieldUsername;
    @FXML private PasswordField fieldPassword;
    @FXML private ComboBox<String> comboRole;

    @FXML private Label statusLabel;

    private final UserService userService = new UserService();
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d ->
            new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()).asObject());
        colUsername.setCellValueFactory(d ->
            new javafx.beans.property.SimpleStringProperty(d.getValue().getUsername()));
        colRole.setCellValueFactory(d ->
            new javafx.beans.property.SimpleStringProperty(d.getValue().getRole()));

        tableUsers.setItems(userList);

        comboRole.setItems(FXCollections.observableArrayList("admin", "client"));
        comboRole.getSelectionModel().selectFirst();

        loadUsers();
    }

    private void loadUsers() {
        try {
            List<User> list = userService.findAll();
            userList.setAll(list);
            statusLabel.setText(list.size() + " utilisateur(s) chargé(s).");
        } catch (Exception e) {
            statusLabel.setText("Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void handleAjouter() {
        String username = fieldUsername.getText().trim();
        String password = fieldPassword.getText().trim();
        String role     = comboRole.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Username et mot de passe requis.");
            return;
        }

        try {
            userService.addUser(username, password, role);
            loadUsers();
            fieldUsername.clear();
            fieldPassword.clear();
            comboRole.getSelectionModel().selectFirst();
            statusLabel.setText("Utilisateur ajouté !");
        } catch (Exception e) {
            statusLabel.setText("Erreur ajout : " + e.getMessage());
        }
    }

    @FXML
    private void handleSupprimer() {
        User selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Sélectionnez un utilisateur à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer l'utilisateur \"" + selected.getUsername() + "\" ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userService.deleteUser(selected.getId());
                    loadUsers();
                    statusLabel.setText("Utilisateur supprimé !");
                } catch (Exception e) {
                    statusLabel.setText("Erreur suppression : " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleRafraichir() {
        loadUsers();
    }
}
