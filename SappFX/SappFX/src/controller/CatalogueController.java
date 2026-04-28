package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import model.Article;
import service.ArticleService;

import java.io.File;
import java.util.List;

public class CatalogueController {

    private static final String IMG_DIR = "C:\\xampp\\htdocs\\Sapp\\Img\\Articles\\";

    @FXML private FlowPane cardsPane;
    @FXML private Label    statusLabel;

    private final ArticleService articleService = new ArticleService();

    @FXML
    public void initialize() {
        loadArticles();
    }

    private void loadArticles() {
        try {
            List<Article> articles = articleService.findAll();
            cardsPane.getChildren().clear();

            if (articles.isEmpty()) {
                statusLabel.setText("Aucun article disponible.");
                return;
            }

            statusLabel.setText(articles.size() + " article(s) disponible(s)");

            for (Article a : articles) {
                cardsPane.getChildren().add(createCard(a));
            }

        } catch (Exception e) {
            statusLabel.setText("Erreur de chargement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createCard(Article a) {

        Region imageZone = buildImageZone(a);

        Label nom = new Label(a.getNom());
        nom.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        nom.setWrapText(true);
        nom.setMaxWidth(170);

        Label type = new Label(a.getType());
        type.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");

        Label prix = new Label(String.format("%.2f €", a.getPrix()));
        prix.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2E8B57;");

        Label stock = new Label("Stock : " + a.getStock());
        stock.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa;");

        String details = "";
        if (a.getGenre() != null && !a.getGenre().isEmpty())   details += a.getGenre();
        if (a.getTaille() != null && !a.getTaille().isEmpty()) details += "  •  T." + a.getTaille();
        Label genre = new Label(details);
        genre.setStyle("-fx-font-size: 11px; -fx-text-fill: #999;");

        VBox card = new VBox(8, imageZone, nom, type, prix, stock, genre);
        card.setPadding(new Insets(12));
        card.setPrefWidth(200);
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #dde1e7;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);"
        );

        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: #f0faf4;" +
            "-fx-border-color: #2E8B57;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #dde1e7;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);"
        ));

        return card;
    }

    private Region buildImageZone(Article a) {

        if (a.getImage() != null && !a.getImage().isEmpty()) {
            File imgFile = new File(IMG_DIR + a.getImage());
            if (imgFile.exists()) {
                try {
                    Image img = new Image(imgFile.toURI().toString(), 176, 160, true, true);
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(176);
                    iv.setFitHeight(160);
                    iv.setPreserveRatio(true);

                    StackPane pane = new StackPane(iv);
                    pane.setPrefSize(176, 160);
                    pane.setStyle(
                        "-fx-background-color: #f8f8f8;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;"
                    );
                    return pane;
                } catch (Exception e) {

                }
            }
        }

        Label fallback = new Label("Image non disponible");
        fallback.setStyle("-fx-font-size: 11px; -fx-text-fill: #bbb;");
        fallback.setAlignment(Pos.CENTER);

        StackPane pane = new StackPane(fallback);
        pane.setPrefSize(176, 160);
        pane.setStyle(
            "-fx-background-color: #f0f0f0;" +
            "-fx-border-color: #ddd;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;"
        );
        return pane;
    }
}
