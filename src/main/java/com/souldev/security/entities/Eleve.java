package com.souldev.security.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.souldev.security.entities.superclasse.Auditable;
import com.souldev.security.security.entities.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "eleve")
public class Eleve extends Auditable {

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

    @NotBlank(message = "Le numéro d'étudiant est requis")
    @Size(max = 20, message = "Le numéro d'étudiant ne doit pas dépasser 20 caractères")
    @Column(name = "numero_etudiant", nullable = false, unique = true, length = 20)
    private String numeroEtudiant;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Size(max = 50, message = "Le code d'accès ne doit pas dépasser 50 caractères")
    @Column(name = "code_acces", length = 50)
    private String codeAcces;

    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    @Column(name = "telephone", length = 20)
    private String telephone;

    @ManyToOne
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    @JsonManagedReference
    private User user;

    private String photoUrl;

    @OneToMany(mappedBy = "eleve", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inscription> inscriptions = new ArrayList<>();

    @OneToMany(mappedBy = "eleve", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes = new ArrayList<>();

    @OneToMany(mappedBy = "eleve", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bulletin> bulletins = new ArrayList<>();

    @OneToMany(mappedBy = "eleve", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Diplome> diplomes = new ArrayList<>();
    private String password;

    // Constructeurs
    public Eleve() {
    }

    public Eleve(
            String nom,
            String prenom,
            String numeroEtudiant,
            LocalDate dateNaissance,
            String codeAcces,
            String telephone,
            Classe classe,
            User user
    ) {
        this.nom = nom;
        this.prenom = prenom;
        this.numeroEtudiant = numeroEtudiant;
        this.dateNaissance = dateNaissance;
        this.codeAcces = codeAcces;
        this.telephone = telephone;
        this.classe = classe;
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

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Inscription> getInscriptions() {
        return inscriptions;
    }

    public void setInscriptions(List<Inscription> inscriptions) {
        this.inscriptions = inscriptions;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public List<Bulletin> getBulletins() {
        return bulletins;
    }

    public void setBulletins(List<Bulletin> bulletins) {
        this.bulletins = bulletins;
    }

    public List<Diplome> getDiplomes() {
        return diplomes;
    }

    public void setDiplomes(List<Diplome> diplomes) {
        this.diplomes = diplomes;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}