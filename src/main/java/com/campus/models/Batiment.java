package com.campus.models;

public class Batiment {
    private String id;
    private String nom;
    private String adresse;
    private int etages;
    private String description;

    public Batiment(String id, String nom, String adresse, int etages) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.etages = etages;
        this.description = "";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public int getEtages() { return etages; }
    public void setEtages(int etages) { this.etages = etages; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return nom + " (" + adresse + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Batiment)) return false;
        return id.equals(((Batiment) obj).id);
    }
}
