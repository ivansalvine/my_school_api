package com.souldev.security.payload.request;

import javax.validation.constraints.NotBlank;

public class BulletinRequestDTO {

    @NotBlank(message = "L’ID de l’élève est requis")
    private String eleveId;

    @NotBlank(message = "L’ID du semestre est requis")
    private String semestreId;

    @NotBlank(message = "L’ID de la classe est requis")
    private String classeId;

    private String commentaire;

    // Constructeurs
    public BulletinRequestDTO() {
    }

    public BulletinRequestDTO(String eleveId, String semestreId, String classeId, String commentaire) {
        this.eleveId = eleveId;
        this.semestreId = semestreId;
        this.classeId = classeId;
        this.commentaire = commentaire;
    }

    // Getters et Setters
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
}