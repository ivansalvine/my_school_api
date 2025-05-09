package com.souldev.security.payload.response;

import java.time.LocalDateTime;
import java.util.List;

public class EmploiDuTempsResponseDTO {
    private String id;
    private String nom;
    private String semestreId;
    private String classeId;
    private List<CreneauResponseDTO> creneaux;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public EmploiDuTempsResponseDTO(
            String id,
            String nom,
            String semestreId,
            String classeId,
            List<CreneauResponseDTO> creneaux,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String createdBy,
            String updatedBy
    ) {
        this.id = id;
        this.nom = nom;
        this.semestreId = semestreId;
        this.classeId = classeId;
        this.creneaux = creneaux;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getSemestreId() { return semestreId; }
    public void setSemestreId(String semestreId) { this.semestreId = semestreId; }
    public String getClasseId() { return classeId; }
    public void setClasseId(String classeId) { this.classeId = classeId; }
    public List<CreneauResponseDTO> getCreneaux() { return creneaux; }
    public void setCreneaux(List<CreneauResponseDTO> creneaux) { this.creneaux = creneaux; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}