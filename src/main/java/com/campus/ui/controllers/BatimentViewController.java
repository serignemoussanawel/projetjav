package com.campus.ui.controllers;

import com.campus.managers.*;
import com.campus.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BatimentViewController {
    private GestionBatiment gestionBatiment;
    private GestionChambre gestionChambre;
    private TableView<Batiment> tableView;
    private Stage primaryStage;
    private AdminDashboardController parentController;

    public BatimentViewController(GestionBatiment gestionBatiment, GestionChambre gestionChambre,
                                Stage primaryStage, AdminDashboardController parentController) {
        this.gestionBatiment = gestionBatiment;
        this.gestionChambre = gestionChambre;
        this.primaryStage = primaryStage;
        this.parentController = parentController;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Gestion des Bâtiments");
        stage.setScene(new Scene(createLayout(), 900, 600));
        stage.show();
    }

    private VBox createLayout() {
        VBox root = new VBox(10);
        root.getStyleClass().add("content-panel");

        // Header
        Label title = new Label("Gestion des Bâtiments");
        title.getStyleClass().add("view-title");

        // Buttons
        HBox buttonBox = new HBox(10);
        Button addBtn = new Button("Ajouter");
        Button editBtn = new Button("Modifier");
        Button deleteBtn = new Button("Supprimer");

        addBtn.setOnAction(e -> showAddDialog());
        editBtn.setOnAction(e -> showEditDialog());
        deleteBtn.setOnAction(e -> deleteBatiment());

        buttonBox.getChildren().addAll(addBtn, editBtn, deleteBtn);

        // Table
        tableView = new TableView<>();
        setupTable();
        refreshTable();

        root.getChildren().addAll(title, buttonBox, tableView);
        return root;
    }

    private void setupTable() {
        tableView.getStyleClass().add("dashboard-table");
        TableColumn<Batiment, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));

        TableColumn<Batiment, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom()));

        TableColumn<Batiment, String> adresseCol = new TableColumn<>("Adresse");
        adresseCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAdresse()));

        TableColumn<Batiment, Integer> etagesCol = new TableColumn<>("Étages");
        etagesCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getEtages()));

        tableView.getColumns().addAll(idCol, nomCol, adresseCol, etagesCol);
        tableView.setPrefHeight(400);
    }

    private void refreshTable() {
        ObservableList<Batiment> data = FXCollections.observableArrayList(gestionBatiment.getAllBatiments());
        tableView.setItems(data);
    }

    private void showAddDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Ajouter un Bâtiment");

        VBox content = new VBox(10);
        content.getStyleClass().add("content-panel");

        TextField idField = new TextField();
        idField.setPromptText("ID");

        TextField nomField = new TextField();
        nomField.setPromptText("Nom");

        TextField adresseField = new TextField();
        adresseField.setPromptText("Adresse");

        Spinner<Integer> etagsSpinner = new Spinner<>(1, 10, 1);
        etagsSpinner.setPrefWidth(150);

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setOnAction(e -> {
            if (validateInput(idField, nomField, adresseField)) {
                Batiment b = new Batiment(idField.getText(), nomField.getText(), 
                                         adresseField.getText(), etagsSpinner.getValue());
                gestionBatiment.addBatiment(b);
                refreshTable();
                dialog.close();
            }
        });

        content.getChildren().addAll(
            new Label("ID:"), idField,
            new Label("Nom:"), nomField,
            new Label("Adresse:"), adresseField,
            new Label("Nombre d'étages:"), etagsSpinner,
            saveBtn
        );

        dialog.setScene(new Scene(content, 400, 350));
        dialog.show();
    }

    private void showEditDialog() {
        Batiment selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un bâtiment");
            return;
        }

        Stage dialog = new Stage();
        dialog.setTitle("Modifier un Bâtiment");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 15;");

        TextField nomField = new TextField(selected.getNom());
        TextField adresseField = new TextField(selected.getAdresse());
        Spinner<Integer> etagsSpinner = new Spinner<>(1, 10, selected.getEtages());

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setOnAction(e -> {
            selected.setNom(nomField.getText());
            selected.setAdresse(adresseField.getText());
            selected.setEtages(etagsSpinner.getValue());
            gestionBatiment.updateBatiment(selected);
            refreshTable();
            dialog.close();
        });

        content.getChildren().addAll(
            new Label("Nom:"), nomField,
            new Label("Adresse:"), adresseField,
            new Label("Nombre d'étages:"), etagsSpinner,
            saveBtn
        );

        dialog.setScene(new Scene(content, 400, 300));
        dialog.show();
    }

    private void deleteBatiment() {
        Batiment selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un bâtiment");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le bâtiment?");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getNom() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            gestionBatiment.deleteBatiment(selected.getId());
            refreshTable();
        }
    }

    private boolean validateInput(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs");
                return false;
            }
        }
        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
