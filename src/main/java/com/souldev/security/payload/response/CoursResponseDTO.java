package com.souldev.security.payload.response;

import java.time.LocalDateTime;

public class CoursResponseDTO {

    private String id;
    private String nom;
    private Integer volumeHoraire;
    private Integer creditsEcts;
    private String description;
    private String matiereId;
    private String matiereNom;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Constructeurs
    public CoursResponseDTO() {
    }

    public CoursResponseDTO(
            String id,
            String nom,
            Integer volumeHoraire,
            Integer creditsEcts,
            String description,
            String matiereId,
            String matiereNom
    ) {
        this.id = id;
        this.nom = nom;
        this.volumeHoraire = volumeHoraire;
        this.creditsEcts = creditsEcts;
        this.description = description;
        this.matiereId = matiereId;
        this.matiereNom = matiereNom;
    }

    public CoursResponseDTO(
            String id,
            String nom,
            Integer volumeHoraire,
            Integer creditsEcts,
            String description,
            String matiereId,
            String matiereNom,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String createdBy,
            String updatedBy
    ) {
        this.id = id;
        this.nom = nom;
        this.volumeHoraire = volumeHoraire;
        this.creditsEcts = creditsEcts;
        this.description = description;
        this.matiereId = matiereId;
        this.matiereNom = matiereNom;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getMatiereNom() {
        return matiereNom;
    }

    public void setMatiereNom(String matiereNom) {
        this.matiereNom = matiereNom;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}