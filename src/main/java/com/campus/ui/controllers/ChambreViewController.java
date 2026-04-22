package com.campus.ui.controllers;

import com.campus.managers.GestionBatiment;
import com.campus.managers.GestionChambre;
import com.campus.models.Batiment;
import com.campus.models.Chambre;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ChambreViewController {
    private final GestionBatiment gestionBatiment;
    private final GestionChambre gestionChambre;
    private TableView<Chambre> tableView;
    private TextField idField;
    private Spinner<Integer> numeroSpinner;
    private ComboBox<Batiment> batimentCombo;
    private Spinner<Integer> etageSpinner;
    private Spinner<Integer> capaciteSpinner;
    private ComboBox<String> typeCombo;
    private ComboBox<String> etatCombo;
    private Button saveButton;
    private Chambre selectedChambre;

    public ChambreViewController(GestionBatiment gestionBatiment, GestionChambre gestionChambre) {
        this.gestionBatiment = gestionBatiment;
        this.gestionChambre = gestionChambre;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Gestion des Chambres");
        Scene scene = new Scene(createView(), 1240, 720);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public HBox createView() {
        HBox root = new HBox(18);
        root.getStyleClass().add("split-view");

        VBox listPanel = createListPanel();
        ScrollPane formPanel = createFormPanel();
        HBox.setHgrow(listPanel, Priority.ALWAYS);
        root.getChildren().addAll(listPanel, formPanel);
        return root;
    }

    private VBox createListPanel() {
        VBox panel = new VBox(16);
        panel.getStyleClass().add("content-panel");
        HBox.setHgrow(panel, Priority.ALWAYS);

        Label title = new Label("Gestion des Chambres");
        title.getStyleClass().add("view-title");

        Label subtitle = new Label("Gardez la main sur les chambres et leur disponibilité dans une vue large.");
        subtitle.getStyleClass().add("panel-subtitle");

        HBox actions = new HBox(10);
        actions.getStyleClass().add("toolbar-row");
        Button newButton = new Button("Nouvelle");
        newButton.getStyleClass().add("primary-button");
        newButton.setOnAction(e -> resetForm());
        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(e -> deleteSelectedChambre());
        actions.getChildren().addAll(newButton, deleteButton);

        tableView = new TableView<>();
        tableView.getStyleClass().add("dashboard-table");
        setupTable();
        refreshTable();
        VBox.setVgrow(tableView, Priority.ALWAYS);

        panel.getChildren().addAll(title, subtitle, actions, tableView);
        return panel;
    }

    private ScrollPane createFormPanel() {
        VBox panel = new VBox(12);
        panel.getStyleClass().addAll("content-panel", "editor-panel");
        panel.setPrefWidth(380);

        Label title = new Label("Formulaire chambre");
        title.getStyleClass().add("dialog-title");
        Label subtitle = new Label("Sélectionnez une ligne pour modifier ses informations.");
        subtitle.getStyleClass().add("dialog-subtitle");
        subtitle.setWrapText(true);

        idField = new TextField();
        numeroSpinner = new Spinner<>(1, 999, 1);
        batimentCombo = new ComboBox<>(FXCollections.observableArrayList(gestionBatiment.getAllBatiments()));
        batimentCombo.setMaxWidth(Double.MAX_VALUE);
        etageSpinner = new Spinner<>(1, 20, 1);
        capaciteSpinner = new Spinner<>(1, 10, 1);
        typeCombo = new ComboBox<>(FXCollections.observableArrayList("Simple", "Double", "Suite"));
        typeCombo.setMaxWidth(Double.MAX_VALUE);
        etatCombo = new ComboBox<>(FXCollections.observableArrayList("Libre", "Occupée", "Maintenance"));
        etatCombo.setMaxWidth(Double.MAX_VALUE);

        saveButton = new Button("Enregistrer");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setOnAction(e -> saveChambre());

        Button resetButton = new Button("Réinitialiser");
        resetButton.getStyleClass().add("secondary-button");
        resetButton.setMaxWidth(Double.MAX_VALUE);
        resetButton.setOnAction(e -> resetForm());

        panel.getChildren().addAll(
                title, subtitle,
                new Label("Identifiant"), idField,
                new Label("Numéro"), numeroSpinner,
                new Label("Bâtiment"), batimentCombo,
                new Label("Étage"), etageSpinner,
                new Label("Capacité"), capaciteSpinner,
                new Label("Type"), typeCombo,
                new Label("État"), etatCombo,
                saveButton, resetButton);

        ScrollPane scrollPane = new ScrollPane(panel);
        scrollPane.getStyleClass().add("editor-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
    }

    private void setupTable() {
        TableColumn<Chambre, String> batimentCol = new TableColumn<>("Bâtiment");
        batimentCol.setCellValueFactory(cell -> {
            Batiment batiment = gestionBatiment.getBatiment(cell.getValue().getBatimentId());
            return new SimpleStringProperty(batiment != null ? batiment.getNom() : "N/A");
        });

        TableColumn<Chambre, Integer> etageCol = new TableColumn<>("Étage");
        etageCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getEtage()));

        TableColumn<Chambre, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType()));

        TableColumn<Chambre, String> etatCol = new TableColumn<>("État");
        etatCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEtat()));

        tableView.getColumns().setAll(List.of(batimentCol, etageCol, typeCol, etatCol));
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> populateForm(newValue));
    }

    private void populateForm(Chambre chambre) {
        selectedChambre = chambre;
        if (chambre == null) {
            return;
        }
        idField.setText(chambre.getId());
        idField.setDisable(true);
        numeroSpinner.getValueFactory().setValue(chambre.getNumero());
        batimentCombo.setValue(gestionBatiment.getBatiment(chambre.getBatimentId()));
        etageSpinner.getValueFactory().setValue(chambre.getEtage());
        capaciteSpinner.getValueFactory().setValue(chambre.getCapacite());
        typeCombo.setValue(chambre.getType());
        etatCombo.setValue(chambre.getEtat());
        saveButton.setText("Modifier");
    }

    private void saveChambre() {
        if (idField.getText().isBlank() || batimentCombo.getValue() == null
                || typeCombo.getValue() == null || etatCombo.getValue() == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        if (selectedChambre == null) {
            Chambre chambre = new Chambre(
                    idField.getText().trim(),
                    numeroSpinner.getValue(),
                    batimentCombo.getValue().getId(),
                    etageSpinner.getValue(),
                    capaciteSpinner.getValue(),
                    typeCombo.getValue());
            chambre.setEtat(etatCombo.getValue());
            gestionChambre.addChambre(chambre);
        } else {
            selectedChambre.setNumero(numeroSpinner.getValue());
            selectedChambre.setBatimentId(batimentCombo.getValue().getId());
            selectedChambre.setEtage(etageSpinner.getValue());
            selectedChambre.setCapacite(capaciteSpinner.getValue());
            selectedChambre.setType(typeCombo.getValue());
            selectedChambre.setEtat(etatCombo.getValue());
            gestionChambre.updateChambre(selectedChambre);
        }

        refreshTable();
        resetForm();
    }

    private void deleteSelectedChambre() {
        Chambre selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner une chambre.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la chambre ?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            gestionChambre.deleteChambre(selected.getId());
            refreshTable();
            resetForm();
        }
    }

    private void refreshTable() {
        ObservableList<Chambre> data = FXCollections.observableArrayList(gestionChambre.getAllChambres());
        tableView.setItems(data);
    }

    private void resetForm() {
        selectedChambre = null;
        idField.clear();
        idField.setDisable(false);
        numeroSpinner.getValueFactory().setValue(1);
        batimentCombo.setValue(null);
        etageSpinner.getValueFactory().setValue(1);
        capaciteSpinner.getValueFactory().setValue(1);
        typeCombo.setValue(null);
        etatCombo.setValue("Libre");
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
