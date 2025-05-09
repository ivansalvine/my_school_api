package com.souldev.security.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public class DevoirRequestDTO {
    @NotBlank(message = "Le nom du devoir est requis")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    private String type;

    @NotNull(message = "La pondération est requise")
    private Double ponderation;

    private LocalDate dateDevoir;

    private LocalTime heureDebut;

    private LocalTime heureFin;

    private String description;

    @NotBlank(message = "L'ID de la classe est requis")
    private String classeId;

    @NotBlank(message = "L'ID du semestre est requis")
    private String semestreId;

    @NotBlank(message = "L'ID de la matière est requis")
    private String matiereId;

    @NotBlank(message = "L'ID du professeur est requis")
    private String professeurId;

    // Constructeurs
    public DevoirRequestDTO() {
    }

    public DevoirRequestDTO(String nom, String type, Double ponderation, LocalDate dateDevoir,
                            LocalTime heureDebut, LocalTime heureFin, String description,
                            String classeId, String semestreId, String matiereId, String professeurId) {
        this.nom = nom;
        this.type = type;
        this.ponderation = ponderation;
        this.dateDevoir = dateDevoir;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.description = description;
        this.classeId = classeId;
        this.semestreId = semestreId;
        this.matiereId = matiereId;
        this.professeurId = professeurId;
    }

    // Getters et Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getPonderation() {
        return ponderation;
    }

    public void setPonderation(Double ponderation) {
        this.ponderation = ponderation;
    }

    public LocalDate getDateDevoir() {
        return dateDevoir;
    }

    public void setDateDevoir(LocalDate dateDevoir) {
        this.dateDevoir = dateDevoir;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClasseId() {
        return classeId;
    }

    public void setClasseId(String classeId) {
        this.classeId = classeId;
    }

    public String getSemestreId() {
        return semestreId;
    }

    public void setSemestreId(String semestreId) {
        this.semestreId = semestreId;
    }

    public String getMatiereId() {
        return matiereId;
    }

    public void setMatiereId(String matiereId) {
        this.matiereId = matiereId;
    }

    public String getProfesseurId() {
        return professeurId;
    }

    public void setProfesseurId(String professeurId) {
        this.professeurId = professeurId;
    }
}