package com.souldev.security.payload.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DevoirResponseDTO {
    private String id;
    private String nom;
    private String type;
    private Double ponderation;
    private LocalDate dateDevoir;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String description;
    private String classeId;
    private String semestreId;
    private String matiereId;
    private String professeurId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Constructeurs
    public DevoirResponseDTO() {
    }

    public DevoirResponseDTO(String id, String nom, String type, Double ponderation,
                             LocalDate dateDevoir, LocalTime heureDebut, LocalTime heureFin,
                             String description, String classeId, String semestreId,
                             String matiereId, String professeurId, LocalDateTime createdAt,
                             LocalDateTime updatedAt, String createdBy, String updatedBy) {
        this.id = id;
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