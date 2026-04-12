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
        Etudiant e1 = creerEtudiant("Dupont", "Jean", "jean.dupont@univ.fr", "MAT001", "Informatique");
        e1.setChambreId("C1");
        e1.setDateAffectation(LocalDate.now().minusDays(30).toString());

        Etudiant e2 = creerEtudiant("Martin", "Marie", "marie.martin@univ.fr", "MAT002", "Mathématiques");
        e2.setChambreId("C2");
        e2.setDateAffectation(LocalDate.now().minusDays(20).toString());

        creerEtudiant("Bernard", "Pierre", "pierre.bernard@univ.fr", "MAT003", "Physique");

        Etudiant e4 = creerEtudiant("Thomas", "Sophie", "sophie.thomas@univ.fr", "MAT004", "Chimie");
        e4.setChambreId("C6");
        e4.setDateAffectation(LocalDate.now().minusDays(15).toString());

        creerEtudiant("Garcia", "Carlos", "carlos.garcia@univ.fr", "MAT005", "Informatique");
    }

    public Etudiant creerEtudiant(String nom, String prenom, String email, String numeroMatricule, String specialite) {
        String id = "E" + nextId++;
        Etudiant etudiant = new Etudiant(id, nom, prenom, email, numeroMatricule, specialite);
        addEtudiant(etudiant);
        return etudiant;
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
