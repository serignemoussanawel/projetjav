package com.campus.ui.controllers;

import com.campus.managers.GestionBatiment;
import com.campus.managers.GestionChambre;
import com.campus.managers.GestionEtudiant;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StatisticsViewController {
    private final GestionBatiment gestionBatiment;
    private final GestionChambre gestionChambre;
    private final GestionEtudiant gestionEtudiant;

    public StatisticsViewController(GestionBatiment gestionBatiment, GestionChambre gestionChambre,
            GestionEtudiant gestionEtudiant) {
        this.gestionBatiment = gestionBatiment;
        this.gestionChambre = gestionChambre;
        this.gestionEtudiant = gestionEtudiant;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Statistiques");
        Scene scene = new Scene(createView(), 1180, 720);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public VBox createView() {
        VBox root = new VBox(18);
        root.getStyleClass().add("content-panel");

        Label title = new Label("Statistiques de Gestion");
        title.getStyleClass().add("view-title");

        Label subtitle = new Label("Visualisez les indicateurs principaux de l'application sur un grand écran.");
        subtitle.getStyleClass().add("panel-subtitle");

        HBox topCards = new HBox(16,
                createStatCard("Bâtiments", String.valueOf(gestionBatiment.getAllBatiments().size())),
                createStatCard("Chambres", String.valueOf(gestionChambre.getAllChambres().size())),
                createStatCard("Étudiants", String.valueOf(gestionEtudiant.getNombreTotalEtudiants())),
                createStatCard("Occupation", String.format("%.1f%%", calculateOccupancyRate())));

        GridPane statsGrid = createStatsGrid();
        BarChart<String, Number> chart = createChart();
        VBox.setVgrow(chart, Priority.ALWAYS);

        root.getChildren().addAll(title, subtitle, topCards, statsGrid, chart);
        return root;
    }

    private GridPane createStatsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        addStatCard(grid, 0, 0, "Chambres Libres", String.valueOf(gestionChambre.getNombreChambresLibres()));
        addStatCard(grid, 1, 0, "Chambres Occupées", String.valueOf(gestionChambre.getNombreChambresOccupees()));
        addStatCard(grid, 0, 1, "Étudiants Logés", String.valueOf(gestionEtudiant.getNombreEtudiantsLogis()));
        addStatCard(grid, 1, 1, "Étudiants Sans Chambre",
                String.valueOf(gestionEtudiant.getNombreTotalEtudiants() - gestionEtudiant.getNombreEtudiantsLogis()));

        return grid;
    }

    private VBox createStatCard(String title, String value) {
        VBox card = new VBox(8);
        card.getStyleClass().add("stat-card");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private void addStatCard(GridPane grid, int col, int row, String title, String value) {
        VBox card = new VBox(8);
        card.getStyleClass().add("admin-action-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefWidth(280);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("admin-action-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");

        card.getChildren().addAll(titleLabel, valueLabel);
        grid.add(card, col, row);
    }

    private BarChart<String, Number> createChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Vue d'ensemble");
        chart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Chambres Libres", gestionChambre.getNombreChambresLibres()));
        series.getData().add(new XYChart.Data<>("Chambres Occupées", gestionChambre.getNombreChambresOccupees()));
        series.getData().add(new XYChart.Data<>("Étudiants Logés", gestionEtudiant.getNombreEtudiantsLogis()));
        chart.getData().add(series);

        return chart;
    }

    private double calculateOccupancyRate() {
        int total = gestionChambre.getAllChambres().size();
        if (total == 0) {
            return 0;
        }
        return (double) gestionChambre.getNombreChambresOccupees() / total * 100;
    }
}
