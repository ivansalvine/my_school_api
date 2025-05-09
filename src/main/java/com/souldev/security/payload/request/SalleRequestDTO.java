package com.souldev.security.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SalleRequestDTO {

    @NotBlank(message = "Le nom de la salle est requis")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String nom;

    private Integer capacite;

    private String description;

    @NotBlank(message = "L'ID de l'école est requis")
    private String ecoleId;

    // Constructeurs
    public SalleRequestDTO() {
    }

    public SalleRequestDTO(String nom, Integer capacite, String description, String ecoleId) {
        this.nom = nom;
        this.capacite = capacite;
        this.description = description;
        this.ecoleId = ecoleId;
    }

    // Getters et Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getCapacite() {
        return capacite;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
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
}
