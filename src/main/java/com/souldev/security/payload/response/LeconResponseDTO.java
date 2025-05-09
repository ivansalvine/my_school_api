package com.souldev.security.payload.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeconResponseDTO {

    private String id;
    private String titre;
    private String description;
    private LocalDate datePrevue;
    private Integer ordre;
    private String enseignementId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Constructeurs
    public LeconResponseDTO() {
    }

    public LeconResponseDTO(String id, String titre, String description, LocalDate datePrevue, Integer ordre,
                            String enseignementId, LocalDateTime createdAt, LocalDateTime updatedAt,
                            String createdBy, String updatedBy) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.datePrevue = datePrevue;
        this.ordre = ordre;
        this.enseignementId = enseignementId;
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

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDatePrevue() {
        return datePrevue;
    }

    public void setDatePrevue(LocalDate datePrevue) {
        this.datePrevue = datePrevue;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }

    public String getEnseignementId() {
        return enseignementId;
    }

    public void setEnseignementId(String enseignementId) {
        this.enseignementId = enseignementId;
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