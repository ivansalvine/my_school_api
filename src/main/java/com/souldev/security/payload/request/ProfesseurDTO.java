package com.souldev.security.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ProfesseurDTO {

    @NotBlank(message = "Le nom est requis")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est requis")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    private String prenom;

    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    private String telephone;

    @NotBlank(message = "Le diplôme est requis")
    @Size(max = 100, message = "Le diplôme ne doit pas dépasser 100 caractères")
    private String diplome;

    @NotBlank(message = "L'année d'expérience est requise")
    @Size(max = 2, message = "L'année d'expérience ne doit pas dépasser 50 caractères")
    private String anneeExperience;

    // Les fichiers image et licence seront envoyés via multipart/form-data
    // Pas besoin d'inclure licenceUrl et imageUrl ici

    // Getters et Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getDiplome() {
        return diplome;
    }

    public void setDiplome(String diplome) {
        this.diplome = diplome;
    }

    public String getAnneeExperience() {
        return anneeExperience;
    }

    public void setAnneeExperience(String anneeExperience) {
        this.anneeExperience = anneeExperience;
    }
}