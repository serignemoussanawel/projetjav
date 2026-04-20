package com.campus.managers;

import com.campus.database.DatabaseManager;
import com.campus.models.Chambre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GestionChambre {
    private final Map<String, Chambre> chambres;
    private int nextId = 1;

    public GestionChambre() {
        this.chambres = new LinkedHashMap<>();
        chargerDepuisLaBase();
    }

    public void addChambre(Chambre chambre) {
        String sql = """
                INSERT INTO chambres (id, code, numero, batiment_id, etage, capacite, etat, etudiant_id, type)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            DatabaseManager.bindChambre(statement, chambre);
            statement.executeUpdate();
            chambres.put(chambre.getId(), chambre);
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible d'ajouter la chambre dans la base de donnees.", e);
        }
    }

    public Chambre getChambre(String id) {
        return chambres.get(id);
    }

    public Chambre getChambreByCode(String code) {
        return chambres.values().stream()
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    public void updateChambre(Chambre chambre) {
        String sql = """
                UPDATE chambres
                SET code = ?, numero = ?, batiment_id = ?, etage = ?, capacite = ?, etat = ?, etudiant_id = ?, type = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, chambre.getCode());
            statement.setInt(2, chambre.getNumero());
            statement.setString(3, chambre.getBatimentId());
            statement.setInt(4, chambre.getEtage());
            statement.setInt(5, chambre.getCapacite());
            statement.setString(6, chambre.getEtat());
            statement.setString(7, chambre.getEtudiantId());
            statement.setString(8, chambre.getType());
            statement.setString(9, chambre.getId());
            statement.executeUpdate();
            chambres.put(chambre.getId(), chambre);
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de modifier la chambre dans la base de donnees.", e);
        }
    }

    public void deleteChambre(String id) {
        String sql = "DELETE FROM chambres WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
            chambres.remove(id);
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de supprimer la chambre de la base de donnees.", e);
        }
    }

    public List<Chambre> getAllChambres() {
        return new ArrayList<>(chambres.values());
    }

    public List<Chambre> getChambresLibres() {
        return chambres.values().stream()
                .filter(Chambre::isLibre)
                .toList();
    }

    public List<Chambre> getChambresByBatiment(String batimentId) {
        return chambres.values().stream()
                .filter(c -> c.getBatimentId().equals(batimentId))
                .toList();
    }

    public List<Chambre> getChambresByEtage(String batimentId, int etage) {
        return chambres.values().stream()
                .filter(c -> c.getBatimentId().equals(batimentId) && c.getEtage() == etage)
                .toList();
    }

    public boolean affecterChambre(String chambreId, String etudiantId) {
        Chambre chambre = getChambre(chambreId);
        if (chambre != null && chambre.isLibre()) {
            chambre.occuper(etudiantId);
            updateChambre(chambre);
            return true;
        }
        return false;
    }

    public boolean libererChambre(String chambreId) {
        Chambre chambre = getChambre(chambreId);
        if (chambre != null && chambre.isOccupee()) {
            chambre.liberer();
            updateChambre(chambre);
            return true;
        }
        return false;
    }

    public int getNombreChambresLibres() {
        return (int) chambres.values().stream().filter(Chambre::isLibre).count();
    }

    public int getNombreChambresOccupees() {
        return (int) chambres.values().stream().filter(Chambre::isOccupee).count();
    }

    public int getNextNumero(String batimentId, int etage) {
        return chambres.values().stream()
                .filter(c -> c.getBatimentId().equals(batimentId) && c.getEtage() == etage)
                .mapToInt(Chambre::getNumero)
                .max()
                .orElse(0) + 1;
    }

    public Chambre creerChambre(String batimentId, int etage, int capacite, String type) {
        int numero = getNextNumero(batimentId, etage);
        String code = generateCode(batimentId, etage, numero);

        String id = "C" + nextId++;

        Chambre chambre = new Chambre(id, code, numero, batimentId, etage, capacite, type);
        addChambre(chambre);

        return chambre;
    }

    public String generateCode(String batimentId, int etage, int numero) {
        return batimentId.toUpperCase() + "-"
                + etage
                + String.format("%02d", numero);
    }

    private void chargerDepuisLaBase() {
        chambres.clear();

        String sql = """
                SELECT id, code, numero, batiment_id, etage, capacite, etat, etudiant_id, type
                FROM chambres
                ORDER BY batiment_id, etage, numero
                """;

        try (Connection connection = DatabaseManager.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Chambre chambre = new Chambre(
                        resultSet.getString("id"),
                        resultSet.getString("code"),
                        resultSet.getInt("numero"),
                        resultSet.getString("batiment_id"),
                        resultSet.getInt("etage"),
                        resultSet.getInt("capacite"),
                        resultSet.getString("type"));
                chambre.setEtat(resultSet.getString("etat"));
                chambre.setEtudiantId(resultSet.getString("etudiant_id"));
                chambres.put(chambre.getId(), chambre);
            }
            nextId = chambres.keySet().stream()
                    .map(id -> id.replaceAll("\\D+", ""))
                    .filter(value -> !value.isBlank())
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElse(0) + 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de charger les chambres depuis la base de donnees.", e);
        }
    }
}
