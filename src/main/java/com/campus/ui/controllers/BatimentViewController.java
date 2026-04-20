package com.campus.ui.controllers;

import com.campus.managers.GestionBatiment;
import com.campus.models.Batiment;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BatimentViewController {
    private final GestionBatiment gestionBatiment;
    private TableView<Batiment> tableView;
    private TextField idField;
    private TextField nomField;
    private TextField adresseField;
    private Spinner<Integer> etagesSpinner;
    private TextArea descriptionArea;
    private Button saveButton;
    private Batiment selectedBatiment;

    public BatimentViewController(GestionBatiment gestionBatiment) {
        this.gestionBatiment = gestionBatiment;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Gestion des Bâtiments");
        Scene scene = new Scene(createView(), 1180, 720);
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

        Label title = new Label("Gestion des Bâtiments");
        title.getStyleClass().add("view-title");

        Label subtitle = new Label("Consultez et mettez à jour les résidences sans quitter l'écran principal.");
        subtitle.getStyleClass().add("panel-subtitle");

        HBox actions = new HBox(10);
        actions.getStyleClass().add("toolbar-row");
        Button newButton = new Button("Nouveau");
        newButton.getStyleClass().add("primary-button");
        newButton.setOnAction(e -> resetForm());
        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(e -> deleteSelectedBatiment());
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
        panel.setPrefWidth(360);

        Label title = new Label("Formulaire bâtiment");
        title.getStyleClass().add("dialog-title");

        Label subtitle = new Label("Sélectionnez un bâtiment dans la liste ou créez-en un nouveau.");
        subtitle.getStyleClass().add("dialog-subtitle");
        subtitle.setWrapText(true);

        idField = new TextField();
        idField.setPromptText("ID");

        nomField = new TextField();
        nomField.setPromptText("Nom");

        adresseField = new TextField();
        adresseField.setPromptText("Adresse");

        etagesSpinner = new Spinner<>(1, 20, 1);

        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Description");
        descriptionArea.setPrefRowCount(5);

        saveButton = new Button("Enregistrer");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setOnAction(e -> saveBatiment());

        Button cancelButton = new Button("Réinitialiser");
        cancelButton.getStyleClass().add("secondary-button");
        cancelButton.setMaxWidth(Double.MAX_VALUE);
        cancelButton.setOnAction(e -> resetForm());

        panel.getChildren().addAll(
                title, subtitle,
                new Label("Identifiant"), idField,
                new Label("Nom"), nomField,
                new Label("Adresse"), adresseField,
                new Label("Nombre d'étages"), etagesSpinner,
                new Label("Description"), descriptionArea,
                saveButton, cancelButton);

        ScrollPane scrollPane = new ScrollPane(panel);
        scrollPane.getStyleClass().add("editor-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
    }

    private void setupTable() {
        TableColumn<Batiment, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getId()));

        TableColumn<Batiment, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNom()));

        TableColumn<Batiment, String> adresseCol = new TableColumn<>("Adresse");
        adresseCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getAdresse()));

        TableColumn<Batiment, Integer> etagesCol = new TableColumn<>("Étages");
        etagesCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getEtages()));

        tableView.getColumns().setAll(idCol, nomCol, adresseCol, etagesCol);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> populateForm(newValue));
    }

    private void populateForm(Batiment batiment) {
        selectedBatiment = batiment;
        if (batiment == null) {
            return;
        }
        idField.setText(batiment.getId());
        idField.setDisable(true);
        nomField.setText(batiment.getNom());
        adresseField.setText(batiment.getAdresse());
        etagesSpinner.getValueFactory().setValue(batiment.getEtages());
        descriptionArea.setText(batiment.getDescription());
        saveButton.setText("Mettre à jour");
    }

    private void saveBatiment() {
        if (idField.getText().isBlank() || nomField.getText().isBlank() || adresseField.getText().isBlank()) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        if (selectedBatiment == null) {
            Batiment batiment = new Batiment(idField.getText().trim(), nomField.getText().trim(),
                    adresseField.getText().trim(), etagesSpinner.getValue());
            batiment.setDescription(descriptionArea.getText().trim());
            gestionBatiment.addBatiment(batiment);
        } else {
            selectedBatiment.setNom(nomField.getText().trim());
            selectedBatiment.setAdresse(adresseField.getText().trim());
            selectedBatiment.setEtages(etagesSpinner.getValue());
            selectedBatiment.setDescription(descriptionArea.getText().trim());
            gestionBatiment.updateBatiment(selectedBatiment);
        }

        refreshTable();
        resetForm();
    }

    private void deleteSelectedBatiment() {
        Batiment selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un bâtiment.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le bâtiment ?");
        confirm.setContentText(selected.getNom());
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            gestionBatiment.deleteBatiment(selected.getId());
            refreshTable();
            resetForm();
        }
    }

    private void refreshTable() {
        ObservableList<Batiment> data = FXCollections.observableArrayList(gestionBatiment.getAllBatiments());
        tableView.setItems(data);
    }

    private void resetForm() {
        selectedBatiment = null;
        idField.clear();
        idField.setDisable(false);
        nomField.clear();
        adresseField.clear();
        etagesSpinner.getValueFactory().setValue(1);
        descriptionArea.clear();
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
