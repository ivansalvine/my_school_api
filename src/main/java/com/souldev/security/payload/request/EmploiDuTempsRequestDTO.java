package com.souldev.security.payload.request;

import javax.validation.constraints.NotBlank;

public class EmploiDuTempsRequestDTO {

    @NotBlank(message = "L’ID du semestre est requis")
    private String semestreId;

    @NotBlank(message = "L’ID de la classe est requis")
    private String classeId;

    // Getters et Setters
    public String getSemestreId() { return semestreId; }
    public void setSemestreId(String semestreId) { this.semestreId = semestreId; }
    public String getClasseId() { return classeId; }
    public void setClasseId(String classeId) { this.classeId = classeId; }
}