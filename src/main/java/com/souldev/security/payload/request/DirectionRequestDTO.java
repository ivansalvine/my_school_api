package com.souldev.security.payload.request;

import javax.validation.constraints.NotBlank;

public class DirectionRequestDTO {
    @NotBlank(message = "Le nom est requis")
    private String nom;

    @NotBlank(message = "Le prénom est requis")
    private String prenom;

    @NotBlank(message = "L'ID de l'école est requis")
    private String ecoleId;

    @NotBlank(message = "L'ID de la fonction est requis")
    private String fonctionId;
    // Getters et Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEcoleId() {
        return ecoleId;
    }

    public void setEcoleId(String ecoleId) {
        this.ecoleId = ecoleId;
    }

    public String getFonctionId() {
        return fonctionId;
    }

    public void setFonctionId(String fonctionId) {
        this.fonctionId = fonctionId;
    }

}