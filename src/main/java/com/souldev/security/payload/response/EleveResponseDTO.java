package com.souldev.security.payload.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EleveResponseDTO {

    private String id;
    private String nom;
    private String prenom;
    private String numeroEtudiant;
    private LocalDate dateNaissance;
    private String codeAcces;
    private String telephone;
    private String classeId;
    private String photoUrl;
    private String userId;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private  String createdBy;
    private String updatedBy;
    private String password;

    // Constructeur
    public EleveResponseDTO(
            String id,
            String nom,
            String prenom,
            String numeroEtudiant,
            LocalDate dateNaissance,
            String codeAcces,
            String telephone,
            String classeId,
            String photoUrl,
            String userId,
            String username,
            String email,
            String password
    ) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.numeroEtudiant = numeroEtudiant;
        this.dateNaissance = dateNaissance;
        this.codeAcces = codeAcces;
        this.telephone = telephone;
        this.classeId = classeId;
        this.photoUrl = photoUrl;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public EleveResponseDTO(
            String id,
            String nom,
            String prenom,
            String numeroEtudiant,
            LocalDate dateNaissance,
            String codeAcces,
            String telephone,
            String classeId,
            String photoUrl,
            String userId,
            String username,
            String email,
            String password,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            String createdBy,
            String updatedBy
    ) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.numeroEtudiant = numeroEtudiant;
        this.dateNaissance = dateNaissance;
        this.codeAcces = codeAcces;
        this.telephone = telephone;
        this.classeId = classeId;
        this.photoUrl = photoUrl;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
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

    public String getNumeroEtudiant() {
        return numeroEtudiant;
    }

    public void setNumeroEtudiant(String numeroEtudiant) {
        this.numeroEtudiant = numeroEtudiant;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getCodeAcces() {
        return codeAcces;
    }

    public void setCodeAcces(String codeAcces) {
        this.codeAcces = codeAcces;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getClasseId() {
        return classeId;
    }

    public void setClasseId(String classeId) {
        this.classeId = classeId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}