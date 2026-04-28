package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

import util.SessionUser;

public class MainController {

    @FXML private StackPane contentPane;

    @FXML private Menu menuGestionSite;
    @FXML private Menu menuStatistique;

    @FXML
    public void initialize() {

        if (!SessionUser.hasRole("admin")) {
            menuGestionSite.setVisible(false);
            menuStatistique.setVisible(false);
        }

        loadPage("/view/Home.fxml");
    }

    private void loadPage(String fxmlPath) {
        try {
            System.out.println("Chargement : " + fxmlPath);
            Pane page = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().clear();
            contentPane.getChildren().add(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void showHome()      { loadPage("/view/Home.fxml"); }
    @FXML private void showCatalogue() { loadPage("/view/CatalogueView.fxml"); }
    @FXML private void handleQuit()    { System.exit(0); }

    @FXML private void showPanier()       { loadPage("/view/Panier.fxml"); }
    @FXML private void handleDeconnexion() {

        SessionUser.clear();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("SappFX - Connexion");
            stage.setScene(scene);
            stage.show();

            contentPane.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void showTopVentes()  { loadPage("/view/TopVentes.fxml"); }
    @FXML private void showPlusVus()    { loadPage("/view/PlusVus.fxml"); }

    @FXML private void showGestionArticles()     { loadPage("/view/GestionArticles.fxml"); }
    @FXML private void showGestionUtilisateurs() { loadPage("/view/GestionUtilisateurs.fxml"); }

    @FXML private void showApropos() { loadPage("/view/Apropos.fxml"); }
    @FXML private void showAide()    { loadPage("/view/Aide.fxml"); }
}
