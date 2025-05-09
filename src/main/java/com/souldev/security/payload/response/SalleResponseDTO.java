package com.souldev.security.payload.response;

import java.time.LocalDateTime;

public class SalleResponseDTO {

    private String id;
    private String nom;
    private Integer capacite;
    private String description;
    private String ecoleId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Constructeurs
    public SalleResponseDTO() {
    }

    public SalleResponseDTO(
            String id,
            String nom,
            Integer capacite,
            String description,
            String ecoleId
    ) {
        this.id = id;
        this.nom = nom;
        this.capacite = capacite;
        this.description = description;
        this.ecoleId = ecoleId;
    }

    public SalleResponseDTO(
            String id,
            String nom,
            Integer capacite,
            String description,
            String ecoleId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String createdBy,
            String updatedBy
    ) {
        this.id = id;
        this.nom = nom;
        this.capacite = capacite;
        this.description = description;
        this.ecoleId = ecoleId;
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
