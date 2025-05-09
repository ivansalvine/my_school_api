package com.souldev.security.payload.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class EcoleCreateDTO {

    @NotBlank(message = "Le nom est requis")
    private String nom;

    @NotBlank(message = "L'adresse est requise")
    private String adresse;

    @NotBlank(message = "Le téléphone est requise")
    private String telephone;

    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est requis")
    private String email;

    @NotBlank(message = "Le nom de domaine est requis")
    private String domaineName;

    private String logo;  // Champ pour le logo optionnel

    // Constructeur
    public EcoleCreateDTO() {}

    public EcoleCreateDTO(
            String nom,
            String adresse,
            String telephone,
            String email,
            String domaineName,
            String logo
    ) {
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
        this.domaineName = domaineName;
        this.logo = logo;
    }

    // Getters et setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDomaineName() { return domaineName; }
    public void setDomaineName(String domaineName) { this.domaineName = domaineName; }

    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return "EcoleCreateDTO{" +
                "nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", domaineName='" + domaineName + '\'' +
                ", logo=" + logo +
                '}';
    }
}