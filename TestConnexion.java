import com.campus.database.DatabaseManager;

import java.sql.Connection;

public class TestConnexion {
    public static void main(String[] args) {
        try (Connection conn = DatabaseManager.getConnection()) {
            System.out.println("Connexion réussie !");
            System.out.println("URL: " + DatabaseManager.getUrl());
            DatabaseManager.initializeDatabase();
            System.out.println("Table utilisateurs prête.");
        } catch (Exception e) {
            System.out.println("Erreur de connexion !");
            e.printStackTrace();
        }
    }
}
