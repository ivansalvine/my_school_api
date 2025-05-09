package com.souldev.security.payload.request;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;

public class EcoleUpdateDTO {
    private String nom;
    private String adresse;

    @Email
    private String email;
    private String domaineName;

    private MultipartFile logo;
    private boolean deleteLogo;
    private String telephone;

    // Getters et setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDomaineName() { return domaineName; }
    public void setDomaineName(String domaineName) { this.domaineName = domaineName; }

    public MultipartFile getLogo() { return logo; }
    public void setLogo(MultipartFile logo) { this.logo = logo; }

    public boolean isDeleteLogo() { return deleteLogo; }
    public void setDeleteLogo(boolean deleteLogo) { this.deleteLogo = deleteLogo; }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return "EcoleUpdateDTO{" +
                "nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", email='" + email + '\'' +
                ", domaineName='" + domaineName + '\'' +
                ", logo=" + logo +
                ", deleteLogo=" + deleteLogo +
                ", telephone='" + telephone + '\'' +
                '}';
    }
}