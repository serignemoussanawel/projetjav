package com.campus.ui.controllers;

import com.campus.managers.*;
import com.campus.models.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminDashboardController {
    private GestionUtilisateur gestionUtilisateur;
    private GestionBatiment gestionBatiment;
    private GestionChambre gestionChambre;
    private GestionEtudiant gestionEtudiant;
    private Stage primaryStage;
    private Utilisateur currentUser;

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
    }

    public void show() {
        BorderPane root = createLayout();
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Dashboard - Campus Room Manager");
        primaryStage.show();
    }

    private BorderPane createLayout() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 10;");

        // Top: Header
        HBox header = createHeader();
        root.setTop(header);

        // Left: Menu
        VBox menu = createMenu();
        root.setLeft(menu);

        // Center: Content area
        VBox contentArea = new VBox();
        contentArea.setId("content-area");
        root.setCenter(contentArea);

        return root;
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setStyle("-fx-padding: 10; -fx-background-color: #e0e0e0;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Tableau de bord Administrateur");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Label userLabel = new Label("Connecté: " + currentUser.getNomComplet());
        userLabel.setStyle("-fx-font-size: 12;");

        Button logoutButton = new Button("Déconnexion");
        logoutButton.setOnAction(e -> logout());

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        header.getChildren().addAll(title, spacer, userLabel, logoutButton);
        return header;
    }

    private VBox createMenu() {
        VBox menu = new VBox(10);
        menu.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-width: 0 1 0 0;");
        menu.setPrefWidth(200);

        Label menuTitle = new Label("Menu");
        menuTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        Button batimentsBtn = new Button("Gestion des Bâtiments");
        batimentsBtn.setPrefWidth(180);
        batimentsBtn.setOnAction(e -> showBatimentsView());

        Button chambresBtn = new Button("Gestion des Chambres");
        chambresBtn.setPrefWidth(180);
        chambresBtn.setOnAction(e -> showChambresView());

        Button etudiantsBtn = new Button("Gestion des Étudiants");
        etudiantsBtn.setPrefWidth(180);
        etudiantsBtn.setOnAction(e -> showEtudiantsView());

        Button utilisateursBtn = new Button("Gestion des Utilisateurs");
        utilisateursBtn.setPrefWidth(180);
        utilisateursBtn.setOnAction(e -> showUtilisateursView());

        Button statistiquesBtn = new Button("Statistiques");
        statistiquesBtn.setPrefWidth(180);
        statistiquesBtn.setOnAction(e -> showStatistiques());

        menu.getChildren().addAll(menuTitle, batimentsBtn, chambresBtn, etudiantsBtn, 
                                  utilisateursBtn, statistiquesBtn);
        return menu;
    }

    private void showBatimentsView() {
        new BatimentViewController(gestionBatiment, gestionChambre, primaryStage, this).show();
    }

    private void showChambresView() {
        new ChambreViewController(gestionBatiment, gestionChambre, primaryStage, this).show();
    }

    private void showEtudiantsView() {
        new EtudiantViewController(gestionEtudiant, gestionChambre, primaryStage, this).show();
    }

    private void showUtilisateursView() {
        new UtilisateurViewController(gestionUtilisateur, primaryStage, this).show();
    }

    private void showStatistiques() {
        new StatisticsViewController(gestionBatiment, gestionChambre, gestionEtudiant, primaryStage).show();
    }

    private void logout() {
        gestionUtilisateur.logout();
        primaryStage.setScene(new Scene(new LoginController(gestionUtilisateur, primaryStage).createLoginView(), 400, 300));
        primaryStage.setTitle("Campus Room Manager - Login");
    }
}
