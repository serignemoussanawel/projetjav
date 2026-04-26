package com.campus.ui.controllers;

import com.campus.managers.GestionBatiment;
import com.campus.managers.GestionUtilisateur;
import com.campus.models.Admin;
import com.campus.models.Batiment;
import com.campus.models.ChefBatiment;
import com.campus.models.UserRole;
import com.campus.models.Utilisateur;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class UtilisateurViewController {
    private final GestionUtilisateur gestionUtilisateur;
    private final GestionBatiment gestionBatiment;
    private TableView<Utilisateur> tableView;
    private TextField idField;
    private TextField nomField;
    private TextField prenomField;
    private TextField emailField;
    private PasswordField passwordField;
    private ComboBox<UserRole> roleCombo;
    private ComboBox<Batiment> batimentCombo;
    private Button saveButton;
    private Utilisateur selectedUtilisateur;

    public UtilisateurViewController(GestionUtilisateur gestionUtilisateur) {
        this.gestionUtilisateur = gestionUtilisateur;
        this.gestionBatiment = new GestionBatiment();
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Gestion des Utilisateurs");
        Scene scene = new Scene(createView(), 1200, 720);
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

        Label title = new Label("Gestion des Utilisateurs");
        title.getStyleClass().add("view-title");

        Label subtitle = new Label("Créez, modifiez ou activez les comptes depuis un seul écran.");
        subtitle.getStyleClass().add("panel-subtitle");

        HBox actions = new HBox(10);
        actions.getStyleClass().add("toolbar-row");
        Button newButton = new Button("Nouveau");
        newButton.getStyleClass().add("primary-button");
        newButton.setOnAction(e -> resetForm());
        Button toggleButton = new Button("Activer/Désactiver");
        toggleButton.getStyleClass().add("success-button");
        toggleButton.setOnAction(e -> toggleActive());
        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(e -> deleteSelectedUtilisateur());
        actions.getChildren().addAll(newButton, toggleButton, deleteButton);

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

        Label title = new Label("Formulaire utilisateur");
        title.getStyleClass().add("dialog-title");

        Label subtitle = new Label("Le formulaire se remplit automatiquement lorsque vous sélectionnez une ligne.");
        subtitle.getStyleClass().add("dialog-subtitle");
        subtitle.setWrapText(true);

        idField = new TextField();
        idField.setPromptText("ID");

        nomField = new TextField();
        nomField.setPromptText("Nom");

        prenomField = new TextField();
        prenomField.setPromptText("Prénom");

        emailField = new TextField();
        emailField.setPromptText("Email");

        passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");

        roleCombo = new ComboBox<>(FXCollections.observableArrayList(UserRole.values()));
        roleCombo.setMaxWidth(Double.MAX_VALUE);
        roleCombo.valueProperty().addListener((obs, oldValue, newValue) -> updateBatimentFieldState(newValue));

        batimentCombo = new ComboBox<>(FXCollections.observableArrayList(gestionBatiment.getAllBatiments()));
        batimentCombo.setMaxWidth(Double.MAX_VALUE);
        batimentCombo.setPromptText("Sélectionner un bâtiment");

        saveButton = new Button("Enregistrer");
        saveButton.getStyleClass().add("primary-button");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setOnAction(e -> saveUtilisateur());

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
                new Label("Mot de passe"), passwordField,
                new Label("Rôle"), roleCombo,
                new Label("Bâtiment"), batimentCombo,
                saveButton, resetButton);

        ScrollPane scrollPane = new ScrollPane(panel);
        scrollPane.getStyleClass().add("editor-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        return scrollPane;
    }

    private void setupTable() {
        TableColumn<Utilisateur, String> nomCol = new TableColumn<>("Nom complet");
        nomCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNomComplet()));

        TableColumn<Utilisateur, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));

        TableColumn<Utilisateur, String> roleCol = new TableColumn<>("Rôle");
        roleCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRole().getDisplayName()));

        TableColumn<Utilisateur, String> actifCol = new TableColumn<>("Statut");
        actifCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isActif() ? "Actif" : "Inactif"));

        tableView.getColumns().setAll(List.of(nomCol, emailCol, roleCol, actifCol));
        tableView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> populateForm(newValue));
    }

    private void populateForm(Utilisateur utilisateur) {
        selectedUtilisateur = utilisateur;
        if (utilisateur == null) {
            return;
        }
        idField.setText(utilisateur.getId());
        idField.setDisable(true);
        nomField.setText(utilisateur.getNom());
        prenomField.setText(utilisateur.getPrenom());
        emailField.setText(utilisateur.getEmail());
        passwordField.setText(utilisateur.getMotDePasse());
        roleCombo.setValue(utilisateur.getRole());
        String batimentId = null;
        if (utilisateur instanceof ChefBatiment) {
            batimentId = ((ChefBatiment) utilisateur).getBatimentId();
        }
        batimentCombo.setValue(gestionBatiment.getBatiment(batimentId));
        updateBatimentFieldState(utilisateur.getRole());
        saveButton.setText("Modifier");
    }

    private void saveUtilisateur() {
        if (idField.getText().isBlank() || nomField.getText().isBlank() || prenomField.getText().isBlank()
                || emailField.getText().isBlank() || passwordField.getText().isBlank()
                || roleCombo.getValue() == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        if (roleCombo.getValue() == UserRole.CHEF_BATIMENT && batimentCombo.getValue() == null) {
            showAlert("Erreur", "Veuillez associer un bâtiment au chef de bâtiment.");
            return;
        }

        String batimentId = roleCombo.getValue() == UserRole.CHEF_BATIMENT
                ? batimentCombo.getValue().getId()
                : null;

        Utilisateur utilisateurToSave;
        if (selectedUtilisateur == null) {
            switch (roleCombo.getValue()) {
                case ADMIN:
                    utilisateurToSave = new Admin(
                            idField.getText().trim(),
                            nomField.getText().trim(),
                            prenomField.getText().trim(),
                            emailField.getText().trim(),
                            passwordField.getText());
                    break;
                case CHEF_BATIMENT:
                    utilisateurToSave = new ChefBatiment(
                            idField.getText().trim(),
                            nomField.getText().trim(),
                            prenomField.getText().trim(),
                            emailField.getText().trim(),
                            passwordField.getText(),
                            batimentId);
                    break;
                case ETUDIANT:
                    utilisateurToSave = new Utilisateur(
                            idField.getText().trim(),
                            nomField.getText().trim(),
                            prenomField.getText().trim(),
                            emailField.getText().trim(),
                            passwordField.getText(),
                            roleCombo.getValue());
                    break;
                default:
                    utilisateurToSave = new Utilisateur(
                            idField.getText().trim(),
                            nomField.getText().trim(),
                            prenomField.getText().trim(),
                            emailField.getText().trim(),
                            passwordField.getText(),
                            roleCombo.getValue());
                    break;
            }
        } else {
            switch (roleCombo.getValue()) {
                case ADMIN:
                    utilisateurToSave = new Admin(
                            selectedUtilisateur.getId(),
                            nomField.getText().trim(),
                            prenomField.getText().trim(),
                            emailField.getText().trim(),
                            passwordField.getText());
                    break;
                case CHEF_BATIMENT:
                    utilisateurToSave = new ChefBatiment(
                            selectedUtilisateur.getId(),
                            nomField.getText().trim(),
                            prenomField.getText().trim(),
                            emailField.getText().trim(),
                            passwordField.getText(),
                            batimentId);
                    break;
                case ETUDIANT:
                    utilisateurToSave = new Utilisateur(
                            selectedUtilisateur.getId(),
                            nomField.getText().trim(),
                            prenomField.getText().trim(),
                            emailField.getText().trim(),
                            passwordField.getText(),
                            roleCombo.getValue());
                    break;
                default:
                    utilisateurToSave = new Utilisateur(
                            selectedUtilisateur.getId(),
                            nomField.getText().trim(),
                            prenomField.getText().trim(),
                            emailField.getText().trim(),
                            passwordField.getText(),
                            roleCombo.getValue());
                    break;
            }
            utilisateurToSave.setActif(selectedUtilisateur.isActif());
        }

        try {
            if (selectedUtilisateur == null) {
                gestionUtilisateur.addUtilisateur(utilisateurToSave);
            } else {
                gestionUtilisateur.updateUtilisateur(utilisateurToSave);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            showAlert("Erreur", e.getMessage());
            return;
        }

        refreshTable();
        resetForm();
    }

    private void toggleActive() {
        Utilisateur selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur.");
            return;
        }
        selected.setActif(!selected.isActif());
        try {
            gestionUtilisateur.updateUtilisateur(selected);
            refreshTable();
            populateForm(selected);
        } catch (IllegalArgumentException | IllegalStateException e) {
            selected.setActif(!selected.isActif());
            showAlert("Erreur", e.getMessage());
        }
    }

    private void deleteSelectedUtilisateur() {
        Utilisateur selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'utilisateur ?");
        confirm.setContentText(selected.getNomComplet());
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            gestionUtilisateur.deleteUtilisateur(selected.getId());
            refreshTable();
            resetForm();
        }
    }

    private void refreshTable() {
        ObservableList<Utilisateur> data = FXCollections.observableArrayList(gestionUtilisateur.getAllUtilisateurs());
        tableView.setItems(data);
    }

    private void resetForm() {
        selectedUtilisateur = null;
        idField.clear();
        idField.setDisable(false);
        nomField.clear();
        prenomField.clear();
        emailField.clear();
        passwordField.clear();
        roleCombo.setValue(null);
        batimentCombo.setValue(null);
        updateBatimentFieldState(null);
        saveButton.setText("Enregistrer");
        if (tableView != null) {
            tableView.getSelectionModel().clearSelection();
        }
    }

    private void updateBatimentFieldState(UserRole role) {
        boolean isChefBatiment = role == UserRole.CHEF_BATIMENT;
        batimentCombo.setDisable(!isChefBatiment);
        if (!isChefBatiment) {
            batimentCombo.setValue(null);
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
