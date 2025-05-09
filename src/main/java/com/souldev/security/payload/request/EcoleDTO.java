package com.souldev.security.payload.request;

import java.time.LocalDateTime;

public class EcoleDTO {
    private String id;
    private String nom;
    private String adresse;
    private String telephone;
    private String email;
    private String domaineName;
    private String logoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private String createdBy;
    private String updatedBy;

    public EcoleDTO() {}

    public EcoleDTO(
            String id,
            String nom,
            String adresse,
            String telephone,
            String email,
            String domaineName,
            String logoUrl
    ) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.domaineName = domaineName;
        this.logoUrl = logoUrl;
    }

    public EcoleDTO(
            String id,
            String nom,
            String adresse,
            String telephone,
            String email,
            String domaineName,
            String logoUrl,
            LocalDateTime createdAt,
            LocalDateTime updateAt,
            String createdBy,
            String updatedBy
    ) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.domaineName = domaineName;
        this.logoUrl = logoUrl;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    // Getters et setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDomaineName() { return domaineName; }
    public void setDomaineName(String domaineName) { this.domaineName = domaineName; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
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
