package com.campus.managers;

import com.campus.database.DatabaseManager;
import com.campus.models.Admin;
import com.campus.models.ChefBatiment;
import com.campus.models.UserRole;
import com.campus.models.Utilisateur;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GestionUtilisateur {
    private final Map<String, Utilisateur> utilisateurs;
    private Utilisateur utilisateurConnecte;

    public GestionUtilisateur() {
        this.utilisateurs = new LinkedHashMap<>();
        this.utilisateurConnecte = null;
        chargerDepuisLaBase();
    }

    private void chargerDepuisLaBase() {
        utilisateurs.clear();

        String sql = """
                SELECT id, nom, prenom, email, mot_de_passe, role, batiment_id, actif
                FROM utilisateurs
                ORDER BY prenom, nom
                """;

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Utilisateur utilisateur = mapUtilisateur(resultSet);
                utilisateurs.put(utilisateur.getId(), utilisateur);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de charger les utilisateurs depuis la base de donnees.", e);
        }
    }

    public void addUtilisateur(Utilisateur utilisateur) {
        validateChefBatimentAssignment(utilisateur);

        String sql = """
                INSERT INTO utilisateurs (id, nom, prenom, email, mot_de_passe, role, batiment_id, actif)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            DatabaseManager.bindUtilisateur(statement, utilisateur);
            statement.executeUpdate();
            utilisateurs.put(utilisateur.getId(), utilisateur);
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible d'ajouter l'utilisateur dans la base de donnees.", e);
        }
    }

    public Utilisateur getUtilisateur(String id) {
        return utilisateurs.get(id);
    }

    public void updateUtilisateur(Utilisateur utilisateur) {
        validateChefBatimentAssignment(utilisateur);

        String batimentId = null;
        if (utilisateur instanceof ChefBatiment) {
            batimentId = ((ChefBatiment) utilisateur).getBatimentId();
        }

        String sql = """
                UPDATE utilisateurs
                SET nom = ?, prenom = ?, email = ?, mot_de_passe = ?, role = ?, batiment_id = ?, actif = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, utilisateur.getNom());
            statement.setString(2, utilisateur.getPrenom());
            statement.setString(3, utilisateur.getEmail());
            statement.setString(4, utilisateur.getMotDePasse());
            statement.setString(5, utilisateur.getRole().name());
            statement.setString(6, batimentId);
            statement.setBoolean(7, utilisateur.isActif());
            statement.setString(8, utilisateur.getId());
            statement.executeUpdate();
            utilisateurs.put(utilisateur.getId(), utilisateur);
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de modifier l'utilisateur dans la base de donnees.", e);
        }
    }

    public void deleteUtilisateur(String id) {
        String sql = "DELETE FROM utilisateurs WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
            utilisateurs.remove(id);
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de supprimer l'utilisateur de la base de donnees.", e);
        }
    }

    public List<Utilisateur> getAllUtilisateurs() {
        return new ArrayList<>(utilisateurs.values());
    }

    public List<Utilisateur> getUtilisateursByRole(UserRole role) {
        return utilisateurs.values().stream()
                .filter(u -> u.getRole() == role && u.isActif())
                .toList();
    }

    public Utilisateur authentifier(String email, String motDePasse) {
        String sql = """
                SELECT id, nom, prenom, email, mot_de_passe, role, batiment_id, actif
                FROM utilisateurs
                WHERE email = ? AND mot_de_passe = ? AND actif = TRUE
                LIMIT 1
                """;

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, motDePasse);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Utilisateur utilisateur = mapUtilisateur(resultSet);
                    utilisateurs.put(utilisateur.getId(), utilisateur);
                    this.utilisateurConnecte = utilisateur;
                    return utilisateur;
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible d'authentifier l'utilisateur depuis la base de donnees.", e);
        }

        return null;
    }

    public Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public void setUtilisateurConnecte(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
    }

    public void logout() {
        this.utilisateurConnecte = null;
    }

    public boolean isConnected() {
        return utilisateurConnecte != null;
    }

    public Utilisateur getChefBatiment(String batimentId) {
        return utilisateurs.values().stream()
                .filter(u -> u.getRole() == UserRole.CHEF_BATIMENT && u instanceof ChefBatiment
                        && batimentId.equals(((ChefBatiment) u).getBatimentId()))
                .findFirst()
                .orElse(null);
    }

    private void validateChefBatimentAssignment(Utilisateur utilisateur) {
        if (utilisateur.getRole() != UserRole.CHEF_BATIMENT) {
            return;
        }

        String batimentId = null;
        if (utilisateur instanceof ChefBatiment) {
            batimentId = ((ChefBatiment) utilisateur).getBatimentId();
        }

        if (batimentId == null || batimentId.isBlank()) {
            throw new IllegalArgumentException("Un chef de bâtiment doit être associé à un bâtiment.");
        }

        Utilisateur existingChef = getChefBatiment(batimentId);
        if (existingChef != null && !existingChef.getId().equals(utilisateur.getId())) {
            throw new IllegalArgumentException("Ce bâtiment a déjà un chef de bâtiment.");
        }
    }

    private Utilisateur mapUtilisateur(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("id");
        String nom = resultSet.getString("nom");
        String prenom = resultSet.getString("prenom");
        String email = resultSet.getString("email");
        String motDePasse = resultSet.getString("mot_de_passe");
        UserRole role = UserRole.valueOf(resultSet.getString("role"));
        String batimentId = resultSet.getString("batiment_id");
        boolean actif = resultSet.getBoolean("actif");

        Utilisateur utilisateur;
        switch (role) {
            case ADMIN:
                utilisateur = new Admin(id, nom, prenom, email, motDePasse);
                break;
            case CHEF_BATIMENT:
                utilisateur = new ChefBatiment(id, nom, prenom, email, motDePasse, batimentId);
                break;
            case ETUDIANT:
                // For students, we create a basic Utilisateur since student details are in
                // separate table
                utilisateur = new Utilisateur(id, nom, prenom, email, motDePasse, role);
                break;
            default:
                utilisateur = new Utilisateur(id, nom, prenom, email, motDePasse, role);
                break;
        }

        utilisateur.setActif(actif);
        return utilisateur;
    }
}
