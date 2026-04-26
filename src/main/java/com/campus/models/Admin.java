package com.campus.models;

public class Admin extends Utilisateur {

    public Admin(String id, String nom, String prenom, String email, String motDePasse) {
        super(id, nom, prenom, email, motDePasse, UserRole.ADMIN);
    }

    // Additional methods specific to Admin can be added here if needed

}