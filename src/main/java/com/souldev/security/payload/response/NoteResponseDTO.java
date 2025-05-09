package com.souldev.security.payload.response;

import java.time.LocalDateTime;

public class NoteResponseDTO {

    private String id;
    private Double valeur;
    private String commentaire;
    private String eleveId;
    private String devoirId;
    private String leconId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Constructeurs
    public NoteResponseDTO() {
    }

    public NoteResponseDTO(String id, Double valeur, String commentaire, String eleveId,
                           String devoirId, String leconId, LocalDateTime createdAt,
                           LocalDateTime updatedAt, String createdBy, String updatedBy) {
        this.id = id;
        this.valeur = valeur;
        this.commentaire = commentaire;
        this.eleveId = eleveId;
        this.devoirId = devoirId;
        this.leconId = leconId;
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

    public Double getValeur() {
        return valeur;
    }

    public void setValeur(Double valeur) {
        this.valeur = valeur;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getEleveId() {
        return eleveId;
    }

    public void setEleveId(String eleveId) {
        this.eleveId = eleveId;
    }

    public String getDevoirId() {
        return devoirId;
    }

    public void setDevoirId(String devoirId) {
        this.devoirId = devoirId;
    }

    public String getLeconId() {
        return leconId;
    }

    public void setLeconId(String leconId) {
        this.leconId = leconId;
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