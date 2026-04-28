package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import model.Article;
import service.ArticleService;

import java.io.File;
import java.util.List;

public class GestionArticlesController {

    @FXML private TableView<Article>             tableArticles;
    @FXML private TableColumn<Article, Integer>  colId;
    @FXML private TableColumn<Article, String>   colNom;
    @FXML private TableColumn<Article, String>   colType;
    @FXML private TableColumn<Article, String>   colCouleur;
    @FXML private TableColumn<Article, Double>   colPrix;
    @FXML private TableColumn<Article, Integer>  colStock;
    @FXML private TableColumn<Article, String>   colTaille;
    @FXML private TableColumn<Article, String>   colGenre;

    @FXML private TextField     fieldNom;
    @FXML private TextField     fieldType;
    @FXML private TextField     fieldCouleur;
    @FXML private TextField     fieldPrix;
    @FXML private TextField     fieldStock;
    @FXML private TextField     fieldTaille;
    @FXML private TextField     fieldGenre;
    @FXML private TextField     fieldDescription;

    @FXML private ImageView     imagePreview;
    @FXML private Label         labelImageNom;
    @FXML private Label         statusLabel;

    private File selectedImageFile = null;

    private static final String IMG_DIR = "C:\\xampp\\htdocs\\Sapp\\Img\\Articles\\";

    private final ArticleService articleService = new ArticleService();
    private final ObservableList<Article> articleList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d ->
            new javafx.beans.property.SimpleIntegerProperty(d.getValue().getId()).asObject());
        colNom.setCellValueFactory(d ->
            new javafx.beans.property.SimpleStringProperty(d.getValue().getNom()));
        colType.setCellValueFactory(d ->
            new javafx.beans.property.SimpleStringProperty(d.getValue().getType()));
        colCouleur.setCellValueFactory(d ->
            new javafx.beans.property.SimpleStringProperty(d.getValue().getCouleur()));
        colPrix.setCellValueFactory(d ->
            new javafx.beans.property.SimpleDoubleProperty(d.getValue().getPrix()).asObject());
        colStock.setCellValueFactory(d ->
            new javafx.beans.property.SimpleIntegerProperty(d.getValue().getStock()).asObject());
        colTaille.setCellValueFactory(d ->
            new javafx.beans.property.SimpleStringProperty(d.getValue().getTaille()));
        colGenre.setCellValueFactory(d ->
            new javafx.beans.property.SimpleStringProperty(d.getValue().getGenre()));

        tableArticles.setItems(articleList);

        tableArticles.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, selected) -> { if (selected != null) fillForm(selected); }
        );

        loadArticles();
    }

    private void loadArticles() {
        try {
            List<Article> list = articleService.findAll();
            articleList.setAll(list);
            statusLabel.setText(list.size() + " article(s) chargé(s).");
        } catch (Exception e) {
            statusLabel.setText("Erreur chargement : " + e.getMessage());
        }
    }

    private void fillForm(Article a) {
        fieldNom.setText(a.getNom());
        fieldType.setText(a.getType());
        fieldCouleur.setText(a.getCouleur()     != null ? a.getCouleur()     : "");
        fieldPrix.setText(String.valueOf(a.getPrix()));
        fieldStock.setText(String.valueOf(a.getStock()));
        fieldTaille.setText(a.getTaille()       != null ? a.getTaille()      : "");
        fieldGenre.setText(a.getGenre()         != null ? a.getGenre()       : "");
        fieldDescription.setText(a.getDescription() != null ? a.getDescription() : "");

        selectedImageFile = null;
        labelImageNom.setText(a.getImage() != null ? a.getImage() : "Aucune image");

        showImagePreview(a.getImage() != null ? new File(IMG_DIR + a.getImage()) : null);
    }

    private void showImagePreview(File file) {
        if (file != null && file.exists()) {
            try {
                Image img = new Image(file.toURI().toString(), 160, 120, true, true);
                imagePreview.setImage(img);
                return;
            } catch (Exception ignored) {}
        }

        imagePreview.setImage(null);
    }

    @FXML
    private void handleChoisirImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir une image");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png")
        );

        File file = chooser.showOpenDialog(fieldNom.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            labelImageNom.setText(file.getName());
            showImagePreview(file);
        }
    }

    private Article readForm() {
        Article a = new Article();
        a.setNom(fieldNom.getText().trim());
        a.setType(fieldType.getText().trim());
        a.setCouleur(fieldCouleur.getText().trim());
        try { a.setPrix(Double.parseDouble(fieldPrix.getText().trim())); } catch (Exception e) { a.setPrix(0); }
        try { a.setStock(Integer.parseInt(fieldStock.getText().trim())); } catch (Exception e) { a.setStock(0); }
        a.setTaille(fieldTaille.getText().trim());
        a.setGenre(fieldGenre.getText().trim());
        a.setDescription(fieldDescription.getText().trim());
        return a;
    }

    private void clearForm() {
        fieldNom.clear(); fieldType.clear(); fieldCouleur.clear();
        fieldPrix.clear(); fieldStock.clear(); fieldTaille.clear();
        fieldGenre.clear(); fieldDescription.clear();
        selectedImageFile = null;
        labelImageNom.setText("Aucune image sélectionnée");
        imagePreview.setImage(null);
        tableArticles.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleAjouter() {
        if (fieldNom.getText().isEmpty() || fieldPrix.getText().isEmpty()) {
            statusLabel.setText("Nom et Prix sont obligatoires.");
            return;
        }
        if (selectedImageFile == null) {
            statusLabel.setText("Veuillez sélectionner une image.");
            return;
        }
        try {
            articleService.create(readForm(), selectedImageFile);
            loadArticles();
            clearForm();
            statusLabel.setText("Article ajouté !");
        } catch (Exception e) {
            statusLabel.setText("Erreur ajout : " + e.getMessage());
        }
    }

    @FXML
    private void handleModifier() {
        Article selected = tableArticles.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Sélectionnez un article à modifier.");
            return;
        }

        try {
            articleService.update(selected.getId(), readForm(), selectedImageFile);
            loadArticles();
            clearForm();
            statusLabel.setText("Article modifié !");
        } catch (Exception e) {
            statusLabel.setText("Erreur modification : " + e.getMessage());
        }
    }

    @FXML
    private void handleSupprimer() {
        Article selected = tableArticles.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Sélectionnez un article à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText(null);
        confirm.setContentText("Supprimer l'article \"" + selected.getNom() + "\" ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    articleService.delete(selected.getId());
                    loadArticles();
                    clearForm();
                    statusLabel.setText("Article supprimé !");
                } catch (Exception e) {
                    statusLabel.setText("Erreur suppression : " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleRafraichir() {
        loadArticles();
        clearForm();
    }
}
