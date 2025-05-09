package com.souldev.security.entities;

import com.souldev.security.entities.superclasse.Auditable;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "enseignement", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cours_id", "classe_id", "professeur_id"})
})
public class Enseignement extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_id", nullable = false)
    private Cours cours;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professeur_id", nullable = false)
    private Professeur professeur;

    @OneToMany(mappedBy = "enseignement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lecon> lecons = new ArrayList<>();

    // Constructeurs
    public Enseignement() {
    }

    public Enseignement(Cours cours, Classe classe, Professeur professeur) {
        this.cours = cours;
        this.classe = classe;
        this.professeur = professeur;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public Professeur getProfesseur() {
        return professeur;
    }

    public void setProfesseur(Professeur professeur) {
        this.professeur = professeur;
    }

    public List<Lecon> getLecons() {
        return lecons;
    }

    public void setLecons(List<Lecon> lecons) {
        this.lecons = lecons;
    }
}