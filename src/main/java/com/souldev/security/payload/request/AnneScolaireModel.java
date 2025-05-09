package com.souldev.security.payload.request;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class AnneScolaireModel {
    String id;

    @NotBlank(message = "Le nom de l'année scolaire est requis")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    @Pattern(regexp = "\\d{4}-\\d{4}", message = "Le nom doit être au format 'YYYY-YYYY', ex. '2024-2025'")
    private String nom;

    private String description;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDebut;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFin;

    // Constructeurs
    public AnneScolaireModel() {
    }

    public AnneScolaireModel(String nom, String description, LocalDate dateDebut, LocalDate dateFin) {
        this.nom = nom;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    // Getters et Setters
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
