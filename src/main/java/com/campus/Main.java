package com.campus;

import com.campus.database.DatabaseManager;
import com.campus.managers.*;
import com.campus.ui.controllers.LoginController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.sql.SQLException;

public class Main extends Application {
    private GestionUtilisateur gestionUtilisateur;

    @Override
    public void start(Stage primaryStage) {
        try {
            DatabaseManager.initializeDatabase();
            gestionUtilisateur = new GestionUtilisateur();
        } catch (SQLException | IllegalStateException e) {
            showDatabaseError(e);
            return;
        }

        LoginController loginController = new LoginController(gestionUtilisateur, primaryStage);
        Scene scene = new Scene(loginController.createLoginView(), 600, 450);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setTitle("Campus Room Manager - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void showDatabaseError(Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur Base de Donnees");
        alert.setHeaderText("Connexion MySQL impossible");
        alert.setContentText(
                "Verifiez la base 'gestion_chambres' et les identifiants MySQL.\n"
                        + "Variables possibles: CAMPUS_DB_URL, CAMPUS_DB_USER, CAMPUS_DB_PASSWORD.\n\n"
                        + "Cause: " + exception.getMessage());
        alert.showAndWait();
    }
}
