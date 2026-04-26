package com.campus.managers;

import com.campus.database.DatabaseManager;
import com.campus.models.Etudiant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GestionEtudiant {
    private final Map<String, Etudiant> etudiants;
    private int nextId = 1;

    public GestionEtudiant() {
        this.etudiants = new LinkedHashMap<>();
        chargerDepuisLaBase();
    }

    public Etudiant creerEtudiant(String nom, String prenom, String email, String codePermanent, String specialite) {
        String id = "E" + nextId++;
        Etudiant etudiant = new Etudiant(id, nom, prenom, email, "etud123", codePermanent, specialite);
        addEtudiant(etudiant);
        return etudiant;
    }

    public void addEtudiant(Etudiant etudiant) {
        String sql = """
                INSERT INTO etudiants (id, nom, prenom, email, code_permanent, specialite, chambre_id, date_affectation,
                chambre_payee, date_paiement_chambre, reclamation, actif)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            DatabaseManager.bindEtudiant(statement, etudiant);
            statement.executeUpdate();
            etudiants.put(etudiant.getId(), etudiant);
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible d'ajouter l'etudiant dans la base de donnees.", e);
        }
    }

    public Etudiant getEtudiant(String id) {
        return etudiants.get(id);
    }

    public Etudiant getEtudiantByCodePermanent(String codePermanent) {
        return etudiants.values().stream()
                .filter(e -> e.getCodePermanent().equals(codePermanent))
                .findFirst()
                .orElse(null);
    }

    public void updateEtudiant(Etudiant etudiant) {
        String sql = """
                UPDATE etudiants
                SET nom = ?, prenom = ?, email = ?, code_permanent = ?, specialite = ?, chambre_id = ?, date_affectation = ?,
                chambre_payee = ?, date_paiement_chambre = ?, reclamation = ?, actif = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, etudiant.getNom());
            statement.setString(2, etudiant.getPrenom());
            statement.setString(3, etudiant.getEmail());
            statement.setString(4, etudiant.getCodePermanent());
            statement.setString(5, etudiant.getSpecialite());
            statement.setString(6, etudiant.getChambreId());
            statement.setString(7, etudiant.getDateAffectation());
            statement.setBoolean(8, etudiant.isChambrePayee());
            statement.setString(9, etudiant.getDatePaiementChambre());
            statement.setString(10, etudiant.getReclamation());
            statement.setBoolean(11, etudiant.isActif());
            statement.setString(12, etudiant.getId());
            statement.executeUpdate();
            etudiants.put(etudiant.getId(), etudiant);
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de modifier l'etudiant dans la base de donnees.", e);
        }
    }

    public void deleteEtudiant(String id) {
        String sql = "DELETE FROM etudiants WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
            etudiants.remove(id);
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de supprimer l'etudiant de la base de donnees.", e);
        }
    }

    public List<Etudiant> getAllEtudiants() {
        return new ArrayList<>(etudiants.values());
    }

    public List<Etudiant> getEtudiantsActifs() {
        return etudiants.values().stream()
                .filter(Etudiant::isActif)
                .toList();
    }

    public List<Etudiant> getEtudiantsAvecChambre() {
        return etudiants.values().stream()
                .filter(Etudiant::hasRoom)
                .toList();
    }

    public List<Etudiant> getEtudiantsSansChambre() {
        return etudiants.values().stream()
                .filter(e -> !e.hasRoom())
                .toList();
    }

    public boolean affecterChambre(String etudiantId, String chambreId, String dateAffectation) {
        Etudiant etudiant = getEtudiant(etudiantId);
        if (etudiant != null) {
            etudiant.setChambreId(chambreId);
            etudiant.setDateAffectation(dateAffectation);
            updateEtudiant(etudiant);
            return true;
        }
        return false;
    }

    public boolean libererChambre(String etudiantId) {
        Etudiant etudiant = getEtudiant(etudiantId);
        if (etudiant != null && etudiant.hasRoom()) {
            etudiant.setChambreId(null);
            etudiant.setDateAffectation(null);
            updateEtudiant(etudiant);
            return true;
        }
        return false;
    }

    public boolean payerChambre(String etudiantId, String datePaiement) {
        Etudiant etudiant = getEtudiant(etudiantId);
        if (etudiant != null && etudiant.hasRoom()) {
            etudiant.payerChambre(datePaiement);
            updateEtudiant(etudiant);
            return true;
        }
        return false;
    }

    public boolean faireReclamation(String etudiantId, String message) {
        Etudiant etudiant = getEtudiant(etudiantId);
        if (etudiant != null && message != null && !message.isBlank()) {
            etudiant.faireReclamation(message.trim());
            updateEtudiant(etudiant);
            return true;
        }
        return false;
    }

    public int getNombreTotalEtudiants() {
        return etudiants.size();
    }

    public int getNombreEtudiantsLogis() {
        return (int) etudiants.values().stream().filter(Etudiant::hasRoom).count();
    }

    private void chargerDepuisLaBase() {
        etudiants.clear();

        String sql = """
                SELECT e.id, e.nom, e.prenom, e.email, u.mot_de_passe, e.code_permanent, e.specialite, e.chambre_id, e.date_affectation,
                e.chambre_payee, e.date_paiement_chambre, e.reclamation, e.actif
                FROM etudiants e
                JOIN utilisateurs u ON e.email = u.email
                ORDER BY e.prenom, e.nom
                """;

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Etudiant etudiant = new Etudiant(
                        resultSet.getString("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("email"),
                        resultSet.getString("mot_de_passe"),
                        resultSet.getString("code_permanent"),
                        resultSet.getString("specialite"));
                etudiant.setChambreId(resultSet.getString("chambre_id"));
                etudiant.setDateAffectation(resultSet.getString("date_affectation"));
                etudiant.setChambrePayee(resultSet.getBoolean("chambre_payee"));
                etudiant.setDatePaiementChambre(resultSet.getString("date_paiement_chambre"));
                etudiant.setReclamation(resultSet.getString("reclamation"));
                etudiant.setActif(resultSet.getBoolean("actif"));
                etudiants.put(etudiant.getId(), etudiant);
            }

            nextId = etudiants.keySet().stream()
                    .map(id -> id.replaceAll("\\D+", ""))
                    .filter(value -> !value.isBlank())
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElse(0) + 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de charger les etudiants depuis la base de donnees.", e);
        }
    }
}
