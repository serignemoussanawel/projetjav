package com.campus.models;

import java.util.Objects;

public class Chambre {
    private String id;
    private int numero;
    private String batimentId;
    private int capacite;
    private String etat; // "Libre", "Occupée", "Maintenance"
    private String etudiantId; // ID de l'étudiant occupant
    private String type; // "Simple", "Double", "Suite"

    public Chambre(String id, int numero, String batimentId, int capacite, String type) {
        this.id = id;
        this.numero = numero;
        this.batimentId = batimentId;
        this.capacite = capacite;
        this.type = type;
        this.etat = "Libre";
        this.etudiantId = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getBatimentId() {
        return batimentId;
    }

    public void setBatimentId(String batimentId) {
        this.batimentId = batimentId;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getEtudiantId() {
        return etudiantId;
    }

    public void setEtudiantId(String etudiantId) {
        this.etudiantId = etudiantId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isLibre() {
        return "Libre".equals(etat);
    }

    public boolean isDisponible() {
        return isLibre();
    }

    public boolean isOccupee() {
        return "Occupée".equals(etat);
    }

    public void occuper(String etudiantId) {
        this.etat = "Occupée";
        this.etudiantId = etudiantId;
    }

    public void liberer() {
        this.etat = "Libre";
        this.etudiantId = null;
    }

    @Override
    public String toString() {
        return "Chambre " + numero + " - " + type + " (" + etat + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Chambre)) {
            return false;
        }
        Chambre other = (Chambre) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
