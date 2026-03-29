package com.campus.ui.controllers;

import com.campus.managers.*;
import com.campus.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChambreViewController {
    private GestionBatiment gestionBatiment;
    private GestionChambre gestionChambre;
    private TableView<Chambre> tableView;
    private Stage primaryStage;
    private AdminDashboardController parentController;

    public ChambreViewController(GestionBatiment gestionBatiment, GestionChambre gestionChambre,
                               Stage primaryStage, AdminDashboardController parentController) {
        this.gestionBatiment = gestionBatiment;
        this.gestionChambre = gestionChambre;
        this.primaryStage = primaryStage;
        this.parentController = parentController;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Gestion des Chambres");
        stage.setScene(new Scene(createLayout(), 1000, 650));
        stage.show();
    }

    private VBox createLayout() {
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 15;");

        Label title = new Label("Gestion des Chambres");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        HBox filterBox = createFilterBox();
        HBox buttonBox = createButtonBox();

        tableView = new TableView<>();
        setupTable();
        refreshTable();

        root.getChildren().addAll(title, filterBox, buttonBox, tableView);
        return root;
    }

    private HBox createFilterBox() {
        HBox box = new HBox(10);
        box.setStyle("-fx-padding: 10;");

        Label batimentLabel = new Label("Bâtiment:");
        ComboBox<Batiment> batimentCombo = new ComboBox<>();
        batimentCombo.setItems(FXCollections.observableArrayList(gestionBatiment.getAllBatiments()));

        batimentCombo.setOnAction(e -> {
            if (batimentCombo.getValue() != null) {
                refreshTableForBatiment(batimentCombo.getValue().getId());
            } else {
                refreshTable();
            }
        });

        box.getChildren().addAll(batimentLabel, batimentCombo);
        return box;
    }

    private HBox createButtonBox() {
        HBox box = new HBox(10);
        
        Button addBtn = new Button("Ajouter");
        Button editBtn = new Button("Modifier");
        Button deleteBtn = new Button("Supprimer");

        addBtn.setOnAction(e -> showAddDialog());
        editBtn.setOnAction(e -> showEditDialog());
        deleteBtn.setOnAction(e -> deleteChambre());

        box.getChildren().addAll(addBtn, editBtn, deleteBtn);
        return box;
    }

    private void setupTable() {
        TableColumn<Chambre, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCode()));

        TableColumn<Chambre, Integer> numeroCol = new TableColumn<>("Numéro");
        numeroCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getNumero()));

        TableColumn<Chambre, String> batimentCol = new TableColumn<>("Bâtiment");
        batimentCol.setCellValueFactory(cellData -> {
            Batiment b = gestionBatiment.getBatiment(cellData.getValue().getBatimentId());
            return new javafx.beans.property.SimpleStringProperty(b != null ? b.getNom() : "N/A");
        });

        TableColumn<Chambre, Integer> etageCol = new TableColumn<>("Étage");
        etageCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getEtage()));

        TableColumn<Chambre, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));

        TableColumn<Chambre, String> etatCol = new TableColumn<>("État");
        etatCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEtat()));

        tableView.getColumns().addAll(codeCol, numeroCol, batimentCol, etageCol, typeCol, etatCol);
        tableView.setPrefHeight(450);
    }

    private void refreshTable() {
        ObservableList<Chambre> data = FXCollections.observableArrayList(gestionChambre.getAllChambres());
        tableView.setItems(data);
    }

    private void refreshTableForBatiment(String batimentId) {
        ObservableList<Chambre> data = FXCollections.observableArrayList(
            gestionChambre.getChambresByBatiment(batimentId)
        );
        tableView.setItems(data);
    }

    private void showAddDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Ajouter une Chambre");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 15;");

        TextField codeField = new TextField();
        codeField.setPromptText("Code (ex: A-101)");

        Spinner<Integer> numeroSpinner = new Spinner<>(100, 999, 100);
        
        ComboBox<Batiment> batimentCombo = new ComboBox<>();
        batimentCombo.setItems(FXCollections.observableArrayList(gestionBatiment.getAllBatiments()));

        Spinner<Integer> etageSpinner = new Spinner<>(1, 10, 1);
        
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.setItems(FXCollections.observableArrayList("Simple", "Double", "Suite"));

        Spinner<Integer> capaciteSpinner = new Spinner<>(1, 5, 1);

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setOnAction(e -> {
            if (codeField.getText().isEmpty() || batimentCombo.getValue() == null || typeCombo.getValue() == null) {
                showAlert("Erreur", "Veuillez remplir tous les champs");
                return;
            }

            Chambre c = new Chambre(
                "C" + System.currentTimeMillis(),
                codeField.getText(),
                numeroSpinner.getValue(),
                batimentCombo.getValue().getId(),
                etageSpinner.getValue(),
                capaciteSpinner.getValue(),
                typeCombo.getValue()
            );
            gestionChambre.addChambre(c);
            refreshTable();
            dialog.close();
        });

        content.getChildren().addAll(
            new Label("Code:"), codeField,
            new Label("Numéro:"), numeroSpinner,
            new Label("Bâtiment:"), batimentCombo,
            new Label("Étage:"), etageSpinner,
            new Label("Type:"), typeCombo,
            new Label("Capacité:"), capaciteSpinner,
            saveBtn
        );

        dialog.setScene(new Scene(content, 400, 450));
        dialog.show();
    }

    private void showEditDialog() {
        Chambre selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner une chambre");
            return;
        }

        Stage dialog = new Stage();
        dialog.setTitle("Modifier une Chambre");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 15;");

        TextField codeField = new TextField(selected.getCode());
        Spinner<Integer> numeroSpinner = new Spinner<>(100, 999, selected.getNumero());
        Spinner<Integer> etageSpinner = new Spinner<>(1, 10, selected.getEtage());
        
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.setItems(FXCollections.observableArrayList("Simple", "Double", "Suite"));
        typeCombo.setValue(selected.getType());

        Spinner<Integer> capaciteSpinner = new Spinner<>(1, 5, selected.getCapacite());

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setOnAction(e -> {
            selected.setCode(codeField.getText());
            selected.setNumero(numeroSpinner.getValue());
            selected.setEtage(etageSpinner.getValue());
            selected.setType(typeCombo.getValue());
            selected.setCapacite(capaciteSpinner.getValue());
            gestionChambre.updateChambre(selected);
            refreshTable();
            dialog.close();
        });

        content.getChildren().addAll(
            new Label("Code:"), codeField,
            new Label("Numéro:"), numeroSpinner,
            new Label("Étage:"), etageSpinner,
            new Label("Type:"), typeCombo,
            new Label("Capacité:"), capaciteSpinner,
            saveBtn
        );

        dialog.setScene(new Scene(content, 400, 400));
        dialog.show();
    }

    private void deleteChambre() {
        Chambre selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner une chambre");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la chambre?");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getCode() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            gestionChambre.deleteChambre(selected.getId());
            refreshTable();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
