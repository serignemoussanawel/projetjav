package com.campus.ui.controllers;

import com.campus.managers.*;
import com.campus.models.*;

import javafx.geometry.Pos;
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

    private BorderPane root; // 🔥 pour changer le centre dynamiquement

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
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Dashboard - Campus Room Manager");
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
        header.getStyleClass().add("admin-header");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Dashboard Administrateur");
        title.getStyleClass().add("admin-title");

        Label userLabel = new Label("👤 " + currentUser.getNomComplet());
        userLabel.getStyleClass().add("header-info");

        Button logoutButton = new Button("Déconnexion");
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setOnAction(e -> logout());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(title, spacer, userLabel, logoutButton);
        return header;
    }

    private VBox createMenu() {
        VBox menu = new VBox(15);
        menu.getStyleClass().add("admin-menu");
        menu.setPrefWidth(220);

        Label menuTitle = new Label("MENU");
        menuTitle.getStyleClass().add("admin-menu-title");

        menu.getChildren().addAll(
                menuTitle,
                createMenuButton("🏢 Bâtiments", this::showBatimentsView),
                createMenuButton("🛏 Chambres", this::showChambresView),
                createMenuButton("🎓 Étudiants", this::showEtudiantsView),
                createMenuButton("👥 Utilisateurs", this::showUtilisateursView),
                createMenuButton("📊 Statistiques", this::showStatistiques));

        return menu;
    }

    private VBox createDashboardContent() {
        VBox content = new VBox(20);
        content.getStyleClass().add("content-panel");

        Label title = new Label("📊 Tableau de bord");
        title.getStyleClass().add("view-title");

        HBox stats = new HBox(20);

        stats.getChildren().addAll(
                createStatCard("Bâtiments", String.valueOf(gestionBatiment.getAllBatiments().size())),
                createStatCard("Chambres", String.valueOf(gestionChambre.getAllChambres().size())),
                createStatCard("Étudiants", String.valueOf(gestionEtudiant.getAllEtudiants().size())));

        content.getChildren().addAll(title, stats);
        return content;
    }

    private VBox createStatCard(String title, String value) {
        VBox card = new VBox(10);
        card.getStyleClass().add("stat-card");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");

        card.getChildren().addAll(titleLabel, valueLabel);

        return card;
    }

    private Button createMenuButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setPrefWidth(180);
        btn.getStyleClass().add("dashboard-menu-button");

        btn.setOnMouseEntered(e -> btn.getStyleClass().add("hover"));
        btn.setOnMouseExited(e -> btn.getStyleClass().remove("hover"));

        btn.setOnAction(e -> action.run());

        return btn;
    }

    // 🔥 Navigation dynamique (pro)
    private void showBatimentsView() {
        BatimentViewController controller = new BatimentViewController(gestionBatiment);
        controller.show();
    }

    private void showChambresView() {
        ChambreViewController controller = new ChambreViewController(gestionBatiment, gestionChambre);
        controller.show();
    }

    private void showEtudiantsView() {
        EtudiantViewController controller = new EtudiantViewController(gestionEtudiant, gestionChambre);
        controller.show();
    }

    private void showUtilisateursView() {
        UtilisateurViewController controller = new UtilisateurViewController(gestionUtilisateur);
        controller.show();
    }

    private void showStatistiques() {
        StatisticsViewController controller = new StatisticsViewController(gestionBatiment, gestionChambre,
                gestionEtudiant);
        controller.show();
    }

    private void logout() {
        gestionUtilisateur.logout();

        LoginController loginController = new LoginController(gestionUtilisateur, primaryStage);
        Scene scene = new Scene(loginController.createLoginView(), 400, 300);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Campus Room Manager - Login");
    }
}