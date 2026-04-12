package com.campus.managers;

import com.campus.database.DatabaseManager;
import com.campus.models.Batiment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GestionBatiment {
    private final Map<String, Batiment> batiments;
    private int nextId = 1;

    public GestionBatiment() {
        this.batiments = new LinkedHashMap<>();
        chargerDepuisLaBase();
    }

    public Batiment creerBatiment(String nom, String adresse, int etages) {
        String id = "B" + nextId++;
        Batiment batiment = new Batiment(id, nom, adresse, etages);
        addBatiment(batiment);
        return batiment;
    }

    public void addBatiment(Batiment batiment) {
        String sql = """
                INSERT INTO batiments (id, nom, adresse, etages, description)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            DatabaseManager.bindBatiment(statement, batiment);
            statement.executeUpdate();
            batiments.put(batiment.getId(), batiment);
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible d'ajouter le batiment dans la base de donnees.", e);
        }
    }

    public Batiment getBatiment(String id) {
        return batiments.get(id);
    }

    public void updateBatiment(Batiment batiment) {
        String sql = """
                UPDATE batiments
                SET nom = ?, adresse = ?, etages = ?, description = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, batiment.getNom());
            statement.setString(2, batiment.getAdresse());
            statement.setInt(3, batiment.getEtages());
            statement.setString(4, batiment.getDescription());
            statement.setString(5, batiment.getId());
            statement.executeUpdate();
            batiments.put(batiment.getId(), batiment);
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de mettre a jour le batiment dans la base de donnees.", e);
        }
    }

    public void deleteBatiment(String id) {
        String sql = "DELETE FROM batiments WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.executeUpdate();
            batiments.remove(id);
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de supprimer le batiment de la base de donnees.", e);
        }
    }

    public List<Batiment> getAllBatiments() {
        return new ArrayList<>(batiments.values());
    }

    public boolean batimentExists(String id) {
        return batiments.containsKey(id);
    }

    private void chargerDepuisLaBase() {
        batiments.clear();

        String sql = """
                SELECT id, nom, adresse, etages, description
                FROM batiments
                ORDER BY id
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Batiment batiment = new Batiment(
                        resultSet.getString("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("adresse"),
                        resultSet.getInt("etages"));
                batiment.setDescription(resultSet.getString("description"));
                batiments.put(batiment.getId(), batiment);
            }
            nextId = batiments.keySet().stream()
                    .map(id -> id.replaceAll("\\D+", ""))
                    .filter(value -> !value.isBlank())
                    .mapToInt(Integer::parseInt)
                    .max()
                    .orElse(0) + 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de charger les batiments depuis la base de donnees.", e);
        }
    }
}
