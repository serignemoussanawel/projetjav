package com.campus.managers;

import com.campus.models.*;
import java.util.*;

public class GestionBatiment {
    private Map<String, Batiment> batiments;
    private int nextId = 1;

    public GestionBatiment() {
        this.batiments = new LinkedHashMap<>();
        initialiserDonnees();
    }

    private void initialiserDonnees() {
        creerBatiment("Bâtiment A", "123 Rue de l'Université", 4);
        creerBatiment("Bâtiment B", "124 Rue de l'Université", 5);
        creerBatiment("Bâtiment C", "125 Rue de l'Université", 3);
    }

    public Batiment creerBatiment(String nom, String adresse, int etages) {
        String id = "B" + nextId++;
        Batiment batiment = new Batiment(id, nom, adresse, etages);
        addBatiment(batiment);
        return batiment;
    }

    public void addBatiment(Batiment batiment) {
        batiments.put(batiment.getId(), batiment);
    }

    public Batiment getBatiment(String id) {
        return batiments.get(id);
    }

    public void updateBatiment(Batiment batiment) {
        batiments.put(batiment.getId(), batiment);
    }

    public void deleteBatiment(String id) {
        batiments.remove(id);
    }

    public List<Batiment> getAllBatiments() {
        return new ArrayList<>(batiments.values());
    }

    public boolean batimentExists(String id) {
        return batiments.containsKey(id);
    }
}
