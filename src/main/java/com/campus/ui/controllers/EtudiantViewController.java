package com.campus.ui.controllers;

import com.campus.managers.GestionChambre;
import com.campus.managers.GestionEtudiant;
import com.campus.models.Chambre;
import com.campus.models.Etudiant;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class EtudiantViewController {
    private final GestionEtudiant gestionEtudiant;
    private final GestionChambre gestionChambre;
    private TableView<Etudiant> tableView;
    private TextField idField;
    private TextField nomField;
    private TextField prenomField;
    private TextField emailField;
    private TextField matriculeField;
    private TextField specialiteField;
    private ComboBox<Chambre> chambreCombo;
    private Button saveButton;
    private Etudiant selectedEtudiant;

    public EtudiantViewController(GestionEtudiant gestionEtudiant, GestionChambre gestionChambre) {
        this.gestionEtudiant = gestionEtudiant;
        this.gestionChambre = gestionChambre;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Gestion des Étudiants");
        Scene scene = new Scene(createView(), 1240, 720);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public HBox createView() {
        HBox root = new HBox(18);
        root.getStyleClass().add("split-view");

        VBox listPanel = createListPanel();
        VBox formPanel = createFormPanel();
        HBox.setHgrow(listPanel, Priority.ALWAYS);

        root.getChildren().addAll(listPanel, formPanel);
        return root;
    }

    private VBox createListPanel() {
        VBox panel = new VBox(16);
        panel.getStyleClass().add("content-panel");
        HBox.setHgrow(panel, Priority.ALWAYS);

        Label title = new Label("Gestion des Étudiants");
        title.getStyleClass().add("view-title");

        Label subtitle = new Label("Gérez les profils étudiants et leurs affectations sans ouvrir de popup.");
        subtitle.getStyleClass().add("panel-subtitle");

        HBox actions = new HBox(10);
        actions.getStyleClass().add("toolbar-row");
        Button newButton = new Button("Nouveau");
        newButton.getStyleClass().add("primary-button");
        newButton.setOnAction(e -> resetForm());
        Button releaseButton = new Button("Libérer la chambre");
        releaseButton.getStyleClass().add("secondary-button");
        releaseButton.setOnAction(e -> libererChambre());
        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(e -> deleteSelectedEtudiant());
        actions.getChildren().addAll(newButton, releaseButton, deleteButton);

        tableView = new TableView<>();
        tableView.getStyleClass().add("dashboard-table");
        setupTable();
        refreshTable();
        VBox.setVgrow(tableView, Priority.ALWAYS);

        panel.getChildren().addAll(title, subtitle, actions, tableView);
        return panel;
    }

    private VBox createFormPanel() {
        VBox panel = new VBox(12);
        panel.getStyleClass().addAll("content-panel", "editor-panel");
        panel.setPrefWidth(380);

        Label title = new Label("Formulaire étudiant");
        title.getStyleClass().add("dialog-title");
        Label subtitle = new Label("Le formulaire permet aussi d'affecter immédiatement une chambre libre.");
        subtitle.getStyleClass().add("dialog-subtitle");
        subtitle.setWrapText(true);

        idField = new TextField();
        nomField = new TextField();
        prenomField = new TextField();
        emailField = new TextField();
        matriculeField = new TextField();
        specialiteField = new TextField();
        chambreCombo = new ComboBox<>();
        chambreCombo.setMaxWidth(Double.MAX_VALUE);

        saveButton = new Button("Enregistrer");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setOnAction(e -> saveEtudiant());

        Button resetButton = new Button("Réinitialiser");
        resetButton.getStyleClass().add("secondary-button");
        resetButton.setMaxWidth(Double.MAX_VALUE);
        resetButton.setOnAction(e -> resetForm());

        panel.getChildren().addAll(
                title, subtitle,
                new Label("Identifiant"), idField,
                new Label("Nom"), nomField,
                new Label("Prénom"), prenomField,
                new Label("Email"), emailField,
                new Label("Matricule"), matriculeField,
                new Label("Spécialité"), specialiteField,
                new Label("Chambre"), chambreCombo,
                saveButton, resetButton);
        refreshChambreChoices(null);
        return panel;
    }

    private void setupTable() {
        TableColumn<Etudiant, String> nomCol = new TableColumn<>("Nom complet");
        nomCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNomComplet()));

        TableColumn<Etudiant, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));

        TableColumn<Etudiant, String> matriculeCol = new TableColumn<>("Matricule");
        matriculeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNumeroMatricule()));

        TableColumn<Etudiant, String> chambreCol = new TableColumn<>("Chambre");
        chambreCol.setCellValueFactory(cell -> {
            Etudiant etudiant = cell.getValue();
            if (!etudiant.hasRoom()) {
                return new SimpleStringProperty("Non affectée");
            }
            Chambre chambre = gestionChambre.getChambre(etudiant.getChambreId());
            return new SimpleStringProperty(chambre != null ? chambre.getCode() : "N/A");
        });

        tableView.getColumns().setAll(nomCol, emailCol, matriculeCol, chambreCol);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> populateForm(newValue));
    }

    private void populateForm(Etudiant etudiant) {
        selectedEtudiant = etudiant;
        if (etudiant == null) {
            return;
        }
        idField.setText(etudiant.getId());
        idField.setDisable(true);
        nomField.setText(etudiant.getNom());
        prenomField.setText(etudiant.getPrenom());
        emailField.setText(etudiant.getEmail());
        matriculeField.setText(etudiant.getNumeroMatricule());
        specialiteField.setText(etudiant.getSpecialite());
        refreshChambreChoices(etudiant.getChambreId());
        if (etudiant.hasRoom()) {
            chambreCombo.setValue(gestionChambre.getChambre(etudiant.getChambreId()));
        } else {
            chambreCombo.setValue(null);
        }
        saveButton.setText("Mettre à jour");
    }

    private void saveEtudiant() {
        if (idField.getText().isBlank() || nomField.getText().isBlank() || prenomField.getText().isBlank()
                || emailField.getText().isBlank() || matriculeField.getText().isBlank() || specialiteField.getText().isBlank()) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        if (selectedEtudiant == null) {
            Etudiant etudiant = new Etudiant(
                    idField.getText().trim(),
                    nomField.getText().trim(),
                    prenomField.getText().trim(),
                    emailField.getText().trim(),
                    matriculeField.getText().trim(),
                    specialiteField.getText().trim());
            gestionEtudiant.addEtudiant(etudiant);
            selectedEtudiant = etudiant;
        } else {
            selectedEtudiant.setNom(nomField.getText().trim());
            selectedEtudiant.setPrenom(prenomField.getText().trim());
            selectedEtudiant.setEmail(emailField.getText().trim());
            selectedEtudiant.setNumeroMatricule(matriculeField.getText().trim());
            selectedEtudiant.setSpecialite(specialiteField.getText().trim());
            gestionEtudiant.updateEtudiant(selectedEtudiant);
        }

        updateRoomAssignment(selectedEtudiant, chambreCombo.getValue());
        refreshTable();
        resetForm();
    }

    private void updateRoomAssignment(Etudiant etudiant, Chambre nouvelleChambre) {
        if (etudiant.hasRoom()) {
            String ancienneChambreId = etudiant.getChambreId();
            if (nouvelleChambre == null || !ancienneChambreId.equals(nouvelleChambre.getId())) {
                gestionChambre.libererChambre(ancienneChambreId);
                gestionEtudiant.libererChambre(etudiant.getId());
            }
        }

        if (nouvelleChambre != null && !nouvelleChambre.getId().equals(etudiant.getChambreId())) {
            gestionEtudiant.affecterChambre(etudiant.getId(), nouvelleChambre.getId(), LocalDate.now().toString());
            gestionChambre.affecterChambre(nouvelleChambre.getId(), etudiant.getId());
        }
    }

    private void libererChambre() {
        Etudiant selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null || !selected.hasRoom()) {
            showAlert("Erreur", "Veuillez sélectionner un étudiant ayant déjà une chambre.");
            return;
        }
        gestionChambre.libererChambre(selected.getChambreId());
        gestionEtudiant.libererChambre(selected.getId());
        refreshTable();
        populateForm(selected);
    }

    private void deleteSelectedEtudiant() {
        Etudiant selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un étudiant.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'étudiant ?");
        confirm.setContentText(selected.getNomComplet());
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (selected.hasRoom()) {
                gestionChambre.libererChambre(selected.getChambreId());
            }
            gestionEtudiant.deleteEtudiant(selected.getId());
            refreshTable();
            resetForm();
        }
    }

    private void refreshTable() {
        ObservableList<Etudiant> data = FXCollections.observableArrayList(gestionEtudiant.getAllEtudiants());
        tableView.setItems(data);
    }

    private void refreshChambreChoices(String chambreIdActuelle) {
        ObservableList<Chambre> choices = FXCollections.observableArrayList(gestionChambre.getChambresLibres());
        if (chambreIdActuelle != null) {
            Chambre chambreActuelle = gestionChambre.getChambre(chambreIdActuelle);
            if (chambreActuelle != null && !choices.contains(chambreActuelle)) {
                choices.add(chambreActuelle);
            }
        }
        chambreCombo.setItems(choices);
    }

    private void resetForm() {
        selectedEtudiant = null;
        idField.clear();
        idField.setDisable(false);
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        matriculeField.clear();
        specialiteField.clear();
        chambreCombo.setValue(null);
        refreshChambreChoices(null);
        saveButton.setText("Enregistrer");
        if (tableView != null) {
            tableView.getSelectionModel().clearSelection();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
