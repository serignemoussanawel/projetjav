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

public class EtudiantViewController {
    private GestionEtudiant gestionEtudiant;
    private GestionChambre gestionChambre;
    private TableView<Etudiant> tableView;

    public EtudiantViewController(GestionEtudiant gestionEtudiant, GestionChambre gestionChambre) {
        this.gestionEtudiant = gestionEtudiant;
        this.gestionChambre = gestionChambre;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Gestion des Étudiants");
        stage.setScene(new Scene(createLayout(), 1000, 650));
        stage.show();
    }

    private VBox createLayout() {
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 15;");

        Label title = new Label("Gestion des Étudiants");
        title.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        HBox buttonBox = createButtonBox();

        tableView = new TableView<>();
        setupTable();
        refreshTable();

        root.getChildren().addAll(title, buttonBox, tableView);
        return root;
    }

    private HBox createButtonBox() {
        HBox box = new HBox(10);

        Button addBtn = new Button("Ajouter");
        Button editBtn = new Button("Modifier");
        Button affecterBtn = new Button("Affecter une Chambre");
        Button libererBtn = new Button("Libérer la Chambre");
        Button deleteBtn = new Button("Supprimer");

        addBtn.setOnAction(e -> showAddDialog());
        editBtn.setOnAction(e -> showEditDialog());
        affecterBtn.setOnAction(e -> showAffecterDialog());
        libererBtn.setOnAction(e -> libererChambre());
        deleteBtn.setOnAction(e -> deleteEtudiant());

        box.getChildren().addAll(addBtn, editBtn, affecterBtn, libererBtn, deleteBtn);
        return box;
    }

    private void setupTable() {
        tableView.getStyleClass().add("dashboard-table");
        TableColumn<Etudiant, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNomComplet()));

        TableColumn<Etudiant, String> matriculeCol = new TableColumn<>("Matricule");
        matriculeCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNumeroMatricule()));

        TableColumn<Etudiant, String> specialiteCol = new TableColumn<>("Spécialité");
        specialiteCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSpecialite()));

        TableColumn<Etudiant, String> chambresCol = new TableColumn<>("Chambre");
        chambresCol.setCellValueFactory(cellData -> {
            Etudiant e = cellData.getValue();
            if (e.hasRoom()) {
                Chambre c = gestionChambre.getChambre(e.getChambreId());
                return new javafx.beans.property.SimpleStringProperty(c != null ? c.getCode() : "N/A");
            }
            return new javafx.beans.property.SimpleStringProperty("Non affectée");
        });

        tableView.getColumns().add(nomCol);
        tableView.getColumns().add(matriculeCol);
        tableView.getColumns().add(specialiteCol);
        tableView.getColumns().add(chambresCol);
        tableView.setPrefHeight(450);
    }

    private void refreshTable() {
        ObservableList<Etudiant> data = FXCollections.observableArrayList(gestionEtudiant.getAllEtudiants());
        tableView.setItems(data);
    }

    private void showAddDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Ajouter un Étudiant");

        VBox content = new VBox(10);
        content.getStyleClass().add("content-panel");

        TextField nomField = new TextField();
        nomField.setPromptText("Nom");

        TextField prenomField = new TextField();
        prenomField.setPromptText("Prénom");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField matriculeField = new TextField();
        matriculeField.setPromptText("Numéro Matricule");

        TextField specialiteField = new TextField();
        specialiteField.setPromptText("Spécialité");

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setOnAction(e -> {
            if (validateInput(nomField, prenomField, emailField, matriculeField, specialiteField)) {
                Etudiant etd = new Etudiant(
                        "E" + System.currentTimeMillis(),
                        nomField.getText(),
                        prenomField.getText(),
                        emailField.getText(),
                        matriculeField.getText(),
                        specialiteField.getText());
                gestionEtudiant.addEtudiant(etd);
                refreshTable();
                dialog.close();
            }
        });

        content.getChildren().addAll(
                new Label("Nom:"), nomField,
                new Label("Prénom:"), prenomField,
                new Label("Email:"), emailField,
                new Label("Matricule:"), matriculeField,
                new Label("Spécialité:"), specialiteField,
                saveBtn);

        dialog.setScene(new Scene(content, 400, 400));
        dialog.show();
    }

    private void showEditDialog() {
        Etudiant selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un étudiant");
            return;
        }

        Stage dialog = new Stage();
        dialog.setTitle("Modifier un Étudiant");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 15;");

        TextField nomField = new TextField(selected.getNom());
        TextField prenomField = new TextField(selected.getPrenom());
        TextField emailField = new TextField(selected.getEmail());
        TextField specialiteField = new TextField(selected.getSpecialite());

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setOnAction(e -> {
            selected.setNom(nomField.getText());
            selected.setPrenom(prenomField.getText());
            selected.setEmail(emailField.getText());
            selected.setSpecialite(specialiteField.getText());
            gestionEtudiant.updateEtudiant(selected);
            refreshTable();
            dialog.close();
        });

        content.getChildren().addAll(
                new Label("Nom:"), nomField,
                new Label("Prénom:"), prenomField,
                new Label("Email:"), emailField,
                new Label("Spécialité:"), specialiteField,
                saveBtn);

        dialog.setScene(new Scene(content, 400, 350));
        dialog.show();
    }

    private void showAffecterDialog() {
        Etudiant selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un étudiant");
            return;
        }

        Stage dialog = new Stage();
        dialog.setTitle("Affecter une Chambre");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 15;");

        Label etuLabel = new Label("Étudiant: " + selected.getNomComplet());
        etuLabel.setStyle("-fx-font-weight: bold;");

        ComboBox<Chambre> chambreCombo = new ComboBox<>();
        chambreCombo.setItems(FXCollections.observableArrayList(gestionChambre.getChambresLibres()));

        Button affecterBtn = new Button("Affecter");
        affecterBtn.setOnAction(e -> {
            if (chambreCombo.getValue() == null) {
                showAlert("Erreur", "Veuillez sélectionner une chambre");
                return;
            }

            Chambre chambre = chambreCombo.getValue();
            gestionEtudiant.affecterChambre(selected.getId(), chambre.getId(), java.time.LocalDate.now().toString());
            gestionChambre.affecterChambre(chambre.getId(), selected.getId());
            refreshTable();
            dialog.close();
        });

        content.getChildren().addAll(
                etuLabel,
                new Label("Chambre disponible:"),
                chambreCombo,
                affecterBtn);

        dialog.setScene(new Scene(content, 400, 250));
        dialog.show();
    }

    private void libererChambre() {
        Etudiant selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un étudiant");
            return;
        }

        if (!selected.hasRoom()) {
            showAlert("Info", "Cet étudiant n'a pas de chambre affectée");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Libérer la chambre?");
        confirm.setContentText("Êtes-vous sûr de vouloir libérer la chambre de " + selected.getNomComplet() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            String chambreId = selected.getChambreId();
            gestionEtudiant.libererChambre(selected.getId());
            gestionChambre.libererChambre(chambreId);
            refreshTable();
        }
    }

    private void deleteEtudiant() {
        Etudiant selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un étudiant");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'étudiant?");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getNomComplet() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            gestionEtudiant.deleteEtudiant(selected.getId());
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
