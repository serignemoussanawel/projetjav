package com.campus.ui.controllers;

import com.campus.managers.GestionBatiment;
import com.campus.managers.GestionChambre;
import com.campus.managers.GestionEtudiant;
import com.campus.managers.GestionUtilisateur;
import com.campus.models.Chambre;
import com.campus.models.Etudiant;
import com.campus.models.Utilisateur;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EtudiantDashboardController {
    private final GestionUtilisateur gestionUtilisateur;
    private final GestionEtudiant gestionEtudiant;
    private final GestionChambre gestionChambre;
    private final GestionBatiment gestionBatiment;
    private final Stage primaryStage;
    private final Utilisateur currentUser;
    private BorderPane root;
    private Button activeMenuButton;

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
        root = createLayout();
        Scene scene = new Scene(root, 1240, 760);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Espace Étudiant ");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private BorderPane createLayout() {
        BorderPane layout = new BorderPane();
        layout.getStyleClass().add("dashboard-root");
        layout.setTop(createHeader());
        layout.setLeft(createSidebar());
        layout.setCenter(wrapContent(createProfileView()));
        return layout;
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.getStyleClass().add("dashboard-header");
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Espace étudiant");
        title.getStyleClass().add("dashboard-title");
        Label subtitle = new Label("Consultez votre profil et vos informations de logement.");
        subtitle.getStyleClass().add("header-info");
        titleBox.getChildren().addAll(title, subtitle);

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

    private VBox createSidebar() {
        VBox menu = new VBox(16);
        menu.getStyleClass().add("dashboard-menu");
        menu.setPrefWidth(230);

        Label title = new Label("Mon espace");
        title.getStyleClass().add("menu-title");
        Label subtitle = new Label("Navigation personnelle");
        subtitle.getStyleClass().add("menu-subtitle");

        Button profileButton = createMenuButton("Mon profil", this::showProfile);
        Button roomButton = createMenuButton("Ma chambre", this::showRoom);

        VBox summary = new VBox(6);
        summary.getStyleClass().add("sidebar-summary");
        Label summaryTitle = new Label("Compte");
        summaryTitle.getStyleClass().add("sidebar-summary-title");
        Label summaryText = new Label(currentUser.getEmail());
        summaryText.getStyleClass().add("sidebar-summary-text");
        summary.getChildren().addAll(summaryTitle, summaryText);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        menu.getChildren().addAll(title, subtitle, profileButton, roomButton, spacer, summary);

        setActiveMenuButton(profileButton);
        return menu;
    }

    private void showProfile() {
        setCenterContent(createProfileView());
    }

    private void showRoom() {
        setCenterContent(createRoomView());
    }

    private VBox createProfileView() {
        VBox content = new VBox(20);
        content.getStyleClass().add("content-panel");
        content.setPadding(new Insets(26));

        Etudiant etudiant = findEtudiantByUser();

        Label title = new Label("Mon profil");
        title.getStyleClass().add("view-title");
        Label subtitle = new Label("Informations personnelles associées à votre compte étudiant.");
        subtitle.getStyleClass().add("panel-subtitle");

        VBox infoCard = new VBox(12);
        infoCard.getStyleClass().add("admin-action-card");

        if (etudiant != null) {
            infoCard.getChildren().addAll(
                    profileRow("Nom complet", etudiant.getNomComplet()),
                    profileRow("Email", etudiant.getEmail()),
                    profileRow("Code permanent", etudiant.getCodePermanent()),
                    profileRow("Spécialité", etudiant.getSpecialite()));
        } else {
            infoCard.getChildren().add(new Label("Aucun profil étudiant correspondant n'a été trouvé."));
        }

        content.getChildren().addAll(title, subtitle, infoCard);
        return content;
    }

    private VBox createRoomView() {
        VBox content = new VBox(20);
        content.getStyleClass().add("content-panel");
        content.setPadding(new Insets(26));

        Label title = new Label("Ma chambre");
        title.getStyleClass().add("view-title");
        Label subtitle = new Label("Retrouvez ici les détails de votre affectation actuelle.");
        subtitle.getStyleClass().add("panel-subtitle");

        VBox roomCard = new VBox(12);
        roomCard.getStyleClass().add("admin-action-card");

        Etudiant etudiant = findEtudiantByUser();
        if (etudiant != null && etudiant.hasRoom()) {
            Chambre chambre = gestionChambre.getChambre(etudiant.getChambreId());
            if (chambre != null) {
                roomCard.getChildren().addAll(
                        profileRow("Chambre", String.valueOf(chambre.getNumero())),
                        profileRow("Type", chambre.getType()),
                        profileRow("Capacité", chambre.getCapacite() + " personne(s)"),
                        profileRow("Bâtiment", gestionBatiment.getBatiment(chambre.getBatimentId()).getNom()),
                        profileRow("Étage", String.valueOf(chambre.getEtage())),
                        profileRow("Date d'affectation", etudiant.getDateAffectation()));
            }
        } else {
            Label noRoom = new Label("Vous n'avez pas encore de chambre affectée.");
            noRoom.getStyleClass().add("panel-subtitle");
            roomCard.getChildren().add(noRoom);
        }

        content.getChildren().addAll(title, subtitle, roomCard);
        return content;
    }

    private HBox profileRow(String label, String value) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        Label labelNode = new Label(label + ":");
        labelNode.getStyleClass().add("stat-title");
        Label valueNode = new Label(value);
        valueNode.getStyleClass().add("admin-action-title");
        row.getChildren().addAll(labelNode, valueNode);
        return row;
    }

    private Etudiant findEtudiantByUser() {
        for (Etudiant etudiant : gestionEtudiant.getAllEtudiants()) {
            if (etudiant.getNomComplet().equals(currentUser.getNomComplet())) {
                return etudiant;
            }
        }
        return null;
    }

    private Button createMenuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("dashboard-menu-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> {
            setActiveMenuButton(button);
            action.run();
        });
        return button;
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

    private void setCenterContent(Node node) {
        root.setCenter(wrapContent(node));
    }

    private StackPane wrapContent(Node node) {
        StackPane wrapper = new StackPane(node);
        wrapper.getStyleClass().add("admin-center-shell");
        return wrapper;
    }

    private void logout() {
        gestionUtilisateur.logout();
        Scene scene = new Scene(new LoginController(gestionUtilisateur, primaryStage).createLoginView(), 1100, 680);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Campus Room Manager - Login");
    }
}
