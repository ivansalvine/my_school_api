package com.souldev.security.payload.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class NoteRequestDTO {

    @NotNull(message = "La valeur de la note est requise")
    @Min(value = 0, message = "La note ne peut pas être négative")
    @Max(value = 20, message = "La note ne peut pas dépasser 20")
    private Double valeur;

    private String commentaire;

    @NotBlank(message = "L’ID de l’élève est requis")
    private String eleveId;

    @NotBlank(message = "L’ID du devoir est requis")
    private String devoirId;

    private String leconId;

    // Constructeurs
    public NoteRequestDTO() {
    }

    public NoteRequestDTO(Double valeur, String commentaire, String eleveId, String devoirId, String leconId) {
        this.valeur = valeur;
        this.commentaire = commentaire;
        this.eleveId = eleveId;
        this.devoirId = devoirId;
        this.leconId = leconId;
    }

    // Getters et Setters
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
}