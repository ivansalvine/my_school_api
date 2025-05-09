package com.souldev.security.payload.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class ClasseDTO {

    private String id; // Non modifiable, utilisé pour la lecture ou la mise à jour

    @NotBlank(message = "Le nom de la classe est requis")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @Min(value = 1, message = "La capacité maximale doit être d'au moins 1")
    private Integer capaciteMaximale;

    @NotNull(message = "L'ID de la filière est requis")
    private String filiereId;

    @NotNull(message = "L'ID de l'année scolaire est requis")
    private String anneeScolaireId;

    private String emploiDuTempsId; // Optionnel, car EmploiDuTemps peut être null
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private  String createdBy;
    private String updatedBy;

    // Constructeurs
    public ClasseDTO() {
    }

    public ClasseDTO(
            String id,
            String nom,
            Integer capaciteMaximale,
            String filiereId,
            String anneeScolaireId,
            String emploiDuTempsId
    ) {
        this.id = id;
        this.nom = nom;
        this.capaciteMaximale = capaciteMaximale;
        this.filiereId = filiereId;
        this.anneeScolaireId = anneeScolaireId;
        this.emploiDuTempsId = emploiDuTempsId;
    }

    public ClasseDTO(
            String id,
            String nom,
            Integer capaciteMaximale,
            String filiereId,
            String anneeScolaireId,
            String emploiDuTempsId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String createdBy,
            String updatedBy
    ) {
        this.id = id;
        this.nom = nom;
        this.capaciteMaximale = capaciteMaximale;
        this.filiereId = filiereId;
        this.anneeScolaireId = anneeScolaireId;
        this.emploiDuTempsId = emploiDuTempsId;
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

    public Integer getCapaciteMaximale() {
        return capaciteMaximale;
    }

    public void setCapaciteMaximale(Integer capaciteMaximale) {
        this.capaciteMaximale = capaciteMaximale;
    }

    public String getFiliereId() {
        return filiereId;
    }

    public void setFiliereId(String filiereId) {
        this.filiereId = filiereId;
    }

    public String getAnneeScolaireId() {
        return anneeScolaireId;
    }

    public void setAnneeScolaireId(String anneeScolaireId) {
        this.anneeScolaireId = anneeScolaireId;
    }

    public String getEmploiDuTempsId() {
        return emploiDuTempsId;
    }

    public void setEmploiDuTempsId(String emploiDuTempsId) {
        this.emploiDuTempsId = emploiDuTempsId;
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