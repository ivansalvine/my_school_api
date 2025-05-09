package com.souldev.security.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.souldev.security.entities.superclasse.Auditable;
import com.souldev.security.security.entities.User;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "direction")
public class Direction  extends Auditable {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prenom", nullable = false)
    private String prenom;

    @ManyToOne
    @JoinColumn(name = "id_ecole", nullable = false)
    private Ecole ecole;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    @JsonManagedReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fonction_id", nullable = false)
    private Fonction fonction;

    private String email;
    private String password;

    private String photoUrl;


    // Constructeurs
    public Direction() {
    }

    public Direction(
            String nom,
            String prenom,
            Fonction fonction,
            Ecole ecole,
            User user,
            String email,
            String password,
            String photoUrl
    ) {
        this.nom = nom;
        this.prenom = prenom;
        this.fonction = fonction;
        this.ecole = ecole;
        this.user = user;
        this.email = email;
        this.password = password;
        this.photoUrl = photoUrl;
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

    public Ecole getEcole() {
        return ecole;
    }

    public void setEcole(Ecole ecole) {
        this.ecole = ecole;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Fonction getFonction() {
        return fonction;
    }

    public void setFonction(Fonction fonction) {
        this.fonction = fonction;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
