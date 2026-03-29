package com.campus.managers;

import com.campus.models.*;
import java.time.LocalDate;
import java.util.*;

public class GestionChambre {
    private Map<String, Chambre> chambres;
    private int nextId = 1;

    public GestionChambre() {
        this.chambres = new LinkedHashMap<>();
        initialiserDonnees();
    }

    private void initialiserDonnees() {
        // Chambres du Bâtiment A
        addChambre(new Chambre("C1", "A-101", 101, "B1", 1, 2, "Double"));
        addChambre(new Chambre("C2", "A-102", 102, "B1", 1, 1, "Simple"));
        addChambre(new Chambre("C3", "A-201", 201, "B1", 2, 3, "Suite"));
        addChambre(new Chambre("C4", "A-202", 202, "B1", 2, 2, "Double"));
        addChambre(new Chambre("C5", "A-301", 301, "B1", 3, 1, "Simple"));

        // Chambres du Bâtiment B
        addChambre(new Chambre("C6", "B-101", 101, "B2", 1, 2, "Double"));
        addChambre(new Chambre("C7", "B-102", 102, "B2", 1, 1, "Simple"));
        addChambre(new Chambre("C8", "B-201", 201, "B2", 2, 3, "Suite"));

        // Chambres du Bâtiment C
        addChambre(new Chambre("C9", "C-101", 101, "B3", 1, 2, "Double"));
        addChambre(new Chambre("C10", "C-102", 102, "B3", 1, 1, "Simple"));
    }

    public void addChambre(Chambre chambre) {
        chambres.put(chambre.getId(), chambre);
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
        chambres.put(chambre.getId(), chambre);
    }

    public void deleteChambre(String id) {
        chambres.remove(id);
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

    public String generateCode(String batimentId, int numero) {
        Batiment b = null;
        // This will need to be refactored with proper batiment lookup
        return batimentId + "-" + numero;
    }
}
