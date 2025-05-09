package com.souldev.security.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SemestreResponseDTO {

    private String id;

    @NotBlank(message = "Le nom du semestre est requis")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String nom;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @NotBlank(message = "L'ID de l'année scolaire est requis")
    private String anneeScolaireId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public SemestreResponseDTO() {
    }

    public SemestreResponseDTO(
            String id,
            String nom,
            String anneeScolaireId,
            LocalDate dateDebut,
            LocalDate dateFin
    ) {
        this.id = id;
        this.nom = nom;
        this.anneeScolaireId = anneeScolaireId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public SemestreResponseDTO(
            String id,
            String nom,
            String anneeScolaireId,
            LocalDate dateDebut,
            LocalDate dateFin,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String createdBy,
            String updatedBy
    ) {
        this.id = id;
        this.nom = nom;
        this.anneeScolaireId = anneeScolaireId;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
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

    public String getAnneeScolaireId() {
        return anneeScolaireId;
    }

    public void setAnneeScolaireId(String anneeScolaireId) {
        this.anneeScolaireId = anneeScolaireId;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
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