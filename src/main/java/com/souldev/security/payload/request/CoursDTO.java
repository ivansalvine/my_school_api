package com.souldev.security.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

public class CoursDTO {

    @NotBlank(message = "Le nom du cours est requis")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    private Integer volumeHoraire;

    private Integer creditsEcts;

    private String description;

    @NotNull(message = "L'ID de la matière est requis")
    private String matiereId;

    // Getters et Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getVolumeHoraire() {
        return volumeHoraire;
    }

    public void setVolumeHoraire(Integer volumeHoraire) {
        this.volumeHoraire = volumeHoraire;
    }

    public Integer getCreditsEcts() {
        return creditsEcts;
    }

    public void setCreditsEcts(Integer creditsEcts) {
        this.creditsEcts = creditsEcts;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMatiereId() {
        return matiereId;
    }

    public void setMatiereId(String matiereId) {
        this.matiereId = matiereId;
    }
}