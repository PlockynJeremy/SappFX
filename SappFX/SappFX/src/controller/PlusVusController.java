package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import model.StatArticle;
import service.StatsService;

import java.util.List;

public class PlusVusController {

    @FXML private VBox  rootPane;
    @FXML private Label statusLabel;
    @FXML private VBox  chartsBox;

    private final StatsService statsService = new StatsService();

    @FXML
    public void initialize() {
        statusLabel.setText("Chargement...");
        new Thread(() -> {
            try {
                List<StatArticle> data = statsService.getTopVues();
                Platform.runLater(() -> buildCharts(data));
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Erreur : " + e.getMessage()));
            }
        }).start();
    }

    private void buildCharts(List<StatArticle> data) {
        chartsBox.getChildren().clear();

        if (data == null || data.isEmpty()) {
            statusLabel.setText("Aucune donnée disponible. Consultez des articles sur le site.");
            return;
        }

        statusLabel.setText("⚠️  Une « vue » correspond à un clic sur un article sur le site web.");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();
        xAxis.setLabel("Article");
        yAxis.setLabel("Nombre de vues");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("👁️ Top " + data.size() + " — Articles les plus vus");
        barChart.setLegendVisible(false);
        barChart.setPrefHeight(350);
        barChart.setAnimated(true);
        barChart.setStyle("-fx-background-color: #f4f6f9;");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (StatArticle s : data) {
            String nom = s.getNom().length() > 14 ? s.getNom().substring(0, 14) + "…" : s.getNom();
            series.getData().add(new XYChart.Data<>(nom, s.getNbVues()));
        }
        barChart.getData().add(series);

        series.getData().forEach(d ->
            d.nodeProperty().addListener((obs, o, n) -> {
                if (n != null) n.setStyle("-fx-bar-fill: #3498db;");
            })
        );

        PieChart pieChart = new PieChart();
        pieChart.setTitle("📊 Répartition des vues");
        pieChart.setPrefHeight(350);
        pieChart.setAnimated(true);
        pieChart.setLegendVisible(true);

        int total = data.stream().mapToInt(StatArticle::getNbVues).sum();
        for (StatArticle s : data) {
            String nom = s.getNom().length() > 18 ? s.getNom().substring(0, 18) + "…" : s.getNom();
            double pct = total > 0 ? Math.round(s.getNbVues() * 1000.0 / total) / 10.0 : 0;
            PieChart.Data slice = new PieChart.Data(nom + " (" + pct + "%)", s.getNbVues());
            pieChart.getData().add(slice);
        }

        Label resume = new Label("Total : " + total + " vue(s) enregistrée(s)");
        resume.setStyle("-fx-font-size:13px; -fx-text-fill:#555;");

        chartsBox.getChildren().addAll(barChart, pieChart, resume);
    }
}
