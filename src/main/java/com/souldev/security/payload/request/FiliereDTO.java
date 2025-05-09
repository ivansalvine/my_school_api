package com.souldev.security.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class FiliereDTO {
    private String id;

    @NotBlank(message = "Le nom de la filière est requis")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    private String description;

    @NotBlank(message = "L'ID du cycle est requis")
    private String cycleId;

    // Constructeurs
    public FiliereDTO() {}

    public FiliereDTO(String id, String nom, String description, String cycleId) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.cycleId = cycleId;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCycleId() {
        return cycleId;
    }

    public void setCycleId(String cycleId) {
        this.cycleId = cycleId;
    }
}