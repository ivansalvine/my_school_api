package com.souldev.security.payload.request;

import javax.validation.constraints.NotBlank;

public class InscriptionRequestDTO {
    @NotBlank
    private String eleveId;
    @NotBlank
    private String filiereId;
    @NotBlank
    private String classeId;
    @NotBlank
    private String anneeScolaireId;
    @NotBlank
    private String statut;

    // Getters et Setters
    public String getEleveId() { return eleveId; }
    public void setEleveId(String eleveId) { this.eleveId = eleveId; }
    public String getFiliereId() { return filiereId; }
    public void setFiliereId(String filiereId) { this.filiereId = filiereId; }
    public String getClasseId() { return classeId; }
    public void setClasseId(String classeId) { this.classeId = classeId; }
    public String getAnneeScolaireId() { return anneeScolaireId; }
    public void setAnneeScolaireId(String anneeScolaireId) { this.anneeScolaireId = anneeScolaireId; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}