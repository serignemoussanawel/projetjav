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

public class ChefBatimentDashboardController {
    private GestionUtilisateur gestionUtilisateur;
    private GestionBatiment gestionBatiment;
    private GestionChambre gestionChambre;
    private GestionEtudiant gestionEtudiant;
    private Stage primaryStage;
    private Utilisateur currentUser;
    private String batimentId;

    public ChefBatimentDashboardController(GestionUtilisateur gestionUtilisateur,
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
        this.batimentId = currentUser.getBatimentId();
    }

    public void show() {
        BorderPane root = createLayout();
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chef de Bâtiment Dashboard - Campus Room Manager");
        primaryStage.show();
    }

    private BorderPane createLayout() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 10;");

        HBox header = createHeader();
        root.setTop(header);

        VBox menu = createMenu();
        root.setLeft(menu);

        return root;
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setStyle("-fx-padding: 10; -fx-background-color: #e0e0e0;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Tableau de bord - Chef de Bâtiment");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Label batimentLbl = new Label("Bâtiment: " + gestionBatiment.getBatiment(batimentId).getNom());
        batimentLbl.setStyle("-fx-font-size: 12;");

        Label userLabel = new Label("Connecté: " + currentUser.getNomComplet());
        userLabel.setStyle("-fx-font-size: 12;");

        Button logoutButton = new Button("Déconnexion");
        logoutButton.setOnAction(e -> logout());

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        header.getChildren().addAll(title, spacer, batimentLbl, userLabel, logoutButton);
        return header;
    }

    private VBox createMenu() {
        VBox menu = new VBox(10);
        menu.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-width: 0 1 0 0;");
        menu.setPrefWidth(200);

        Label menuTitle = new Label("Menu");
        menuTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        Button chambresBtn = new Button("Mes Chambres");
        chambresBtn.setPrefWidth(180);
        chambresBtn.setOnAction(e -> showChambres());

        Button etudiantsBtn = new Button("Étudiants du Bâtiment");
        etudiantsBtn.setPrefWidth(180);
        etudiantsBtn.setOnAction(e -> showEtudiants());

        Button disponibiliteBtn = new Button("Disponibilité");
        disponibiliteBtn.setPrefWidth(180);
        disponibiliteBtn.setOnAction(e -> showDisponibilite());

        menu.getChildren().addAll(menuTitle, chambresBtn, etudiantsBtn, disponibiliteBtn);
        return menu;
    }

    private void showChambres() {
        // Show chambres du bâtiment
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chambres du Bâtiment");
        alert.setHeaderText("Liste des chambres");
        alert.setContentText("Fonctionnalité en cours de développement");
        alert.showAndWait();
    }

    private void showEtudiants() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Étudiants");
        alert.setHeaderText("Liste des étudiants");
        alert.setContentText("Fonctionnalité en cours de développement");
        alert.showAndWait();
    }

    private void showDisponibilite() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Disponibilité");
        alert.setHeaderText("Chambre disponibles");
        alert.setContentText("Fonctionnalité en cours de développement");
        alert.showAndWait();
    }

    private void logout() {
        gestionUtilisateur.logout();
        primaryStage.setScene(new Scene(new LoginController(gestionUtilisateur, primaryStage).createLoginView(), 400, 300));
        primaryStage.setTitle("Campus Room Manager - Login");
    }
}
