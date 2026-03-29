package com.campus.models;

public class Utilisateur {
    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private UserRole role;
    private String batimentId; // Pour le chef de bâtiment
    private boolean actif;

    public Utilisateur(String id, String nom, String prenom, String email, 
                      String motDePasse, UserRole role) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.batimentId = null;
        this.actif = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public String getBatimentId() { return batimentId; }
    public void setBatimentId(String batimentId) { this.batimentId = batimentId; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    @Override
    public String toString() {
        return getNomComplet() + " (" + role.getDisplayName() + ")";
    }
}
