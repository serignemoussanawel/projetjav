package com.campus.ui.controllers;

import com.campus.managers.*;
import com.campus.models.*;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController {

    private GestionUtilisateur gestionUtilisateur;
    private Stage primaryStage;
    private TextField emailField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label errorLabel;

    public LoginController(GestionUtilisateur gestionUtilisateur, Stage primaryStage) {
        this.gestionUtilisateur = gestionUtilisateur;
        this.primaryStage = primaryStage;
    }

    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs");
            return;
        }

        Utilisateur user = gestionUtilisateur.authentifier(email, password);
        if (user != null) {
            loadMainWindow(user);
        } else {
            errorLabel.setText("Identifiants invalides");
            passwordField.clear();
        }
    }

    private void loadMainWindow(Utilisateur user) {
        GestionBatiment gestionBatiment = new GestionBatiment();
        GestionChambre gestionChambre = new GestionChambre();
        GestionEtudiant gestionEtudiant = new GestionEtudiant();

        if (user.getRole() == UserRole.ADMIN) {
            new AdminDashboardController(gestionUtilisateur, gestionBatiment, gestionChambre, gestionEtudiant,
                    primaryStage).show();
        } else if (user.getRole() == UserRole.CHEF_BATIMENT) {
            new ChefBatimentDashboardController(gestionUtilisateur, gestionBatiment, gestionChambre, gestionEtudiant,
                    primaryStage).show();
        } else {
            new EtudiantDashboardController(gestionUtilisateur, gestionEtudiant, gestionChambre, gestionBatiment,
                    primaryStage).show();
        }
    }

    public VBox createLoginView() {

        VBox root = new VBox(15);
        root.setPrefSize(400, 300);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("login-root");

        Label titleLabel = new Label("Campus Room Manager");
        titleLabel.getStyleClass().add("login-title");

        Label emailLabel = new Label("Email:");
        emailLabel.getStyleClass().add("login-label");

        emailField = new TextField();
        emailField.setPromptText("Entrez votre email");
        emailField.getStyleClass().add("form-field");

        Label passwordLabel = new Label("Mot de passe:");
        passwordLabel.getStyleClass().add("login-label");

        passwordField = new PasswordField();
        passwordField.setPromptText("Entrez votre mot de passe");
        passwordField.getStyleClass().add("form-field");

        loginButton = new Button("Connexion");
        loginButton.setPrefWidth(150);
        loginButton.getStyleClass().add("login-button");
        loginButton.setOnAction(e -> handleLogin());

        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        root.getChildren().addAll(
                titleLabel,
                emailLabel, emailField,
                passwordLabel, passwordField,
                loginButton,
                errorLabel);

        return root;
    }
}
