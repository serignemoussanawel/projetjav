package com.campus.ui.controllers;

import com.campus.managers.*;
import com.campus.models.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    
    private GestionUtilisateur gestionUtilisateur;
    private Stage primaryStage;

    public LoginController(GestionUtilisateur gestionUtilisateur, Stage primaryStage) {
        this.gestionUtilisateur = gestionUtilisateur;
        this.primaryStage = primaryStage;
    }

    @FXML
    public void initialize() {
        // Setup UI
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
            new AdminDashboardController(gestionUtilisateur, gestionBatiment, gestionChambre, gestionEtudiant, primaryStage).show();
        } else if (user.getRole() == UserRole.CHEF_BATIMENT) {
            new ChefBatimentDashboardController(gestionUtilisateur, gestionBatiment, gestionChambre, gestionEtudiant, primaryStage).show();
        } else {
            new EtudiantDashboardController(gestionUtilisateur, gestionEtudiant, gestionChambre, gestionBatiment, primaryStage).show();
        }
    }

    public VBox createLoginView() {
        VBox root = new VBox(15);
        root.setPrefSize(400, 300);
        root.setStyle("-fx-padding: 30; -fx-alignment: CENTER;");

        Label titleLabel = new Label("Campus Room Manager");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Label emailLabel = new Label("Email:");
        emailField = new TextField();
        emailField.setPromptText("Entrez votre email");

        Label passwordLabel = new Label("Mot de passe:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Entrez votre mot de passe");

        loginButton = new Button("Connexion");
        loginButton.setPrefWidth(150);
        loginButton.setStyle("-fx-font-size: 14;");
        loginButton.setOnAction(e -> handleLogin());

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        root.getChildren().addAll(titleLabel, emailLabel, emailField, passwordLabel, 
                                   passwordField, loginButton, errorLabel);
        return root;
    }
}
