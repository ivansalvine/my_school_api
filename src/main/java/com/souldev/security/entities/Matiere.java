package com.souldev.security.entities;

import com.souldev.security.entities.superclasse.Auditable;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matiere")
public class Matiere extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @NotBlank(message = "Le nom de la matière est requis")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "description")
    private String description;

    @PositiveOrZero(message = "Le coefficient doit être positif ou zéro")
    @Column(name = "coefficient")
    private Double coefficient;

    @ManyToOne
    @JoinColumn(name = "type_matiere_id", nullable = false)
    private TypeMatiere typeMatiere;

    @ManyToMany(mappedBy = "matieres")
    private List<Filiere> filieres = new ArrayList<>();

    @OneToMany(mappedBy = "matiere")
    private List<Cours> cours = new ArrayList<>();

    // Constructeurs
    public Matiere() {
    }

    public Matiere(String nom, String description, Double coefficient, TypeMatiere typeMatiere) {
        this.nom = nom;
        this.description = description;
        this.coefficient = coefficient;
        this.typeMatiere = typeMatiere;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    public TypeMatiere getTypeMatiere() {
        return typeMatiere;
    }

    public void setTypeMatiere(TypeMatiere typeMatiere) {
        this.typeMatiere = typeMatiere;
    }

    public List<Filiere> getFilieres() {
        return filieres;
    }

    public void setFilieres(List<Filiere> filieres) {
        this.filieres = filieres;
    }

    public List<Cours> getCours() {
        return cours;
    }

    public void setCours(List<Cours> cours) {
        this.cours = cours;
    }
}