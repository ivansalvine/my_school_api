package com.souldev.security.payload.request;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class MatiereDTO {

    private String id; // Non modifiable, utilisé pour la lecture ou la mise à jour

    @NotBlank(message = "Le nom de la matière est requis")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    private String description;

    @PositiveOrZero(message = "Le coefficient doit être positif ou zéro")
    private Double coefficient;

    private String filiereIds; // Identifiants des filières associées
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Constructeurs
    public MatiereDTO() {
    }

    public MatiereDTO(
            String id,
            String nom,
            String description,
            Double coefficient,
            String filiereIds
    ) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.coefficient = coefficient;
        this.filiereIds = filiereIds;
    }

    public MatiereDTO(
            String id,
            String nom,
            String description,
            Double coefficient,
            String filiereIds,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String createdBy,
            String updatedBy
    ) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.coefficient = coefficient;
        this.filiereIds = filiereIds;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    public String getFiliereIds() {
        return filiereIds;
    }

    public void setFiliereIds(String filiereIds) {
        this.filiereIds = filiereIds;
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
