package com.campus.database;

import com.campus.models.Batiment;
import com.campus.models.Chambre;
import com.campus.models.UserRole;
import com.campus.models.Utilisateur;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class DatabaseManager {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/gestion_chambres?createDatabaseIfNotExist=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "admin";
    private static final String DEFAULT_PASSWORD = "1234";

    private DatabaseManager() {
    }

    public static String getUrl() {
        return getEnvOrDefault("CAMPUS_DB_URL", DEFAULT_URL);
    }

    public static String getUser() {
        return getEnvOrDefault("CAMPUS_DB_USER", DEFAULT_USER);
    }

    public static String getPassword() {
        return getEnvOrDefault("CAMPUS_DB_PASSWORD", DEFAULT_PASSWORD);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl(), getUser(), getPassword());
    }

    public static void initializeDatabase() throws SQLException {
        createUtilisateursTable();
        createBatimentsTable();
        createChambresTable();
        seedDefaultUsersIfNeeded();
        seedDefaultBatimentsIfNeeded();
        seedDefaultChambresIfNeeded();
    }

    private static void createUtilisateursTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS utilisateurs (
                    id VARCHAR(50) PRIMARY KEY,
                    nom VARCHAR(100) NOT NULL,
                    prenom VARCHAR(100) NOT NULL,
                    email VARCHAR(150) NOT NULL UNIQUE,
                    mot_de_passe VARCHAR(255) NOT NULL,
                    role VARCHAR(50) NOT NULL,
                    batiment_id VARCHAR(50) NULL,
                    actif BOOLEAN NOT NULL DEFAULT TRUE
                )
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private static void createBatimentsTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS batiments (
                    id VARCHAR(50) PRIMARY KEY,
                    nom VARCHAR(150) NOT NULL,
                    adresse VARCHAR(255) NOT NULL,
                    etages INT NOT NULL,
                    description TEXT NULL
                )
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private static void createChambresTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS chambres (
                    id VARCHAR(50) PRIMARY KEY,
                    code VARCHAR(50) NOT NULL UNIQUE,
                    numero INT NOT NULL,
                    batiment_id VARCHAR(50) NOT NULL,
                    etage INT NOT NULL,
                    capacite INT NOT NULL,
                    etat VARCHAR(50) NOT NULL,
                    etudiant_id VARCHAR(50) NULL,
                    type VARCHAR(50) NOT NULL
                )
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private static void seedDefaultUsersIfNeeded() throws SQLException {
        String countSql = "SELECT COUNT(*) FROM utilisateurs";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(countSql)) {

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return;
            }
        }

        List<Utilisateur> defaults = new ArrayList<>();

        Utilisateur admin = new Utilisateur("U1", "Admin", "System", "admin@univ.fr", "admin123", UserRole.ADMIN);
        defaults.add(admin);

        Utilisateur admin2 = new Utilisateur("U6", "Admin1", "Michel", "admin1@univ.fr", "admin123", UserRole.ADMIN);
        defaults.add(admin2);

        Utilisateur chef1 = new Utilisateur("U2", "Durand", "Michel", "chef1@univ.fr", "chef123", UserRole.CHEF_BATIMENT);
        chef1.setBatimentId("B1");
        defaults.add(chef1);

        Utilisateur chef2 = new Utilisateur("U3", "Leclerc", "Francoise", "chef2@univ.fr", "chef123", UserRole.CHEF_BATIMENT);
        chef2.setBatimentId("B2");
        defaults.add(chef2);

        Utilisateur etudiant1 = new Utilisateur("U4", "Dupont", "Jean", "jean@univ.fr", "etud123", UserRole.ETUDIANT);
        defaults.add(etudiant1);

        Utilisateur etudiant2 = new Utilisateur("U5", "Martin", "Marie", "marie@univ.fr", "etud123", UserRole.ETUDIANT);
        defaults.add(etudiant2);

        String insertSql = """
                INSERT INTO utilisateurs (id, nom, prenom, email, mot_de_passe, role, batiment_id, actif)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(insertSql)) {
            for (Utilisateur utilisateur : defaults) {
                bindUtilisateur(statement, utilisateur);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private static void seedDefaultBatimentsIfNeeded() throws SQLException {
        if (tableHasData("batiments")) {
            return;
        }

        List<Batiment> defaults = new ArrayList<>();

        Batiment batiment1 = new Batiment("B1", "Bâtiment A", "123 Rue de l'Université", 4);
        batiment1.setDescription("Résidence principale du campus");
        defaults.add(batiment1);

        Batiment batiment2 = new Batiment("B2", "Bâtiment B", "124 Rue de l'Université", 5);
        batiment2.setDescription("Résidence mixte");
        defaults.add(batiment2);

        Batiment batiment3 = new Batiment("B3", "Bâtiment C", "125 Rue de l'Université", 3);
        batiment3.setDescription("Résidence étudiants");
        defaults.add(batiment3);

        String insertSql = """
                INSERT INTO batiments (id, nom, adresse, etages, description)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(insertSql)) {
            for (Batiment batiment : defaults) {
                bindBatiment(statement, batiment);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private static void seedDefaultChambresIfNeeded() throws SQLException {
        if (tableHasData("chambres")) {
            return;
        }

        List<Chambre> defaults = new ArrayList<>();
        defaults.add(createChambre("C1", "B1-101", 1, "B1", 1, 2, "Double"));
        defaults.add(createChambre("C2", "B1-102", 2, "B1", 1, 1, "Simple"));
        defaults.add(createChambre("C3", "B1-201", 1, "B1", 2, 3, "Suite"));
        defaults.add(createChambre("C4", "B1-202", 2, "B1", 2, 2, "Double"));
        defaults.add(createChambre("C5", "B1-301", 1, "B1", 3, 1, "Simple"));
        defaults.add(createChambre("C6", "B2-101", 1, "B2", 1, 2, "Double"));
        defaults.add(createChambre("C7", "B2-102", 2, "B2", 1, 1, "Simple"));
        defaults.add(createChambre("C8", "B2-201", 1, "B2", 2, 3, "Suite"));
        defaults.add(createChambre("C9", "B3-101", 1, "B3", 1, 2, "Double"));
        defaults.add(createChambre("C10", "B3-102", 2, "B3", 1, 1, "Simple"));

        String insertSql = """
                INSERT INTO chambres (id, code, numero, batiment_id, etage, capacite, etat, etudiant_id, type)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(insertSql)) {
            for (Chambre chambre : defaults) {
                bindChambre(statement, chambre);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    public static void bindUtilisateur(PreparedStatement statement, Utilisateur utilisateur) throws SQLException {
        statement.setString(1, utilisateur.getId());
        statement.setString(2, utilisateur.getNom());
        statement.setString(3, utilisateur.getPrenom());
        statement.setString(4, utilisateur.getEmail());
        statement.setString(5, utilisateur.getMotDePasse());
        statement.setString(6, utilisateur.getRole().name());
        statement.setString(7, utilisateur.getBatimentId());
        statement.setBoolean(8, utilisateur.isActif());
    }

    public static void bindBatiment(PreparedStatement statement, Batiment batiment) throws SQLException {
        statement.setString(1, batiment.getId());
        statement.setString(2, batiment.getNom());
        statement.setString(3, batiment.getAdresse());
        statement.setInt(4, batiment.getEtages());
        statement.setString(5, batiment.getDescription());
    }

    public static void bindChambre(PreparedStatement statement, Chambre chambre) throws SQLException {
        statement.setString(1, chambre.getId());
        statement.setString(2, chambre.getCode());
        statement.setInt(3, chambre.getNumero());
        statement.setString(4, chambre.getBatimentId());
        statement.setInt(5, chambre.getEtage());
        statement.setInt(6, chambre.getCapacite());
        statement.setString(7, chambre.getEtat());
        statement.setString(8, chambre.getEtudiantId());
        statement.setString(9, chambre.getType());
    }

    private static boolean tableHasData(String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }

    private static Chambre createChambre(
            String id,
            String code,
            int numero,
            String batimentId,
            int etage,
            int capacite,
            String type) {
        return new Chambre(id, code, numero, batimentId, etage, capacite, type);
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
