package com.souldev.security.payload.request;

import javax.validation.constraints.NotBlank;

public class EnseignementRequestDTO {

    @NotBlank(message = "L'ID du cours est requis")
    private String coursId;

    @NotBlank(message = "L'ID de la classe est requis")
    private String classeId;

    @NotBlank(message = "L'ID du professeur est requis")
    private String professeurId;

    // Constructeurs
    public EnseignementRequestDTO() {
    }

    public EnseignementRequestDTO(String coursId, String classeId, String professeurId) {
        this.coursId = coursId;
        this.classeId = classeId;
        this.professeurId = professeurId;
    }

    // Getters et Setters
    public String getCoursId() {
        return coursId;
    }

    public void setCoursId(String coursId) {
        this.coursId = coursId;
    }

    public String getClasseId() {
        return classeId;
    }

    public void setClasseId(String classeId) {
        this.classeId = classeId;
    }

    public String getProfesseurId() {
        return professeurId;
    }

    public void setProfesseurId(String professeurId) {
        this.professeurId = professeurId;
    }
}