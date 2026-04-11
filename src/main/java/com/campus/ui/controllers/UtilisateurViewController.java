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

public class UtilisateurViewController {
    private GestionUtilisateur gestionUtilisateur;
    private TableView<Utilisateur> tableView;
    private Stage primaryStage;
    private AdminDashboardController parentController;

    public UtilisateurViewController(GestionUtilisateur gestionUtilisateur,
                                   Stage primaryStage, AdminDashboardController parentController) {
        this.gestionUtilisateur = gestionUtilisateur;
        this.primaryStage = primaryStage;
        this.parentController = parentController;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Gestion des Utilisateurs");
        stage.setScene(new Scene(createLayout(), 1000, 600));
        stage.show();
    }

    private VBox createLayout() {
        VBox root = new VBox(10);
        root.getStyleClass().add("content-panel");

        Label title = new Label("Gestion des Utilisateurs");
        title.getStyleClass().add("view-title");

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
        Button deleteBtn = new Button("Supprimer");
        Button toggleActiveBtn = new Button("Activer/Désactiver");

        addBtn.setOnAction(e -> showAddDialog());
        editBtn.setOnAction(e -> showEditDialog());
        deleteBtn.setOnAction(e -> deleteUtilisateur());
        toggleActiveBtn.setOnAction(e -> toggleActive());

        box.getChildren().addAll(addBtn, editBtn, toggleActiveBtn, deleteBtn);
        return box;
    }

    private void setupTable() {
        TableColumn<Utilisateur, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNomComplet()));

        TableColumn<Utilisateur, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));

        TableColumn<Utilisateur, String> roleCol = new TableColumn<>("Rôle");
        roleCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole().getDisplayName()));

        TableColumn<Utilisateur, String> actifCol = new TableColumn<>("Statut");
        actifCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
            cellData.getValue().isActif() ? "Actif" : "Inactif"
        ));

        tableView.getColumns().addAll(nomCol, emailCol, roleCol, actifCol);
        tableView.setPrefHeight(400);
    }

    private void refreshTable() {
        ObservableList<Utilisateur> data = FXCollections.observableArrayList(gestionUtilisateur.getAllUtilisateurs());
        tableView.setItems(data);
    }

    private void showAddDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Ajouter un Utilisateur");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 15;");

        TextField nomField = new TextField();
        nomField.setPromptText("Nom");

        TextField prenomField = new TextField();
        prenomField.setPromptText("Prénom");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");

        ComboBox<UserRole> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList(UserRole.values()));

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setOnAction(e -> {
            if (validateInput(nomField, prenomField, emailField, passwordField) && roleCombo.getValue() != null) {
                Utilisateur u = new Utilisateur(
                    "U" + System.currentTimeMillis(),
                    nomField.getText(),
                    prenomField.getText(),
                    emailField.getText(),
                    passwordField.getText(),
                    roleCombo.getValue()
                );
                gestionUtilisateur.addUtilisateur(u);
                refreshTable();
                dialog.close();
            }
        });

        content.getChildren().addAll(
            new Label("Nom:"), nomField,
            new Label("Prénom:"), prenomField,
            new Label("Email:"), emailField,
            new Label("Mot de passe:"), passwordField,
            new Label("Rôle:"), roleCombo,
            saveBtn
        );

        dialog.setScene(new Scene(content, 400, 400));
        dialog.show();
    }

    private void showEditDialog() {
        Utilisateur selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur");
            return;
        }

        Stage dialog = new Stage();
        dialog.setTitle("Modifier un Utilisateur");

        VBox content = new VBox(10);
        content.setStyle("-fx-padding: 15;");

        TextField nomField = new TextField(selected.getNom());
        TextField prenomField = new TextField(selected.getPrenom());
        TextField emailField = new TextField(selected.getEmail());

        ComboBox<UserRole> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList(UserRole.values()));
        roleCombo.setValue(selected.getRole());

        Button saveBtn = new Button("Enregistrer");
        saveBtn.setOnAction(e -> {
            selected.setNom(nomField.getText());
            selected.setPrenom(prenomField.getText());
            selected.setEmail(emailField.getText());
            selected.setRole(roleCombo.getValue());
            gestionUtilisateur.updateUtilisateur(selected);
            refreshTable();
            dialog.close();
        });

        content.getChildren().addAll(
            new Label("Nom:"), nomField,
            new Label("Prénom:"), prenomField,
            new Label("Email:"), emailField,
            new Label("Rôle:"), roleCombo,
            saveBtn
        );

        dialog.setScene(new Scene(content, 400, 350));
        dialog.show();
    }

    private void toggleActive() {
        Utilisateur selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur");
            return;
        }

        selected.setActif(!selected.isActif());
        gestionUtilisateur.updateUtilisateur(selected);
        refreshTable();
    }

    private void deleteUtilisateur() {
        Utilisateur selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un utilisateur");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'utilisateur?");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getNomComplet() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            gestionUtilisateur.deleteUtilisateur(selected.getId());
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
