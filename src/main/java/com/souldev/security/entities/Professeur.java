package com.souldev.security.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.souldev.security.entities.superclasse.Auditable;
import com.souldev.security.security.entities.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "professeur")
public class Professeur extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @NotBlank(message = "Le nom est requis")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    @Column(name = "nom", nullable = false, length = 50)
    private String nom;

    @NotBlank(message = "Le prénom est requis")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    @Column(name = "prenom", nullable = false, length = 50)
    private String prenom;

    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    @Column(name = "telephone", length = 20)
    private String telephone;

    @Size(max = 100, message = "Le diplôme ne doit pas dépasser 100 caractères")
    @Column(name = "diplome", length = 100)
    private String diplome;

    @Size(max = 50, message = "L'année d'expérience ne doit pas dépasser 50 caractères")
    @Column(name = "annee_experience", length = 50)
    private String anneeExperience;

    @Size(max = 255, message = "L'URL de la licence ne doit pas dépasser 255 caractères")
    @Column(name = "licence_url", length = 255)
    private String licenceUrl;

    @Size(max = 255, message = "L'URL de l'image ne doit pas dépasser 255 caractères")
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    @JsonManagedReference
    private User user;

    @OneToMany(mappedBy = "professeur")
    private List<Devoir> devoirs = new ArrayList<>();

    @OneToMany(mappedBy = "professeur")
    private List<Enseignement> enseignements = new ArrayList<>();

    // Constructeurs
    public Professeur() {
    }

    public Professeur(String nom, String prenom, String telephone, String diplome, String anneeExperience,
                      String licenceUrl, String imageUrl, User user) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.diplome = diplome;
        this.anneeExperience = anneeExperience;
        this.licenceUrl = licenceUrl;
        this.imageUrl = imageUrl;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Devoir> getDevoirs() {
        return devoirs;
    }

    public void setDevoirs(List<Devoir> devoirs) {
        this.devoirs = devoirs;
    }

    public List<Enseignement> getEnseignements() {
        return enseignements;
    }

    public void setEnseignements(List<Enseignement> enseignements) {
        this.enseignements = enseignements;
    }
}