package com.souldev.security.payload.response;

import java.time.LocalDateTime;

public class ProfesseurResponseDTO {

    private String id;
    private String nom;
    private String prenom;
    private String telephone;
    private String diplome;
    private String anneeExperience;
    private String licenceUrl;
    private String imageUrl;
    private String userId;
    private String username;
    private String email;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Constructeurs
    public ProfesseurResponseDTO() {
    }

    public ProfesseurResponseDTO(
            String id,
            String nom,
            String prenom,
            String telephone,
            String diplome,
            String anneeExperience,
            String licenceUrl,
            String imageUrl,
            String userId,
            String username,
            String email
    ) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.diplome = diplome;
        this.anneeExperience = anneeExperience;
        this.licenceUrl = licenceUrl;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.username = username;
        this.email = email;
    }

    public ProfesseurResponseDTO(
            String id,
            String nom,
            String prenom,
            String telephone,
            String diplome,
            String anneeExperience,
            String licenceUrl,
            String imageUrl,
            String userId,
            String username,
            String email,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String createdBy,
            String updatedBy
    ) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.diplome = diplome;
        this.anneeExperience = anneeExperience;
        this.licenceUrl = licenceUrl;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.username = username;
        this.email = email;
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

    public String getLicenceUrl() {
        return licenceUrl;
    }

    public void setLicenceUrl(String licenceUrl) {
        this.licenceUrl = licenceUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}