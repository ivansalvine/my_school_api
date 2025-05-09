package com.souldev.security.payload.request;

import javax.validation.constraints.NotBlank;

public class BulletinGenerateRequestDTO {
    @NotBlank(message = "L’ID du semestre est requis")
    private String semestreId;

    private String classeId; // Optionnel pour génération par classe

    private String commentaire;

    // Getters et setters
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