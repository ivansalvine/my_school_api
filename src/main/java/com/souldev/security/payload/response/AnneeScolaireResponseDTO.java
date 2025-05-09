package com.souldev.security.payload.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AnneeScolaireResponseDTO {

    private String id;
    private String nom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String description;
    private int nombreSemestres;
    private int nombreClasses;
    private int nombreInscriptions;
    private List<String> semestreIds;
    private List<String> classeIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Constructeur
    public AnneeScolaireResponseDTO(String id, String nom, LocalDate dateDebut, LocalDate dateFin, String description,
                                    int nombreSemestres, int nombreClasses, int nombreInscriptions,
                                    List<String> semestreIds, List<String> classeIds,
                                    LocalDateTime createdAt, LocalDateTime updatedAt,
                                    String createdBy, String updatedBy) {
        this.id = id;
        this.nom = nom;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.description = description;
        this.nombreSemestres = nombreSemestres;
        this.nombreClasses = nombreClasses;
        this.nombreInscriptions = nombreInscriptions;
        this.semestreIds = semestreIds;
        this.classeIds = classeIds;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNombreSemestres() {
        return nombreSemestres;
    }

    public void setNombreSemestres(int nombreSemestres) {
        this.nombreSemestres = nombreSemestres;
    }

    public int getNombreClasses() {
        return nombreClasses;
    }

    public void setNombreClasses(int nombreClasses) {
        this.nombreClasses = nombreClasses;
    }

    public int getNombreInscriptions() {
        return nombreInscriptions;
    }

    public void setNombreInscriptions(int nombreInscriptions) {
        this.nombreInscriptions = nombreInscriptions;
    }

    public List<String> getSemestreIds() {
        return semestreIds;
    }

    public void setSemestreIds(List<String> semestreIds) {
        this.semestreIds = semestreIds;
    }

    public List<String> getClasseIds() {
        return classeIds;
    }

    public void setClasseIds(List<String> classeIds) {
        this.classeIds = classeIds;
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