package com.souldev.security.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

public class CycleResponseDTO {
    private String id;

    @NotBlank(message = "Le nom du cycle est requis")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @Min(value = 1, message = "La durée doit être d'au moins 1 an")
    private int dureeAnnees;

    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private  String createdBy;
    private String updatedBy;

    @NotBlank(message = "L'ID de l'école est requis")
    private String ecoleId;

    // Constructeurs
    public CycleResponseDTO() {}

    public CycleResponseDTO(

            String id,
            String nom,
            int dureeAnnees,
            String description,
            String ecoleId
    ) {
        this.id = id;
        this.nom = nom;
        this.dureeAnnees = dureeAnnees;
        this.description = description;
        this.ecoleId = ecoleId;
    }

    public CycleResponseDTO(
            String id,
            String nom,
            int dureeAnnees,
            String description,
            String ecoleId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String createdBy,
            String updatedBy
    ) {
        this.id = id;
        this.nom = nom;
        this.dureeAnnees = dureeAnnees;
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