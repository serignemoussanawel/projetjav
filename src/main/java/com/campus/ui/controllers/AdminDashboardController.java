package com.campus.ui.controllers;

import com.campus.managers.*;
import com.campus.models.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AdminDashboardController {

    private GestionUtilisateur gestionUtilisateur;
    private GestionBatiment gestionBatiment;
    private GestionChambre gestionChambre;
    private GestionEtudiant gestionEtudiant;
    private Stage primaryStage;
    private Utilisateur currentUser;
    private Button activeMenuButton;

    private BorderPane root;

    public AdminDashboardController(GestionUtilisateur gestionUtilisateur,
            GestionBatiment gestionBatiment,
            GestionChambre gestionChambre,
            GestionEtudiant gestionEtudiant,
            Stage primaryStage) {

        this.gestionUtilisateur = gestionUtilisateur;
        this.gestionBatiment = gestionBatiment;
        this.gestionChambre = gestionChambre;
        this.gestionEtudiant = gestionEtudiant;
        this.primaryStage = primaryStage;

        this.currentUser = gestionUtilisateur.getUtilisateurConnecte();

        if (this.currentUser == null) {
            throw new IllegalStateException("Aucun utilisateur connecté !");
        }
    }

    public void show() {
        root = createLayout();
        Scene scene = new Scene(root, 1360, 820);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dashboard Admin");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private BorderPane createLayout() {
        BorderPane layout = new BorderPane();
        layout.getStyleClass().add("dashboard-root");

        layout.setTop(createHeader());
        layout.setLeft(createMenu());
        layout.setCenter(createDashboardContent());

        return layout;
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.getStyleClass().add("dashboard-header");
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Tableau de bord administrateur");
        title.getStyleClass().add("dashboard-title");
        titleBox.getChildren().add(title);

        /*
         * Label subtitle = new
         * Label("Pilotez les espaces, les comptes et les affectations depuis une seule vue."
         * );
         * subtitle.getStyleClass().add("header-info");
         * titleBox.getChildren().addAll(title, subtitle);
         */

        Label userLabel = new Label("Connecté: " + currentUser.getNomComplet());
        userLabel.getStyleClass().add("header-info");

        Button logoutButton = new Button("Déconnexion");
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setOnAction(e -> logout());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(titleBox, spacer, userLabel, logoutButton);
        return header;
    }

    private VBox createMenu() {
        VBox menu = new VBox(16);
        menu.getStyleClass().add("dashboard-menu");
        menu.setPrefWidth(210);
        menu.setMinWidth(210);
        menu.setMaxWidth(210);

        Label menuTitle = new Label("Administration");
        menuTitle.getStyleClass().add("menu-title");

        Label menuSubtitle = new Label("Accès rapide aux modules principaux");
        menuSubtitle.getStyleClass().add("menu-subtitle");

        VBox menuButtons = new VBox(10);
        Button dashboardButton = createMenuButton("Tableau de bord", this::showDashboardHome);
        Button batimentsButton = createMenuButton("Bâtiments", this::showBatimentsView);
        Button chambresButton = createMenuButton("Chambres", this::showChambresView);
        Button etudiantsButton = createMenuButton("Étudiants", this::showEtudiantsView);
        Button utilisateursButton = createMenuButton("Utilisateurs", this::showUtilisateursView);
        Button statistiquesButton = createMenuButton("Statistiques", this::showStatistiques);
        menuButtons.getChildren().addAll(
                dashboardButton, batimentsButton, chambresButton, etudiantsButton, utilisateursButton,
                statistiquesButton);
        menuButtons.getStyleClass().add("menu-button-list");
        setActiveMenuButton(dashboardButton);

        VBox menuFoot = new VBox(6);
        menuFoot.getStyleClass().add("sidebar-summary");
        Label footTitle = new Label("Session active");
        footTitle.getStyleClass().add("sidebar-summary-title");
        Label footText = new Label(currentUser.getEmail());
        footText.getStyleClass().add("sidebar-summary-text");
        menuFoot.getChildren().addAll(footTitle, footText);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        menu.getChildren().addAll(menuTitle, menuSubtitle, menuButtons, spacer, menuFoot);

        return menu;
    }

    private VBox createDashboardContent() {
        VBox content = new VBox(22);
        content.getStyleClass().add("content-panel");
        content.setPadding(new Insets(26));

        Label title = new Label("Vue d'ensemble");
        title.getStyleClass().add("view-title");

        Label intro = new Label("Consultez les indicateurs clés et ouvrez rapidement les espaces de gestion.");
        intro.getStyleClass().add("panel-subtitle");

        HBox stats = new HBox(18);
        stats.getChildren().addAll(
                createStatCard("Bâtiments", String.valueOf(gestionBatiment.getAllBatiments().size()),
                        "Sites configurés"),
                createStatCard("Chambres", String.valueOf(gestionChambre.getAllChambres().size()), "Capacité totale"),
                createStatCard("Étudiants", String.valueOf(gestionEtudiant.getAllEtudiants().size()),
                        "Profils suivis"));

        HBox quickActions = new HBox(16,
                createQuickActionCard("Gérer les bâtiments", "Ajouter, modifier ou organiser les résidences.",
                        this::showBatimentsView),
                createQuickActionCard("Suivre les chambres", "Contrôler l'état et la disponibilité en temps réel.",
                        this::showChambresView),
                createQuickActionCard("Administrer les comptes", "Activer, modifier ou désactiver les accès.",
                        this::showUtilisateursView));
        quickActions.getStyleClass().add("quick-actions-row");

        content.getChildren().addAll(title, intro, stats, quickActions);
        return content;
    }

    private VBox createStatCard(String title, String value, String caption) {
        VBox card = new VBox(8);
        card.getStyleClass().add("stat-card");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");

        Label captionLabel = new Label(caption);
        captionLabel.getStyleClass().add("stat-caption");

        card.getChildren().addAll(titleLabel, valueLabel, captionLabel);

        return card;
    }

    private Button createMenuButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStyleClass().add("dashboard-menu-button");
        btn.setOnAction(e -> {
            setActiveMenuButton(btn);
            action.run();
        });
        return btn;
    }

    private void setActiveMenuButton(Button button) {
        if (activeMenuButton != null) {
            activeMenuButton.getStyleClass().remove("dashboard-menu-button-active");
        }
        activeMenuButton = button;
        if (!button.getStyleClass().contains("dashboard-menu-button-active")) {
            button.getStyleClass().add("dashboard-menu-button-active");
        }
    }

    private VBox createQuickActionCard(String title, String description, Runnable action) {
        VBox card = new VBox(12);
        card.getStyleClass().add("admin-action-card");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("admin-action-title");

        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().add("admin-action-text");
        descriptionLabel.setWrapText(true);

        Button openButton = new Button("Ouvrir");
        openButton.getStyleClass().add("primary-button");
        openButton.setOnAction(e -> action.run());

        card.getChildren().addAll(titleLabel, descriptionLabel, openButton);
        return card;
    }

    private void showDashboardHome() {
        setCenterContent(createDashboardContent());
    }

    private void showBatimentsView() {
        BatimentViewController controller = new BatimentViewController(gestionBatiment);
        setCenterContent(controller.createView());
    }

    private void showChambresView() {
        ChambreViewController controller = new ChambreViewController(gestionBatiment, gestionChambre);
        setCenterContent(controller.createView());
    }

    private void showEtudiantsView() {
        EtudiantViewController controller = new EtudiantViewController(gestionEtudiant, gestionChambre);
        setCenterContent(controller.createView());
    }

    private void showUtilisateursView() {
        UtilisateurViewController controller = new UtilisateurViewController(gestionUtilisateur);
        setCenterContent(controller.createView());
    }

    private void showStatistiques() {
        StatisticsViewController controller = new StatisticsViewController(gestionBatiment, gestionChambre,
                gestionEtudiant);
        setCenterContent(controller.createView());
    }

    private void setCenterContent(Node node) {
        StackPane wrapper = new StackPane(node);
        wrapper.getStyleClass().add("admin-center-shell");
        StackPane.setMargin(node, new Insets(0));
        root.setCenter(wrapper);
    }

    private void logout() {
        gestionUtilisateur.logout();

        LoginController loginController = new LoginController(gestionUtilisateur, primaryStage);
        Scene scene = new Scene(loginController.createLoginView(), 1100, 680);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        // primaryStage.setTitle("Campus Room Manager - Login");
    }
}
