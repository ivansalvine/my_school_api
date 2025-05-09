package com.souldev.security.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.souldev.security.entities.superclasse.Auditable;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cycle")
public class Cycle extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @NotBlank(message = "Le nom du cycle est requis")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    @Column(name = "nom", nullable = false, length = 50)
    private String nom;

    @Min(value = 1, message = "La durée doit être d'au moins 1 an")
    @Column(name = "duree_annees", nullable = false)
    private int dureeAnnees;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ecole_id", nullable = false)
    @JsonBackReference // Empêche la sérialisation de ecole pour éviter la récursion
    private Ecole ecole;

    @OneToMany(mappedBy = "cycle", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Autorise la sérialisation des filières
    private List<Filiere> filieres = new ArrayList<>();

    // Constructeurs, getters et setters (inchangés)
    public Cycle() {
    }

    public Cycle(String nom, int dureeAnnees, String description, Ecole ecole) {
        this.nom = nom;
        this.dureeAnnees = dureeAnnees;
        this.description = description;
        this.ecole = ecole;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public int getDureeAnnees() { return dureeAnnees; }
    public void setDureeAnnees(int dureeAnnees) { this.dureeAnnees = dureeAnnees; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Ecole getEcole() { return ecole; }
    public void setEcole(Ecole ecole) { this.ecole = ecole; }
    public List<Filiere> getFilieres() { return filieres; }
    public void setFilieres(List<Filiere> filieres) { this.filieres = filieres; }
}