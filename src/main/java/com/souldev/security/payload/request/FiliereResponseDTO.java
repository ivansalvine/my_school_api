package com.souldev.security.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class FiliereResponseDTO {

    private String id;

    @NotBlank(message = "Le nom de la filière est requis")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    private String description;
    private CycleResponseDTO cycleResponseDTO;
    private LocalDateTime cratedAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Constructeurs
    public FiliereResponseDTO() {}

    public FiliereResponseDTO(
            String id,
            String nom,
            String description,
            CycleResponseDTO cycleResponseDTO
    ) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.cycleResponseDTO = cycleResponseDTO;
    }

    public FiliereResponseDTO(
            String id,
            String nom,
            String description,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String createdBy,
            String updatedBy,
            CycleResponseDTO cycleResponseDTO
    ) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.cratedAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.cycleResponseDTO = cycleResponseDTO;
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

    public CycleResponseDTO getCycleResponseDTO() {
        return cycleResponseDTO;
    }

    public void setCycleResponseDTO(CycleResponseDTO cycleResponseDTO) {
        this.cycleResponseDTO = cycleResponseDTO;
    }

    public LocalDateTime getCratedAt() {
        return cratedAt;
    }

    public void setCratedAt(LocalDateTime cratedAt) {
        this.cratedAt = cratedAt;
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
