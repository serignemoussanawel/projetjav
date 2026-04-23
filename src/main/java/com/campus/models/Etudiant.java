package com.campus.models;

public class Etudiant {
    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String codePermanent;
    private String specialite;
    private String chambreId; // ID de la chambre affectée
    private String dateAffectation;
    private boolean actif;

    public Etudiant(String id, String nom, String prenom, String email, 
                   String codePermanent, String specialite) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.codePermanent = codePermanent;
        this.specialite = specialite;
        this.chambreId = null;
        this.dateAffectation = null;
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

    public String getCodePermanent() { return codePermanent; }
    public void setCodePermanent(String codePermanent) { this.codePermanent = codePermanent; }

    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    public String getChambreId() { return chambreId; }
    public void setChambreId(String chambreId) { this.chambreId = chambreId; }

    public String getDateAffectation() { return dateAffectation; }
    public void setDateAffectation(String dateAffectation) { this.dateAffectation = dateAffectation; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public boolean hasRoom() {
        return chambreId != null && !chambreId.isEmpty();
    }

    @Override
    public String toString() {
        return getNomComplet() + " (" + codePermanent + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Etudiant)) return false;
        return id.equals(((Etudiant) obj).id);
    }
}
