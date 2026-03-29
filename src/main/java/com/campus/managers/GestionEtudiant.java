package com.campus.managers;

import com.campus.models.*;
import java.time.LocalDate;
import java.util.*;

public class GestionEtudiant {
    private Map<String, Etudiant> etudiants;
    private int nextId = 1;

    public GestionEtudiant() {
        this.etudiants = new LinkedHashMap<>();
        initialiserDonnees();
    }

    private void initialiserDonnees() {
        Etudiant e1 = new Etudiant("E1", "Dupont", "Jean", "jean.dupont@univ.fr", "MAT001", "Informatique");
        e1.setChambreId("C1");
        e1.setDateAffectation(LocalDate.now().minusDays(30).toString());
        addEtudiant(e1);

        Etudiant e2 = new Etudiant("E2", "Martin", "Marie", "marie.martin@univ.fr", "MAT002", "Mathématiques");
        e2.setChambreId("C2");
        e2.setDateAffectation(LocalDate.now().minusDays(20).toString());
        addEtudiant(e2);

        Etudiant e3 = new Etudiant("E3", "Bernard", "Pierre", "pierre.bernard@univ.fr", "MAT003", "Physique");
        addEtudiant(e3);

        Etudiant e4 = new Etudiant("E4", "Thomas", "Sophie", "sophie.thomas@univ.fr", "MAT004", "Chimie");
        e4.setChambreId("C6");
        e4.setDateAffectation(LocalDate.now().minusDays(15).toString());
        addEtudiant(e4);

        Etudiant e5 = new Etudiant("E5", "Garcia", "Carlos", "carlos.garcia@univ.fr", "MAT005", "Informatique");
        addEtudiant(e5);
    }

    public void addEtudiant(Etudiant etudiant) {
        etudiants.put(etudiant.getId(), etudiant);
    }

    public Etudiant getEtudiant(String id) {
        return etudiants.get(id);
    }

    public Etudiant getEtudiantByMatricule(String matricule) {
        return etudiants.values().stream()
            .filter(e -> e.getNumeroMatricule().equals(matricule))
            .findFirst()
            .orElse(null);
    }

    public void updateEtudiant(Etudiant etudiant) {
        etudiants.put(etudiant.getId(), etudiant);
    }

    public void deleteEtudiant(String id) {
        etudiants.remove(id);
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

    public int getNombreTotalEtudiants() {
        return etudiants.size();
    }

    public int getNombreEtudiantsLogis() {
        return (int) etudiants.values().stream().filter(Etudiant::hasRoom).count();
    }
}
