package com.campus.managers;

import com.campus.models.*;
import java.util.*;

public class GestionChambre {
    private Map<String, Chambre> chambres;
    private int nextId = 1;

    public GestionChambre() {
        this.chambres = new LinkedHashMap<>();
        initialiserDonnees();
    }

    private void initialiserDonnees() {
        // chambres B1
        creerChambre("B1", 1, 2, "Double");
        creerChambre("B1", 1, 1, "Simple");
        creerChambre("B1", 2, 3, "Suite");
        creerChambre("B1", 2, 2, "Double");
        creerChambre("B1", 3, 1, "Simple");
        // chambres B2
        creerChambre("B2", 1, 2, "Double");
        creerChambre("B2", 1, 1, "Simple");
        creerChambre("B2", 2, 3, "Suite");
        // chambres B3
        creerChambre("B3", 1, 2, "Double");
        creerChambre("B3", 1, 1, "Simple");
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
}