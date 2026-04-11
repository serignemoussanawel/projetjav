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
        root.getStyleClass().add("dashboard-root");

        HBox header = createHeader();
        root.setTop(header);

        VBox menu = createMenu();
        root.setLeft(menu);

        return root;
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.getStyleClass().add("dashboard-header");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Tableau de bord - Chef de Bâtiment");
        title.getStyleClass().add("dashboard-title");

        Label batimentLbl = new Label("Bâtiment: " + gestionBatiment.getBatiment(batimentId).getNom());
        batimentLbl.getStyleClass().add("header-info");

        Label userLabel = new Label("Connecté: " + currentUser.getNomComplet());
        userLabel.getStyleClass().add("header-info");

        Button logoutButton = new Button("Déconnexion");
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setOnAction(e -> logout());

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        header.getChildren().addAll(title, spacer, batimentLbl, userLabel, logoutButton);
        return header;
    }

    private VBox createMenu() {
        VBox menu = new VBox(14);
        menu.getStyleClass().add("dashboard-menu");
        menu.setPrefWidth(200);

        Label menuTitle = new Label("Menu");
        menuTitle.getStyleClass().add("menu-title");

        Button chambresBtn = new Button("Mes Chambres");
        chambresBtn.getStyleClass().add("dashboard-menu-button");
        chambresBtn.setOnAction(e -> showChambres());

        Button etudiantsBtn = new Button("Étudiants du Bâtiment");
        etudiantsBtn.getStyleClass().add("dashboard-menu-button");
        etudiantsBtn.setOnAction(e -> showEtudiants());

        Button disponibiliteBtn = new Button("Disponibilité");
        disponibiliteBtn.getStyleClass().add("dashboard-menu-button");
        disponibiliteBtn.setOnAction(e -> showDisponibilite());

        menu.getChildren().addAll(menuTitle, chambresBtn, etudiantsBtn, disponibiliteBtn);
        return menu;
    }

    private void showChambres() {
    TableView<Chambre> table = new TableView<>();
    table.getStyleClass().add("dashboard-table");

    TableColumn<Chambre, String> codeCol = new TableColumn<>("Code");
    codeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCode()));

    TableColumn<Chambre, String> typeCol = new TableColumn<>("Type");
    typeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getType()));

    TableColumn<Chambre, String> statutCol = new TableColumn<>("Statut");
    statutCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
        c.getValue().isLibre() ? "Disponible" : "Occupée"
    ));

    table.getColumns().addAll(codeCol, typeCol, statutCol);

    var chambres = gestionChambre.getAllChambres().stream()
        .filter(c -> c.getBatimentId().equals(batimentId))
        .toList();

    table.setItems(javafx.collections.FXCollections.observableArrayList(chambres));

    VBox content = new VBox(18, new Label("Liste des chambres"), table);
    content.getStyleClass().add("content-panel");
    ((BorderPane) primaryStage.getScene().getRoot()).setCenter(content);
}

    private void showEtudiants() {
    TableView<Etudiant> table = new TableView<>();
    table.getStyleClass().add("dashboard-table");

    TableColumn<Etudiant, String> nomCol = new TableColumn<>("Nom");
    nomCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNomComplet()));

    TableColumn<Etudiant, String> matriculeCol = new TableColumn<>("Matricule");
    matriculeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNumeroMatricule()));

    TableColumn<Etudiant, String> chambreCol = new TableColumn<>("Chambre");
    chambreCol.setCellValueFactory(c -> {
        Etudiant e = c.getValue();
        if (e.hasRoom()) {
            Chambre ch = gestionChambre.getChambre(e.getChambreId());
            return new javafx.beans.property.SimpleStringProperty(ch != null ? ch.getCode() : "N/A");
        }
        return new javafx.beans.property.SimpleStringProperty("Non affecté");
    });

    table.getColumns().addAll(nomCol, matriculeCol, chambreCol);

    var etudiants = gestionEtudiant.getAllEtudiants().stream()
        .filter(e -> e.hasRoom())
        .filter(e -> {
            Chambre ch = gestionChambre.getChambre(e.getChambreId());
            return ch != null && ch.getBatimentId().equals(batimentId);
        })
        .toList();

    table.setItems(javafx.collections.FXCollections.observableArrayList(etudiants));

    VBox content = new VBox(18, new Label("Étudiants du bâtiment"), table);
    content.getStyleClass().add("content-panel");
    ((BorderPane) primaryStage.getScene().getRoot()).setCenter(content);
}

    private void showDisponibilite() {
        var chambres = gestionChambre.getAllChambres().stream()
            .filter(c -> c.getBatimentId().equals(batimentId))
            .toList();

        long libres = chambres.stream().filter(Chambre::isLibre).count();
        long occupees = chambres.size() - libres;

        Label summary = new Label("Chambres libres : " + libres + "    Occupées : " + occupees);
        summary.getStyleClass().add("summary-label");

        TableView<Chambre> table = new TableView<>();
        table.getStyleClass().add("dashboard-table");

        TableColumn<Chambre, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCode()));

        TableColumn<Chambre, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getType()));

        TableColumn<Chambre, String> statutCol = new TableColumn<>("Statut");
        statutCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
            c.getValue().isLibre() ? "Disponible" : "Occupée"
        ));

        table.getColumns().addAll(codeCol, typeCol, statutCol);
        table.setItems(javafx.collections.FXCollections.observableArrayList(chambres));

        VBox content = new VBox(18, summary, table);
        content.getStyleClass().add("content-panel");

        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(content);
    }

    private void logout() {
        gestionUtilisateur.logout();
        primaryStage.setScene(new Scene(new LoginController(gestionUtilisateur, primaryStage).createLoginView(), 400, 300));
        primaryStage.setTitle("Campus Room Manager - Login");
    }
}
