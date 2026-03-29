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

public class EtudiantDashboardController {
    private GestionUtilisateur gestionUtilisateur;
    private GestionEtudiant gestionEtudiant;
    private GestionChambre gestionChambre;
    private GestionBatiment gestionBatiment;
    private Stage primaryStage;
    private Utilisateur currentUser;

    public EtudiantDashboardController(GestionUtilisateur gestionUtilisateur,
                                     GestionEtudiant gestionEtudiant,
                                     GestionChambre gestionChambre,
                                     GestionBatiment gestionBatiment,
                                     Stage primaryStage) {
        this.gestionUtilisateur = gestionUtilisateur;
        this.gestionEtudiant = gestionEtudiant;
        this.gestionChambre = gestionChambre;
        this.gestionBatiment = gestionBatiment;
        this.primaryStage = primaryStage;
        this.currentUser = gestionUtilisateur.getUtilisateurConnecte();
    }

    public void show() {
        BorderPane root = createLayout();
        Scene scene = new Scene(root, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Espace Étudiant - Campus Room Manager");
        primaryStage.show();
    }

    private BorderPane createLayout() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 10;");

        HBox header = createHeader();
        root.setTop(header);

        VBox content = createContent();
        root.setCenter(content);

        return root;
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setStyle("-fx-padding: 10; -fx-background-color: #e0e0e0;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Espace Étudiant");
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

    private VBox createContent() {
        VBox content = new VBox(15);
        content.setStyle("-fx-padding: 20; -fx-alignment: CENTER;");

        // Find the student
        Etudiant etudiant = findEtudiantByUser();

        if (etudiant != null) {
            Label infoLabel = new Label("Informations Personnelles");
            infoLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

            VBox infoBox = new VBox(10);
            infoBox.setStyle("-fx-padding: 10; -fx-border-color: #cccccc; -fx-border-width: 1;");

            Label nameLabel = new Label("Nom: " + etudiant.getNomComplet());
            Label matriculeLabel = new Label("Numéro Matricule: " + etudiant.getNumeroMatricule());
            Label specialiteLabel = new Label("Spécialité: " + etudiant.getSpecialite());

            infoBox.getChildren().addAll(nameLabel, matriculeLabel, specialiteLabel);

            content.getChildren().add(infoLabel);
            content.getChildren().add(infoBox);

            // Room information
            if (etudiant.hasRoom()) {
                Label chambreLabel = new Label("Chambre Affectée");
                chambreLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

                Chambre chambre = gestionChambre.getChambre(etudiant.getChambreId());
                if (chambre != null) {
                    VBox chambreBox = new VBox(10);
                    chambreBox.setStyle("-fx-padding: 10; -fx-border-color: #4CAF50; -fx-border-width: 2;");

                    Label codeLabel = new Label("Code: " + chambre.getCode());
                    Label typeLabel = new Label("Type: " + chambre.getType());
                    Label capaciteLabel = new Label("Capacité: " + chambre.getCapacite() + " personne(s)");
                    Label batimentLabel = new Label("Bâtiment: " + 
                        gestionBatiment.getBatiment(chambre.getBatimentId()).getNom());
                    Label etageLabel = new Label("Étage: " + chambre.getEtage());
                    Label dateLabel = new Label("Date d'affectation: " + etudiant.getDateAffectation());

                    chambreBox.getChildren().addAll(codeLabel, typeLabel, capaciteLabel, 
                                                    batimentLabel, etageLabel, dateLabel);

                    content.getChildren().add(chambreLabel);
                    content.getChildren().add(chambreBox);
                }
            } else {
                Label noRoomLabel = new Label("Vous n'avez pas encore de chambre affectée");
                noRoomLabel.setStyle("-fx-text-fill: #ff9800; -fx-font-size: 12;");
                content.getChildren().add(noRoomLabel);
            }
        }

        return content;
    }

    private Etudiant findEtudiantByUser() {
        // Find student by user name
        for (Etudiant e : gestionEtudiant.getAllEtudiants()) {
            if (e.getNomComplet().equals(currentUser.getNomComplet())) {
                return e;
            }
        }
        return null;
    }

    private void logout() {
        gestionUtilisateur.logout();
        primaryStage.setScene(new Scene(new LoginController(gestionUtilisateur, primaryStage).createLoginView(), 400, 300));
        primaryStage.setTitle("Campus Room Manager - Login");
    }
}
