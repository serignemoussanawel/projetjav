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
        addBatiment(new Batiment("B1", "Bâtiment A", "123 Rue de l'Université", 4));
        addBatiment(new Batiment("B2", "Bâtiment B", "124 Rue de l'Université", 5));
        addBatiment(new Batiment("B3", "Bâtiment C", "125 Rue de l'Université", 3));
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
