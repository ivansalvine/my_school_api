package com.souldev.security.payload.response;

import java.time.LocalDateTime;

public class DirectionResponseDTO {
    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String ecoleNom; // Nom de l'école pour le frontend
    private String fonctionNom; // Nom de la fonction
    private String username; // Nom d'utilisateur associé
    private String password;
    private String photoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private  String createdBy;
    private String updatedBy;

    // Constructeur
    public DirectionResponseDTO(
            String id,
            String nom,
            String prenom,
            String email,
            String ecoleNom,
            String fonctionNom,
            String username
    ) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.ecoleNom = ecoleNom;
        this.fonctionNom = fonctionNom;
        this.username = username;
    }

    public DirectionResponseDTO(
            String id,
            String nom,
            String prenom,
            String email,
            String ecoleNom,
            String fonctionNom,
            String username,
            String password,
            String photoUrl,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String createdBy,
            String updatedBy
    ) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.ecoleNom = ecoleNom;
        this.fonctionNom = fonctionNom;
        this.username = username;
        this.password = password;
        this.photoUrl = photoUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEcoleNom() {
        return ecoleNom;
    }

    public void setEcoleNom(String ecoleNom) {
        this.ecoleNom = ecoleNom;
    }

    public String getFonctionNom() {
        return fonctionNom;
    }

    public void setFonctionNom(String fonctionNom) {
        this.fonctionNom = fonctionNom;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}