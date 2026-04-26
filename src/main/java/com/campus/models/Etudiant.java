package com.campus.models;

public class Etudiant extends Utilisateur {
    private String codePermanent;
    private String specialite;
    private String chambreId; // ID de la chambre affectée
    private String dateAffectation;
    private boolean chambrePayee;
    private String datePaiementChambre;
    private String reclamation;

    public Etudiant(String id, String nom, String prenom, String email,
            String motDePasse, String codePermanent, String specialite) {
        super(id, nom, prenom, email, motDePasse, UserRole.ETUDIANT);
        this.codePermanent = codePermanent;
        this.specialite = specialite;
        this.chambreId = null;
        this.dateAffectation = null;
        this.chambrePayee = false;
        this.datePaiementChambre = null;
        this.reclamation = null;
    }

    // Getters and Setters
    public String getCodePermanent() {
        return codePermanent;
    }

    public void setCodePermanent(String codePermanent) {
        this.codePermanent = codePermanent;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getChambreId() {
        return chambreId;
    }

    public void setChambreId(String chambreId) {
        this.chambreId = chambreId;
    }

    public String getDateAffectation() {
        return dateAffectation;
    }

    public void setDateAffectation(String dateAffectation) {
        this.dateAffectation = dateAffectation;
    }

    public boolean isChambrePayee() {
        return chambrePayee;
    }

    public void setChambrePayee(boolean chambrePayee) {
        this.chambrePayee = chambrePayee;
    }

    public String getDatePaiementChambre() {
        return datePaiementChambre;
    }

    public void setDatePaiementChambre(String datePaiementChambre) {
        this.datePaiementChambre = datePaiementChambre;
    }

    public String getReclamation() {
        return reclamation;
    }

    public void setReclamation(String reclamation) {
        this.reclamation = reclamation;
    }

    public boolean hasRoom() {
        return chambreId != null && !chambreId.isEmpty();
    }

    public void payerChambre(String datePaiement) {
        this.chambrePayee = true;
        this.datePaiementChambre = datePaiement;
    }

    public void annulerPaiementChambre() {
        this.chambrePayee = false;
        this.datePaiementChambre = null;
    }

    public void faireReclamation(String message) {
        this.reclamation = message;
    }

    @Override
    public String toString() {
        return getNomComplet() + " (" + codePermanent + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Etudiant))
            return false;
        return id.equals(((Etudiant) obj).id);
    }
}
