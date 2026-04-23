package com.campus.ui.controllers;

import com.campus.managers.GestionBatiment;
import com.campus.managers.GestionChambre;
import com.campus.managers.GestionEtudiant;
import com.campus.managers.GestionUtilisateur;
import com.campus.models.Chambre;
import com.campus.models.Etudiant;
import com.campus.models.Utilisateur;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class ChefBatimentDashboardController {
    private final GestionUtilisateur gestionUtilisateur;
    private final GestionBatiment gestionBatiment;
    private final GestionChambre gestionChambre;
    private final GestionEtudiant gestionEtudiant;
    private final Stage primaryStage;
    private final Utilisateur currentUser;
    private final String batimentId;
    private BorderPane root;
    private Button activeMenuButton;

    private TableView<Chambre> chambresTable;
    private TextField chambreIdField;
    private Spinner<Integer> chambreNumeroSpinner;
    private Spinner<Integer> chambreEtageSpinner;
    private Spinner<Integer> chambreCapaciteSpinner;
    private ComboBox<String> chambreTypeCombo;
    private Button saveChambreButton;
    private Chambre selectedChambre;

    private TableView<Etudiant> affectationTable;
    private ComboBox<Etudiant> etudiantCombo;
    private ComboBox<Chambre> chambreCombo;

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
        root = createLayout();
        Scene scene = new Scene(root, 1320, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dashboard Chef de Bâtiment ");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private BorderPane createLayout() {
        BorderPane layout = new BorderPane();
        layout.getStyleClass().add("dashboard-root");
        layout.setTop(createHeader());
        layout.setLeft(createMenu());
        layout.setCenter(wrapContent(createOverview()));
        return layout;
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.getStyleClass().add("dashboard-header");
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Tableau de bord chef de bâtiment");
        title.getStyleClass().add("dashboard-title");
        Label subtitle = new Label("Gérez les chambres et les affectations de votre bâtiment.");
        subtitle.getStyleClass().add("header-info");
        titleBox.getChildren().addAll(title, subtitle);

        Label batimentLabel = new Label("Bâtiment: " + gestionBatiment.getBatiment(batimentId).getNom());
        batimentLabel.getStyleClass().add("header-info");

        Label userLabel = new Label("Connecté: " + currentUser.getNomComplet());
        userLabel.getStyleClass().add("header-info");

        Button logoutButton = new Button("Déconnexion");
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setOnAction(e -> logout());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titleBox, spacer, batimentLabel, userLabel, logoutButton);
        return header;
    }

    private VBox createMenu() {
        VBox menu = new VBox(16);
        menu.getStyleClass().add("dashboard-menu");
        menu.setPrefWidth(240);

        Label title = new Label("Chef de bâtiment");
        title.getStyleClass().add("menu-title");
        Label subtitle = new Label("Actions sur votre zone");
        subtitle.getStyleClass().add("menu-subtitle");

        VBox buttons = new VBox(10);
        Button overviewButton = createMenuButton("Vue d'ensemble", this::showOverview);
        Button chambresButton = createMenuButton("Mes chambres", this::showChambresManager);
        Button affectationsButton = createMenuButton("Affectations", this::showAffectationsManager);
        Button disponibiliteButton = createMenuButton("Disponibilité", this::showDisponibilite);
        buttons.getChildren().addAll(overviewButton, chambresButton, affectationsButton, disponibiliteButton);

        VBox infoCard = new VBox(6);
        infoCard.getStyleClass().add("sidebar-summary");
        Label infoTitle = new Label("Zone gérée");
        infoTitle.getStyleClass().add("sidebar-summary-title");
        Label infoText = new Label(gestionBatiment.getBatiment(batimentId).getNom());
        infoText.getStyleClass().add("sidebar-summary-text");
        infoCard.getChildren().addAll(infoTitle, infoText);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        menu.getChildren().addAll(title, subtitle, buttons, spacer, infoCard);

        setActiveMenuButton(overviewButton);
        return menu;
    }

    private VBox createOverview() {
        VBox content = new VBox(22);
        content.getStyleClass().add("content-panel");
        content.setPadding(new Insets(26));

        Label title = new Label("Vue d'ensemble");
        title.getStyleClass().add("view-title");
        Label subtitle = new Label("Capacité, disponibilité et occupations dans votre bâtiment.");
        subtitle.getStyleClass().add("panel-subtitle");

        List<Chambre> chambres = getChambresDuBatiment();
        long libres = chambres.stream().filter(Chambre::isLibre).count();
        long occupees = chambres.size() - libres;
        long etudiantsLoges = getEtudiantsLogesDuBatiment().size();

        HBox cards = new HBox(16,
                createStatCard("Chambres", String.valueOf(chambres.size()), "Capacité totale"),
                createStatCard("Disponibles", String.valueOf(libres), "Espaces libres"),
                createStatCard("Occupées", String.valueOf(occupees), "Espaces affectés"),
                createStatCard("Étudiants logés", String.valueOf(etudiantsLoges), "Occupants suivis"));

        content.getChildren().addAll(title, subtitle, cards);
        return content;
    }

    private HBox createChambresManager() {
        HBox rootView = new HBox(18);
        rootView.getStyleClass().add("split-view");

        VBox listPanel = new VBox(16);
        listPanel.getStyleClass().add("content-panel");
        HBox.setHgrow(listPanel, Priority.ALWAYS);

        Label title = new Label("Mes chambres");
        title.getStyleClass().add("view-title");
        Label subtitle = new Label("Ajoutez des chambres uniquement dans votre bâtiment.");
        subtitle.getStyleClass().add("panel-subtitle");

        HBox actions = new HBox(10);
        actions.getStyleClass().add("toolbar-row");
        Button newButton = new Button("Nouvelle chambre");
        newButton.getStyleClass().add("primary-button");
        newButton.setOnAction(e -> resetChambreForm());
        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(e -> deleteSelectedChambre());
        actions.getChildren().addAll(newButton, deleteButton);

        chambresTable = new TableView<>();
        chambresTable.getStyleClass().add("dashboard-table");
        setupChambresTable();
        refreshChambresTable();
        VBox.setVgrow(chambresTable, Priority.ALWAYS);

        listPanel.getChildren().addAll(title, subtitle, actions, chambresTable);

        VBox formPanel = new VBox(12);
        formPanel.getStyleClass().addAll("content-panel", "editor-panel");
        formPanel.setPrefWidth(360);

        Label formTitle = new Label("Formulaire chambre");
        formTitle.getStyleClass().add("dialog-title");
        Label formSubtitle = new Label("Les nouvelles chambres seront automatiquement liées à votre bâtiment.");
        formSubtitle.getStyleClass().add("dialog-subtitle");
        formSubtitle.setWrapText(true);

        chambreIdField = new TextField();
        chambreIdField.setPromptText("Identifiant");
        chambreNumeroSpinner = new Spinner<>(1, 999, 1);
        chambreEtageSpinner = new Spinner<>(1, 20, 1);
        chambreCapaciteSpinner = new Spinner<>(1, 10, 1);
        chambreTypeCombo = new ComboBox<>(FXCollections.observableArrayList("Simple", "Double", "Suite"));
        chambreTypeCombo.setMaxWidth(Double.MAX_VALUE);

        saveChambreButton = new Button("Enregistrer");
        saveChambreButton.getStyleClass().add("primary-button");
        saveChambreButton.setMaxWidth(Double.MAX_VALUE);
        saveChambreButton.setOnAction(e -> saveChambre());

        Button resetButton = new Button("Réinitialiser");
        resetButton.getStyleClass().add("secondary-button");
        resetButton.setMaxWidth(Double.MAX_VALUE);
        resetButton.setOnAction(e -> resetChambreForm());

        formPanel.getChildren().addAll(
                formTitle, formSubtitle,
                new Label("Identifiant"), chambreIdField,
                new Label("Numéro"), chambreNumeroSpinner,
                new Label("Étage"), chambreEtageSpinner,
                new Label("Capacité"), chambreCapaciteSpinner,
                new Label("Type"), chambreTypeCombo,
                saveChambreButton, resetButton);

        rootView.getChildren().addAll(listPanel, formPanel);
        return rootView;
    }

    private HBox createAffectationsManager() {
        HBox rootView = new HBox(18);
        rootView.getStyleClass().add("split-view");

        VBox listPanel = new VBox(16);
        listPanel.getStyleClass().add("content-panel");
        HBox.setHgrow(listPanel, Priority.ALWAYS);

        Label title = new Label("Affectations");
        title.getStyleClass().add("view-title");
        Label subtitle = new Label("Affectez un étudiant à une chambre libre ou libérez une chambre occupée.");
        subtitle.getStyleClass().add("panel-subtitle");

        affectationTable = new TableView<>();
        affectationTable.getStyleClass().add("dashboard-table");
        setupAffectationTable();
        refreshAffectationTable();
        VBox.setVgrow(affectationTable, Priority.ALWAYS);

        listPanel.getChildren().addAll(title, subtitle, affectationTable);

        VBox formPanel = new VBox(12);
        formPanel.getStyleClass().addAll("content-panel", "editor-panel");
        formPanel.setPrefWidth(380);

        Label formTitle = new Label("Gérer l'affectation");
        formTitle.getStyleClass().add("dialog-title");
        Label formSubtitle = new Label("Choisissez un étudiant puis une chambre libre dans votre bâtiment.");
        formSubtitle.getStyleClass().add("dialog-subtitle");
        formSubtitle.setWrapText(true);

        etudiantCombo = new ComboBox<>();
        etudiantCombo.setMaxWidth(Double.MAX_VALUE);
        chambreCombo = new ComboBox<>();
        chambreCombo.setMaxWidth(Double.MAX_VALUE);
        refreshAffectationChoices();

        Button affecterButton = new Button("Affecter");
        affecterButton.getStyleClass().add("primary-button");
        affecterButton.setMaxWidth(Double.MAX_VALUE);
        affecterButton.setOnAction(e -> affecterChambre());

        Button libererButton = new Button("Libérer la chambre sélectionnée");
        libererButton.getStyleClass().add("secondary-button");
        libererButton.setMaxWidth(Double.MAX_VALUE);
        libererButton.setOnAction(e -> libererChambreSelectionnee());

        formPanel.getChildren().addAll(
                formTitle, formSubtitle,
                new Label("Étudiant"), etudiantCombo,
                new Label("Chambre libre"), chambreCombo,
                affecterButton, libererButton);

        rootView.getChildren().addAll(listPanel, formPanel);
        return rootView;
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

    private void showOverview() {
        setCenterContent(createOverview());
    }

    private void showChambresManager() {
        setCenterContent(createChambresManager());
    }

    private void showAffectationsManager() {
        setCenterContent(createAffectationsManager());
    }

    private void showDisponibilite() {
        TableView<Chambre> table = new TableView<>();
        table.getStyleClass().add("dashboard-table");

        TableColumn<Chambre, Integer> numeroCol = new TableColumn<>("Numéro");
        numeroCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getNumero()));
        TableColumn<Chambre, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
        TableColumn<Chambre, String> statutCol = new TableColumn<>("Disponibilité");
        statutCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isLibre() ? "Libre" : "Occupée"));

        List<Chambre> chambres = getChambresDuBatiment();
        long libres = chambres.stream().filter(Chambre::isLibre).count();
        long occupees = chambres.size() - libres;

        table.getColumns().setAll(List.of(numeroCol, typeCol, statutCol));
        table.setItems(FXCollections.observableArrayList(chambres));

        VBox content = buildTablePanel("Disponibilité des chambres",
                "Chambres libres: " + libres + "   |   Chambres occupées: " + occupees, table);
        setCenterContent(content);
    }

    private VBox buildTablePanel(String titleText, String subtitleText, TableView<?> table) {
        VBox content = new VBox(18);
        content.getStyleClass().add("content-panel");
        Label title = new Label(titleText);
        title.getStyleClass().add("view-title");
        Label subtitle = new Label(subtitleText);
        subtitle.getStyleClass().add("panel-subtitle");
        VBox.setVgrow(table, Priority.ALWAYS);
        content.getChildren().addAll(title, subtitle, table);
        return content;
    }

    private void setupChambresTable() {
        TableColumn<Chambre, Integer> numeroCol = new TableColumn<>("Numéro");
        numeroCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getNumero()));
        TableColumn<Chambre, Integer> etageCol = new TableColumn<>("Étage");
        etageCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getEtage()));
        TableColumn<Chambre, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
        TableColumn<Chambre, String> etatCol = new TableColumn<>("État");
        etatCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEtat()));

        chambresTable.getColumns().setAll(List.of(numeroCol, etageCol, typeCol, etatCol));
        chambresTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> populateChambreForm(newValue));
    }

    private void setupAffectationTable() {
        TableColumn<Etudiant, String> nomCol = new TableColumn<>("Étudiant");
        nomCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNomComplet()));
        TableColumn<Etudiant, String> codePermanentCol = new TableColumn<>("Code permanent");
        codePermanentCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodePermanent()));
        TableColumn<Etudiant, String> chambreCol = new TableColumn<>("Chambre");
        chambreCol.setCellValueFactory(c -> {
            Chambre chambre = c.getValue().hasRoom() ? gestionChambre.getChambre(c.getValue().getChambreId()) : null;
            return new SimpleStringProperty(chambre != null ? String.valueOf(chambre.getNumero()) : "Non affecté");
        });

        affectationTable.getColumns().setAll(List.of(nomCol, codePermanentCol, chambreCol));
    }

    private void populateChambreForm(Chambre chambre) {
        selectedChambre = chambre;
        if (chambre == null) {
            return;
        }
        chambreIdField.setText(chambre.getId());
        chambreIdField.setDisable(true);
        chambreNumeroSpinner.getValueFactory().setValue(chambre.getNumero());
        chambreEtageSpinner.getValueFactory().setValue(chambre.getEtage());
        chambreCapaciteSpinner.getValueFactory().setValue(chambre.getCapacite());
        chambreTypeCombo.setValue(chambre.getType());
        saveChambreButton.setText("Mettre à jour");
    }

    private void saveChambre() {
        if (chambreIdField.getText().isBlank() || chambreTypeCombo.getValue() == null) {
            showAlert("Erreur", "Veuillez remplir les informations de la chambre.");
            return;
        }

        if (selectedChambre == null) {
            Chambre chambre = new Chambre(
                    chambreIdField.getText().trim(),
                    chambreNumeroSpinner.getValue(),
                    batimentId,
                    chambreEtageSpinner.getValue(),
                    chambreCapaciteSpinner.getValue(),
                    chambreTypeCombo.getValue());
            gestionChambre.addChambre(chambre);
        } else {
            selectedChambre.setNumero(chambreNumeroSpinner.getValue());
            selectedChambre.setBatimentId(batimentId);
            selectedChambre.setEtage(chambreEtageSpinner.getValue());
            selectedChambre.setCapacite(chambreCapaciteSpinner.getValue());
            selectedChambre.setType(chambreTypeCombo.getValue());
            gestionChambre.updateChambre(selectedChambre);
        }

        refreshChambresTable();
        refreshAffectationChoices();
        resetChambreForm();
        showOverview();
    }

    private void deleteSelectedChambre() {
        if (chambresTable == null) {
            return;
        }

        Chambre selected = chambresTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner une chambre.");
            return;
        }
        if (selected.isOccupee()) {
            showAlert("Erreur", "Impossible de supprimer une chambre occupée. Libérez-la d'abord.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la chambre ?");
        confirm.setContentText("Chambre " + selected.getNumero());
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            gestionChambre.deleteChambre(selected.getId());
            refreshChambresTable();
            refreshAffectationChoices();
            resetChambreForm();
        }
    }

    private void affecterChambre() {
        Etudiant etudiant = etudiantCombo.getValue();
        Chambre chambre = chambreCombo.getValue();

        if (etudiant == null || chambre == null) {
            showAlert("Erreur", "Veuillez sélectionner un étudiant et une chambre libre.");
            return;
        }
        if (!chambre.isLibre()) {
            showAlert("Erreur", "La chambre sélectionnée n'est plus libre.");
            return;
        }

        if (etudiant.hasRoom()) {
            Chambre ancienneChambre = gestionChambre.getChambre(etudiant.getChambreId());
            if (ancienneChambre != null) {
                gestionChambre.libererChambre(ancienneChambre.getId());
            }
        }

        gestionEtudiant.affecterChambre(etudiant.getId(), chambre.getId(), LocalDate.now().toString());
        gestionChambre.affecterChambre(chambre.getId(), etudiant.getId());
        refreshAffectationTable();
        refreshAffectationChoices();
    }

    private void libererChambreSelectionnee() {
        Etudiant selected = affectationTable.getSelectionModel().getSelectedItem();
        if (selected == null || !selected.hasRoom()) {
            showAlert("Erreur", "Veuillez sélectionner un étudiant déjà affecté.");
            return;
        }

        gestionChambre.libererChambre(selected.getChambreId());
        gestionEtudiant.libererChambre(selected.getId());
        refreshAffectationTable();
        refreshAffectationChoices();
    }

    private void refreshChambresTable() {
        if (chambresTable != null) {
            chambresTable.setItems(FXCollections.observableArrayList(getChambresDuBatiment()));
        }
    }

    private void refreshAffectationTable() {
        if (affectationTable != null) {
            affectationTable.setItems(FXCollections.observableArrayList(getEtudiantsLogesDuBatiment()));
        }
    }

    private void refreshAffectationChoices() {
        if (etudiantCombo != null) {
            etudiantCombo.setItems(FXCollections.observableArrayList(gestionEtudiant.getAllEtudiants()));
        }
        if (chambreCombo != null) {
            chambreCombo.setItems(FXCollections.observableArrayList(
                    getChambresDuBatiment().stream().filter(Chambre::isLibre).toList()));
        }
    }

    private void resetChambreForm() {
        selectedChambre = null;
        if (chambreIdField != null) {
            chambreIdField.clear();
            chambreIdField.setDisable(false);
        }
        if (chambreNumeroSpinner != null) {
            chambreNumeroSpinner.getValueFactory().setValue(1);
        }
        if (chambreEtageSpinner != null) {
            chambreEtageSpinner.getValueFactory().setValue(1);
        }
        if (chambreCapaciteSpinner != null) {
            chambreCapaciteSpinner.getValueFactory().setValue(1);
        }
        if (chambreTypeCombo != null) {
            chambreTypeCombo.setValue(null);
        }
        if (saveChambreButton != null) {
            saveChambreButton.setText("Enregistrer");
        }
        if (chambresTable != null) {
            chambresTable.getSelectionModel().clearSelection();
        }
    }

    private List<Chambre> getChambresDuBatiment() {
        return gestionChambre.getAllChambres().stream()
                .filter(c -> batimentId.equals(c.getBatimentId()))
                .toList();
    }

    private List<Etudiant> getEtudiantsLogesDuBatiment() {
        return gestionEtudiant.getAllEtudiants().stream()
                .filter(Etudiant::hasRoom)
                .filter(e -> {
                    Chambre chambre = gestionChambre.getChambre(e.getChambreId());
                    return chambre != null && batimentId.equals(chambre.getBatimentId());
                })
                .toList();
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void logout() {
        gestionUtilisateur.logout();
        Scene scene = new Scene(new LoginController(gestionUtilisateur, primaryStage).createLoginView(), 1100, 680);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Campus Room Manager - Login");
    }
}
