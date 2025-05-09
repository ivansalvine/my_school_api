package com.souldev.security.payload.request;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class SemestreRequestDTO {

    @NotBlank(message = "Le nom du semestre est requis")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String nom;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDebut;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFin;
    @NotBlank(message = "L'ID de l'année scolaire est requis")
    private String anneeScolaireId;

    public SemestreRequestDTO() {
    }

    public SemestreRequestDTO(String nom, String anneeScolaireId, LocalDate dateDebut, LocalDate dateFin) {
        this.nom = nom;
        this.anneeScolaireId = anneeScolaireId;
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
}