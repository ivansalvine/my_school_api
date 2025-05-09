package com.souldev.security.payload.request;

public class CycleDTO {

    private String nom;
    private int dureeAnnees;
    private String description;
    private String ecoleId;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getDureeAnnees() {
        return dureeAnnees;
    }

    public void setDureeAnnees(int dureeAnnees) {
        this.dureeAnnees = dureeAnnees;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEcoleId() {
        return ecoleId;
    }

    public void setEcoleId(String ecoleId) {
        this.ecoleId = ecoleId;
    }

    @Override
    public String toString() {
        return "CycleDTO{" +
                "nom='" + nom + '\'' +
                ", dureeAnnees=" + dureeAnnees +
                ", description='" + description + '\'' +
                ", ecoleId='" + ecoleId + '\'' +
                '}';
    }
}
