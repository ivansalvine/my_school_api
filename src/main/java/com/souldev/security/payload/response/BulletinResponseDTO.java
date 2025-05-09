package com.souldev.security.payload.response;

import java.time.LocalDateTime;

public class BulletinResponseDTO {

    private String id;
    private String eleveId;
    private String semestreId;
    private String classeId;
    private String commentaire;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Constructeurs
    public BulletinResponseDTO() {
    }

    public BulletinResponseDTO(String id, String eleveId, String semestreId, String classeId,
                               String commentaire, LocalDateTime createdAt, LocalDateTime updatedAt,
                               String createdBy, String updatedBy) {
        this.id = id;
        this.eleveId = eleveId;
        this.semestreId = semestreId;
        this.classeId = classeId;
        this.commentaire = commentaire;
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

    public String getEleveId() {
        return eleveId;
    }

    public void setEleveId(String eleveId) {
        this.eleveId = eleveId;
    }

    public String getSemestreId() {
        return semestreId;
    }

    public void setSemestreId(String semestreId) {
        this.semestreId = semestreId;
    }

    public String getClasseId() {
        return classeId;
    }

    public void setClasseId(String classeId) {
        this.classeId = classeId;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
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