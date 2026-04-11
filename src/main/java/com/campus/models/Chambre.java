package com.campus.models;

public class Chambre {
    private String id;
    private String code;
    private int numero;
    private String batimentId;
    private int etage;
    private int capacite;
    private String etat; // "Libre", "Occupée", "Maintenance"
    private String etudiantId; // ID de l'étudiant occupant
    private String type; // "Simple", "Double", "Suite"

    public Chambre(String id, String code, int numero, String batimentId, 
                  int etage, int capacite, String type) {
        this.id = id;
        this.code = code;
        this.numero = numero;
        this.batimentId = batimentId;
        this.etage = etage;
        this.capacite = capacite;
        this.type = type;
        this.etat = "Libre";
        this.etudiantId = null;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public String getBatimentId() { return batimentId; }
    public void setBatimentId(String batimentId) { this.batimentId = batimentId; }

    public int getEtage() { return etage; }
    public void setEtage(int etage) { this.etage = etage; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }

    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }

    public String getEtudiantId() { return etudiantId; }
    public void setEtudiantId(String etudiantId) { this.etudiantId = etudiantId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

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
        return code + " - " + type + " (" + etat + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Chambre)) return false;
        return id.equals(((Chambre) obj).id);
    }
}
