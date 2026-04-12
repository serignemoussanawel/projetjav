package com.campus.ui.controllers;

import com.campus.managers.*;
import com.campus.models.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
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

    public HBox createLoginView() {
        HBox root = new HBox();
        root.setPrefSize(1100, 680);
        root.getStyleClass().add("login-root");

        StackPane visualPanel = createVisualPanel();
        VBox formPanel = createFormPanel();

        HBox.setHgrow(visualPanel, Priority.ALWAYS);
        HBox.setHgrow(formPanel, Priority.ALWAYS);
        root.getChildren().addAll(visualPanel, formPanel);
        return root;
    }

    private StackPane createVisualPanel() {
        StackPane visualPanel = new StackPane();
        visualPanel.getStyleClass().add("login-visual-panel");
        visualPanel.setPrefWidth(560);

        VBox heroContent = new VBox(18);
        heroContent.setPadding(new Insets(36));
        heroContent.setMaxWidth(420);
        heroContent.setAlignment(Pos.CENTER_LEFT);

        Label badge = new Label("Gestion intelligente du campus");
        badge.getStyleClass().add("login-badge");

        Label title = new Label("Un espace moderne pour piloter bâtiments, chambres et utilisateurs.");
        title.getStyleClass().add("login-hero-title");
        title.setWrapText(true);

        Label subtitle = new Label(
                "Suivez l'occupation, accédez rapidement aux tableaux de bord et gardez une vue claire sur toute la résidence.");
        subtitle.getStyleClass().add("login-hero-subtitle");
        subtitle.setWrapText(true);

        HBox highlights = new HBox(12,
                createHighlightCard("Bâtiments", "3 blocs suivis"),
                createHighlightCard("Chambres", "10 espaces prêts"),
                createHighlightCard("Accès", "Profils sécurisés"));
        highlights.getStyleClass().add("login-highlight-row");

        StackPane illustration = new StackPane();
        illustration.getStyleClass().add("login-illustration");
        VBox illustrationCard = new VBox(10);
        illustrationCard.getStyleClass().add("login-illustration-card");
        Label illusTitle = new Label("Vue résidence");
        illusTitle.getStyleClass().add("login-illustration-title");
        Label illusText = new Label("Connexion rapide, suivi clair et interface pensée pour l'administration du campus.");
        illusText.getStyleClass().add("login-illustration-text");
        illusText.setWrapText(true);
        illustrationCard.getChildren().addAll(illusTitle, illusText);
        illustration.getChildren().add(illustrationCard);

        heroContent.getChildren().addAll(badge, title, subtitle, highlights, illustration);
        visualPanel.getChildren().add(heroContent);
        return visualPanel;
    }

    private VBox createFormPanel() {
        VBox formPanel = new VBox();
        formPanel.getStyleClass().add("login-form-panel");
        formPanel.setAlignment(Pos.CENTER);

        VBox card = new VBox(16);
        card.getStyleClass().add("login-card");
        card.setMaxWidth(380);

        Label titleLabel = new Label("Campus Room Manager");
        titleLabel.getStyleClass().add("login-title");

        Label subtitleLabel = new Label("Connectez-vous pour accéder à votre espace.");
        subtitleLabel.getStyleClass().add("login-subtitle");

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

        card.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                emailLabel, emailField,
                passwordLabel, passwordField,
                loginButton,
                errorLabel);

        formPanel.getChildren().add(card);
        return formPanel;
    }

    private Node createHighlightCard(String title, String value) {
        VBox card = new VBox(4);
        card.getStyleClass().add("login-highlight-card");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("login-highlight-title");
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("login-highlight-value");
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
}
