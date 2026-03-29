package com.campus.ui.controllers;

import com.campus.managers.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StatisticsViewController {
    private GestionBatiment gestionBatiment;
    private GestionChambre gestionChambre;
    private GestionEtudiant gestionEtudiant;
    private Stage primaryStage;

    public StatisticsViewController(GestionBatiment gestionBatiment, GestionChambre gestionChambre,
                                  GestionEtudiant gestionEtudiant, Stage primaryStage) {
        this.gestionBatiment = gestionBatiment;
        this.gestionChambre = gestionChambre;
        this.gestionEtudiant = gestionEtudiant;
        this.primaryStage = primaryStage;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Statistiques");
        stage.setScene(new Scene(createLayout(), 900, 600));
        stage.show();
    }

    private VBox createLayout() {
        VBox root = new VBox(15);
        root.setStyle("-fx-padding: 15;");

        Label title = new Label("Statistiques Générales");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        GridPane statsGrid = createStatsGrid();
        BarChart<String, Number> chart = createChart();

        root.getChildren().addAll(title, statsGrid, chart);
        return root;
    }

    private GridPane createStatsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setStyle("-fx-padding: 10;");

        // Stats cards
        addStatCard(grid, 0, 0, "Nombre Total de Bâtiments", 
                   String.valueOf(gestionBatiment.getAllBatiments().size()));
        addStatCard(grid, 1, 0, "Nombre Total de Chambres", 
                   String.valueOf(gestionChambre.getAllChambres().size()));
        addStatCard(grid, 0, 1, "Chambres Libres", 
                   String.valueOf(gestionChambre.getNombreChambresLibres()));
        addStatCard(grid, 1, 1, "Chambres Occupées", 
                   String.valueOf(gestionChambre.getNombreChambresOccupees()));
        addStatCard(grid, 0, 2, "Nombre Total d'Étudiants", 
                   String.valueOf(gestionEtudiant.getNombreTotalEtudiants()));
        addStatCard(grid, 1, 2, "Étudiants Logés", 
                   String.valueOf(gestionEtudiant.getNombreEtudiantsLogis()));

        double tauxOccupation = calculateOccupancyRate();
        addStatCard(grid, 0, 3, "Taux d'Occupation", 
                   String.format("%.1f%%", tauxOccupation));

        return grid;
    }

    private void addStatCard(GridPane grid, int col, int row, String title, String value) {
        VBox card = new VBox(10);
        card.setStyle("-fx-padding: 15; -fx-border-color: #cccccc; -fx-border-width: 1; " +
                     "-fx-background-color: #f5f5f5;");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(200);
        card.setPrefHeight(100);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #2196F3;");

        card.getChildren().addAll(titleLabel, valueLabel);
        grid.add(card, col, row);
    }

    private BarChart<String, Number> createChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Catégories");
        yAxis.setLabel("Nombre");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Vue d'ensemble");
        chart.setPrefHeight(300);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Statistiques");
        series.getData().add(new XYChart.Data<>("Chambres Libres", gestionChambre.getNombreChambresLibres()));
        series.getData().add(new XYChart.Data<>("Chambres Occupées", gestionChambre.getNombreChambresOccupees()));
        series.getData().add(new XYChart.Data<>("Étudiants Logés", gestionEtudiant.getNombreEtudiantsLogis()));

        chart.getData().add(series);
        return chart;
    }

    private double calculateOccupancyRate() {
        int total = gestionChambre.getAllChambres().size();
        if (total == 0) return 0;
        int occupied = gestionChambre.getNombreChambresOccupees();
        return (double) occupied / total * 100;
    }
}
