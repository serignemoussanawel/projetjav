package com.campus.database;

import com.campus.models.Batiment;
import com.campus.models.Chambre;
import com.campus.models.ChefBatiment;
import com.campus.models.Etudiant;
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
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/gestion_chambres?createDatabaseIfNotExist=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "0405Dieng@";

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
        dropLegacyChambreCodeColumn();
        createEtudiantsTable();
        ensureEtudiantsFinanceAndReclamationColumns();
        migrateEtudiantsMatriculeToCodePermanent();
        synchronizeEtudiantsFromUtilisateurs();
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
                    numero INT NOT NULL,
                    batiment_id VARCHAR(50) NOT NULL,
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

    private static void dropLegacyChambreCodeColumn() throws SQLException {
        String checkSql = """
                SELECT COUNT(*) AS total
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = 'chambres'
                  AND column_name = 'code'
                """;

        try (Connection connection = getConnection();
                PreparedStatement checkStatement = connection.prepareStatement(checkSql);
                ResultSet resultSet = checkStatement.executeQuery()) {
            if (resultSet.next() && resultSet.getInt("total") > 0) {
                try (Statement alterStatement = connection.createStatement()) {
                    alterStatement.execute("ALTER TABLE chambres DROP COLUMN code");
                }
            }
        }
    }

    private static void createEtudiantsTable() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS etudiants (
                    id VARCHAR(50) PRIMARY KEY,
                    nom VARCHAR(100) NOT NULL,
                    prenom VARCHAR(100) NOT NULL,
                    email VARCHAR(150) NOT NULL UNIQUE,
                    code_permanent VARCHAR(100) NOT NULL UNIQUE,
                    specialite VARCHAR(150) NOT NULL,
                    chambre_id VARCHAR(50) NULL,
                    date_affectation VARCHAR(50) NULL,
                    chambre_payee BOOLEAN NOT NULL DEFAULT FALSE,
                    date_paiement_chambre VARCHAR(50) NULL,
                    reclamation TEXT NULL,
                    actif BOOLEAN NOT NULL DEFAULT TRUE
                )
                """;

        try (Connection connection = getConnection();
                Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private static void ensureEtudiantsFinanceAndReclamationColumns() throws SQLException {
        addColumnIfMissing("etudiants", "chambre_payee", "BOOLEAN NOT NULL DEFAULT FALSE");
        addColumnIfMissing("etudiants", "date_paiement_chambre", "VARCHAR(50) NULL");
        addColumnIfMissing("etudiants", "reclamation", "TEXT NULL");
    }

    private static void migrateEtudiantsMatriculeToCodePermanent() throws SQLException {
        String checkOldColumnSql = """
                SELECT COUNT(*) AS total
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = 'etudiants'
                  AND column_name = 'numero_matricule'
                """;

        String checkNewColumnSql = """
                SELECT COUNT(*) AS total
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = 'etudiants'
                  AND column_name = 'code_permanent'
                """;

        try (Connection connection = getConnection();
                PreparedStatement oldColumnStatement = connection.prepareStatement(checkOldColumnSql);
                PreparedStatement newColumnStatement = connection.prepareStatement(checkNewColumnSql);
                ResultSet oldResult = oldColumnStatement.executeQuery();
                ResultSet newResult = newColumnStatement.executeQuery()) {

            boolean hasOldColumn = oldResult.next() && oldResult.getInt("total") > 0;
            boolean hasNewColumn = newResult.next() && newResult.getInt("total") > 0;

            if (hasOldColumn && !hasNewColumn) {
                try (Statement alterStatement = connection.createStatement()) {
                    alterStatement.execute(
                            "ALTER TABLE etudiants CHANGE numero_matricule code_permanent VARCHAR(100) NOT NULL UNIQUE");
                }
            }
        }
    }

    private static void synchronizeEtudiantsFromUtilisateurs() throws SQLException {
        String sql = """
                SELECT u.id, u.nom, u.prenom, u.email, u.actif
                FROM utilisateurs u
                LEFT JOIN etudiants e ON e.email = u.email
                WHERE u.role = ? AND e.id IS NULL
                """;

        List<Etudiant> missingEtudiants = new ArrayList<>();

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, UserRole.ETUDIANT.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String userId = resultSet.getString("id");
                    Etudiant etudiant = new Etudiant(
                            "ETU_" + userId,
                            resultSet.getString("nom"),
                            resultSet.getString("prenom"),
                            resultSet.getString("email"),
                            resultSet.getString("mot_de_passe"),
                            "AUTO-" + userId,
                            "Non renseignée");
                    etudiant.setActif(resultSet.getBoolean("actif"));
                    missingEtudiants.add(etudiant);
                }
            }
        }

        if (missingEtudiants.isEmpty()) {
            return;
        }

        String insertSql = """
                INSERT INTO etudiants (id, nom, prenom, email, code_permanent, specialite, chambre_id, date_affectation,
                chambre_payee, date_paiement_chambre, reclamation, actif)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(insertSql)) {
            for (Etudiant etudiant : missingEtudiants) {
                bindEtudiant(statement, etudiant);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    public static void bindUtilisateur(PreparedStatement statement, Utilisateur utilisateur) throws SQLException {
        String batimentId = null;
        if (utilisateur instanceof ChefBatiment) {
            batimentId = ((ChefBatiment) utilisateur).getBatimentId();
        }

        statement.setString(1, utilisateur.getId());
        statement.setString(2, utilisateur.getNom());
        statement.setString(3, utilisateur.getPrenom());
        statement.setString(4, utilisateur.getEmail());
        statement.setString(5, utilisateur.getMotDePasse());
        statement.setString(6, utilisateur.getRole().name());
        statement.setString(7, batimentId);
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
        statement.setInt(2, chambre.getNumero());
        statement.setString(3, chambre.getBatimentId());
        statement.setInt(4, chambre.getCapacite());
        statement.setString(5, chambre.getEtat());
        statement.setString(6, chambre.getEtudiantId());
        statement.setString(7, chambre.getType());
    }

    public static void bindEtudiant(PreparedStatement statement, Etudiant etudiant) throws SQLException {
        statement.setString(1, etudiant.getId());
        statement.setString(2, etudiant.getNom());
        statement.setString(3, etudiant.getPrenom());
        statement.setString(4, etudiant.getEmail());
        statement.setString(5, etudiant.getCodePermanent());
        statement.setString(6, etudiant.getSpecialite());
        statement.setString(7, etudiant.getChambreId());
        statement.setString(8, etudiant.getDateAffectation());
        statement.setBoolean(9, etudiant.isChambrePayee());
        statement.setString(10, etudiant.getDatePaiementChambre());
        statement.setString(11, etudiant.getReclamation());
        statement.setBoolean(12, etudiant.isActif());
    }

    private static void addColumnIfMissing(String tableName, String columnName, String columnDefinition)
            throws SQLException {
        String checkSql = """
                SELECT COUNT(*) AS total
                FROM information_schema.columns
                WHERE table_schema = DATABASE()
                  AND table_name = ?
                  AND column_name = ?
                """;

        try (Connection connection = getConnection();
                PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
            checkStatement.setString(1, tableName);
            checkStatement.setString(2, columnName);

            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt("total") == 0) {
                    try (Statement alterStatement = connection.createStatement()) {
                        alterStatement.execute(
                                "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition);
                    }
                }
            }
        }
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
