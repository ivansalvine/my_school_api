package com.souldev.security.payload.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class InscriptionResponseDTO {
    private String id;
    private LocalDate dateInscription;
    private String statut;
    private String eleveId;
    private String filiereId;
    private String classeId;
    private String anneeScolaireId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public InscriptionResponseDTO(
            String id,
            LocalDate dateInscription,
            String statut,
            String eleveId,
            String filiereId,
            String classeId,
            String anneeScolaireId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String createdBy,
            String updatedBy
    ) {
        this.id = id;
        this.dateInscription = dateInscription;
        this.statut = statut;
        this.eleveId = eleveId;
        this.filiereId = filiereId;
        this.classeId = classeId;
        this.anneeScolaireId = anneeScolaireId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getEleveId() { return eleveId; }
    public void setEleveId(String eleveId) { this.eleveId = eleveId; }
    public String getFiliereId() { return filiereId; }
    public void setFiliereId(String filiereId) { this.filiereId = filiereId; }
    public String getClasseId() { return classeId; }
    public void setClasseId(String classeId) { this.classeId = classeId; }
    public String getAnneeScolaireId() { return anneeScolaireId; }
    public void setAnneeScolaireId(String anneeScolaireId) { this.anneeScolaireId = anneeScolaireId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}