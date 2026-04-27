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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginController {
    private static final String[] LOGO_RESOURCE_CANDIDATES = {
            "/assets/logo.png",
            "/assets/logo-uadb.png",
            "/assets/logo-uadb.jpg",
            "/assets/logo-uadb.jpeg"
    };

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
            if (!(user instanceof ChefBatiment chefBatiment)
                    || chefBatiment.getBatimentId() == null
                    || gestionBatiment.getBatiment(chefBatiment.getBatimentId()) == null) {
                showErrorAlert("Compte incomplet",
                        "Ce chef de bâtiment n'est associé à aucun bâtiment. Veuillez contacter l'administrateur.");
                return;
            }
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

        Label title = new Label("Page d'accueil du système");
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
        Label illusText = new Label(
                "Connexion rapide, suivi clair et interface pensée pour l'administration du campus.");
        illusText.getStyleClass().add("login-illustration-text");
        illusText.setWrapText(true);
        illustrationCard.getChildren().addAll(illusTitle, illusText);
        illustration.getChildren().add(illustrationCard);

        heroContent.getChildren().addAll(badge, title, subtitle, highlights, illustration);
        visualPanel.getChildren().add(heroContent);
        return visualPanel;
    }

    private StackPane createLogoView() {
        StackPane logo = new StackPane();
        logo.getStyleClass().add("login-logo");
        logo.setPrefSize(220, 240);
        logo.setMinSize(220, 240);
        logo.setMaxSize(220, 240);

        ImageView logoImageView = createLogoImageView();
        if (logoImageView != null) {
            logo.getChildren().add(logoImageView);
            return logo;
        }

        Label topText = new Label("UNIVERSITE ALIOUNE DIOP");
        topText.getStyleClass().add("login-logo-title");

        Circle outerRing = new Circle(76);
        outerRing.setFill(Color.WHITE);
        outerRing.setStroke(Color.web("#c07d45"));
        outerRing.setStrokeWidth(4);

        Circle innerRing = new Circle(64);
        innerRing.setFill(Color.web("#f7fafc"));
        innerRing.setStroke(Color.web("#dfb07c"));
        innerRing.setStrokeWidth(2);

        Circle core = new Circle(51);
        core.setFill(Color.web("#1f5372"));
        core.setStroke(Color.web("#2d739a"));
        core.setStrokeWidth(2);

        Circle sun = new Circle(8);
        sun.setFill(Color.web("#f0c368"));
        sun.setTranslateY(-26);

        Polygon roof = new Polygon(
                -24.0, -2.0,
                0.0, -20.0,
                24.0, -2.0,
                18.0, 4.0,
                0.0, -9.0,
                -18.0, 4.0);
        roof.setFill(Color.web("#b7682e"));

        Rectangle tower = new Rectangle(18, 40);
        tower.setArcWidth(6);
        tower.setArcHeight(6);
        tower.setFill(Color.WHITE);
        tower.setTranslateY(16);

        Rectangle gate = new Rectangle(8, 18);
        gate.setArcWidth(4);
        gate.setArcHeight(4);
        gate.setFill(Color.web("#8fbad1"));
        gate.setTranslateY(26);

        Rectangle leftWing = new Rectangle(16, 22);
        leftWing.setArcWidth(6);
        leftWing.setArcHeight(6);
        leftWing.setFill(Color.web("#d8ebf5"));
        leftWing.setTranslateX(-20);
        leftWing.setTranslateY(22);

        Rectangle rightWing = new Rectangle(16, 22);
        rightWing.setArcWidth(6);
        rightWing.setArcHeight(6);
        rightWing.setFill(Color.web("#d8ebf5"));
        rightWing.setTranslateX(20);
        rightWing.setTranslateY(22);

        Label acronym = new Label("UADB");
        acronym.getStyleClass().add("login-logo-acronym");
        acronym.setTranslateY(-48);

        StackPane emblem = new StackPane(
                outerRing,
                innerRing,
                core,
                sun,
                roof,
                leftWing,
                rightWing,
                tower,
                gate,
                acronym);
        emblem.setPrefSize(176, 176);
        emblem.setMaxSize(176, 176);

        Label bottomText = new Label("DE BAMBEY");
        bottomText.getStyleClass().add("login-logo-subtitle");

        Label motto = new Label("Excellence • Ethique • Constance");
        motto.getStyleClass().add("login-logo-motto");

        VBox logoContent = new VBox(8, topText, emblem, bottomText, motto);
        logoContent.setAlignment(Pos.CENTER);
        logo.getChildren().add(logoContent);
        return logo;
    }

    private ImageView createLogoImageView() {
        for (String resourcePath : LOGO_RESOURCE_CANDIDATES) {
            var resource = getClass().getResource(resourcePath);
            if (resource == null) {
                continue;
            }

            ImageView imageView = new ImageView(new Image(resource.toExternalForm(), true));
            imageView.getStyleClass().add("login-logo-image");
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(170);
            imageView.setFitHeight(170);
            imageView.setSmooth(true);
            return imageView;
        }

        return null;
    }

    private VBox createFormPanel() {
        VBox formPanel = new VBox();
        formPanel.getStyleClass().add("login-form-panel");
        formPanel.setAlignment(Pos.CENTER);

        VBox card = new VBox(16);
        card.getStyleClass().add("login-card");
        card.setMaxWidth(380);

        StackPane logoContainer = new StackPane(createLogoView());
        logoContainer.getStyleClass().add("login-logo-shell");

        Label titleLabel = new Label("Connexion");
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

        HBox buttonContainer = new HBox(loginButton);
        buttonContainer.setAlignment(Pos.CENTER);

        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        card.getChildren().addAll(
                logoContainer,
                titleLabel,
                subtitleLabel,
                emailLabel, emailField,
                passwordLabel, passwordField,
                buttonContainer,
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

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(primaryStage);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
