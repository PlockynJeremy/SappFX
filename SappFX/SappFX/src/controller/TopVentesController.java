package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.StatArticle;
import service.StatsService;

import java.util.List;

public class TopVentesController {

    @FXML private VBox      rootPane;
    @FXML private Label     statusLabel;
    @FXML private VBox      chartsBox;

    private final StatsService statsService = new StatsService();

    @FXML
    public void initialize() {
        statusLabel.setText("Chargement...");

        new Thread(() -> {
            try {
                List<StatArticle> data = statsService.getTopVentes();
                Platform.runLater(() -> buildCharts(data));
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Erreur : " + e.getMessage()));
            }
        }).start();
    }

    private void buildCharts(List<StatArticle> data) {
        chartsBox.getChildren().clear();

        if (data == null || data.isEmpty()) {
            statusLabel.setText("Aucune donnée disponible. Ajoutez des articles au panier sur le site.");
            return;
        }

        statusLabel.setText("⚠️  Une « vente » correspond à un ajout au panier sur le site web.");

        CategoryAxis yAxis = new CategoryAxis();
        NumberAxis   xAxis = new NumberAxis();
        xAxis.setLabel("Nombre de ventes");
        yAxis.setLabel("Article");

        BarChart<Number, String> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("🏆 Top " + data.size() + " — Ventes");
        barChart.setLegendVisible(false);
        barChart.setPrefHeight(350);
        barChart.setAnimated(true);

        XYChart.Series<Number, String> series = new XYChart.Series<>();
        for (StatArticle s : data) {
            String nom = s.getNom().length() > 20 ? s.getNom().substring(0, 20) + "…" : s.getNom();
            series.getData().add(new XYChart.Data<>(s.getNbVentes(), nom));
        }
        barChart.getData().add(series);

        barChart.setStyle("-fx-background-color: #f4f6f9;");
        for (XYChart.Data<Number, String> d : series.getData()) {
            if (d.getNode() != null) {
                d.getNode().setStyle("-fx-bar-fill: #2E8B57;");
            }
        }
        series.getData().forEach(d ->
            d.nodeProperty().addListener((obs, o, n) -> {
                if (n != null) n.setStyle("-fx-bar-fill: #2E8B57;");
            })
        );

        PieChart pieChart = new PieChart();
        pieChart.setTitle("📊 Répartition des ventes");
        pieChart.setPrefHeight(350);
        pieChart.setAnimated(true);
        pieChart.setLegendVisible(true);

        int total = data.stream().mapToInt(StatArticle::getNbVentes).sum();
        for (StatArticle s : data) {
            String nom = s.getNom().length() > 18 ? s.getNom().substring(0, 18) + "…" : s.getNom();
            double pct = total > 0 ? Math.round(s.getNbVentes() * 1000.0 / total) / 10.0 : 0;
            PieChart.Data slice = new PieChart.Data(nom + " (" + pct + "%)", s.getNbVentes());
            pieChart.getData().add(slice);
        }

        Label resume = new Label("Total : " + total + " vente(s) enregistrée(s)");
        resume.setStyle("-fx-font-size:13px; -fx-text-fill:#555;");

        chartsBox.getChildren().addAll(barChart, pieChart, resume);
    }
}
