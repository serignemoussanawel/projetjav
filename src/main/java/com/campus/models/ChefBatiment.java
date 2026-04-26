package com.campus.models;

public class ChefBatiment extends Utilisateur {
    private String batimentId;

    public ChefBatiment(String id, String nom, String prenom, String email,
            String motDePasse, String batimentId) {
        super(id, nom, prenom, email, motDePasse, UserRole.CHEF_BATIMENT);
        this.batimentId = batimentId;
    }

    public String getBatimentId() {
        return batimentId;
    }

    public void setBatimentId(String batimentId) {
        this.batimentId = batimentId;
    }

    // Additional methods specific to ChefBatiment can be added here if needed

}