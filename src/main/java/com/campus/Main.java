package com.campus;

import com.campus.managers.*;
import com.campus.ui.controllers.LoginController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private GestionUtilisateur gestionUtilisateur;

    @Override
    public void start(Stage primaryStage) {
        // Initialize managers
        gestionUtilisateur = new GestionUtilisateur();

        // Setup login screen
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
}
