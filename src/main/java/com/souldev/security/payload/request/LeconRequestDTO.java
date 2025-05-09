package com.souldev.security.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class LeconRequestDTO {

    @NotBlank(message = "Le titre de la leçon est requis")
    @Size(max = 100, message = "Le titre ne doit pas dépasser 100 caractères")
    private String titre;

    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    private String description;

    private LocalDate datePrevue;

    private Integer ordre;

    @NotBlank(message = "L'ID de l’enseignement est requis")
    private String enseignementId;

    // Constructeurs
    public LeconRequestDTO() {
    }

    public LeconRequestDTO(String titre, String description, LocalDate datePrevue, Integer ordre, String enseignementId) {
        this.titre = titre;
        this.description = description;
        this.datePrevue = datePrevue;
        this.ordre = ordre;
        this.enseignementId = enseignementId;
    }

    // Getters et Setters
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDatePrevue() {
        return datePrevue;
    }

    public void setDatePrevue(LocalDate datePrevue) {
        this.datePrevue = datePrevue;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }

    public String getEnseignementId() {
        return enseignementId;
    }

    public void setEnseignementId(String enseignementId) {
        this.enseignementId = enseignementId;
    }
}