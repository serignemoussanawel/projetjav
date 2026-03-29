package com.campus.managers;

import com.campus.models.*;
import java.util.*;

public class GestionUtilisateur {
    private Map<String, Utilisateur> utilisateurs;
    private Utilisateur utilisateurConnecte;

    public GestionUtilisateur() {
        this.utilisateurs = new LinkedHashMap<>();
        this.utilisateurConnecte = null;
        initialiserDonnees();
    }

    private void initialiserDonnees() {
        addUtilisateur(new Utilisateur("U1", "Admin", "System", "admin@univ.fr", "admin123", UserRole.ADMIN));
        
        Utilisateur chef1 = new Utilisateur("U2", "Durand", "Michel", "chef1@univ.fr", "chef123", UserRole.CHEF_BATIMENT);
        chef1.setBatimentId("B1");
        addUtilisateur(chef1);

        Utilisateur chef2 = new Utilisateur("U3", "Leclerc", "Francoise", "chef2@univ.fr", "chef123", UserRole.CHEF_BATIMENT);
        chef2.setBatimentId("B2");
        addUtilisateur(chef2);

        Utilisateur etudiant1 = new Utilisateur("U4", "Dupont", "Jean", "jean@univ.fr", "etud123", UserRole.ETUDIANT);
        addUtilisateur(etudiant1);

        Utilisateur etudiant2 = new Utilisateur("U5", "Martin", "Marie", "marie@univ.fr", "etud123", UserRole.ETUDIANT);
        addUtilisateur(etudiant2);
    }

    public void addUtilisateur(Utilisateur utilisateur) {
        utilisateurs.put(utilisateur.getId(), utilisateur);
    }

    public Utilisateur getUtilisateur(String id) {
        return utilisateurs.get(id);
    }

    public void updateUtilisateur(Utilisateur utilisateur) {
        utilisateurs.put(utilisateur.getId(), utilisateur);
    }

    public void deleteUtilisateur(String id) {
        utilisateurs.remove(id);
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
        for (Utilisateur u : utilisateurs.values()) {
            if (u.getEmail().equals(email) && u.getMotDePasse().equals(motDePasse) && u.isActif()) {
                this.utilisateurConnecte = u;
                return u;
            }
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
            .filter(u -> u.getRole() == UserRole.CHEF_BATIMENT && batimentId.equals(u.getBatimentId()))
            .findFirst()
            .orElse(null);
    }
}
