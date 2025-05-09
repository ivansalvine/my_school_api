package com.souldev.security.entities;

import com.souldev.security.entities.superclasse.Auditable;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "emploi_du_temps", indexes = {
        @Index(name = "idx_emploi_du_temps_classe", columnList = "classe_id"),
        @Index(name = "idx_emploi_du_temps_semestre", columnList = "semestre_id")
})
public class EmploiDuTemps extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @NotBlank(message = "Le nom de l'emploi du temps est requis")
    @Column(name = "nom", nullable = false)
    private String nom;

    @NotNull(message = "Le semestre est requis")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    @NotNull(message = "La classe est requise")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;

    @OneToMany(mappedBy = "emploiDuTemps", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Creneau> creneaux = new ArrayList<>();

    // Constructeurs
    public EmploiDuTemps() {
    }

    public EmploiDuTemps(String nom, Semestre semestre, Classe classe) {
        this.nom = nom;
        this.semestre = semestre;
        this.classe = classe;
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

    public Semestre getSemestre() {
        return semestre;
    }

    public void setSemestre(Semestre semestre) {
        this.semestre = semestre;
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public List<Creneau> getCreneaux() {
        return creneaux;
    }

    public void setCreneaux(List<Creneau> creneaux) {
        this.creneaux = creneaux;
    }

    // Méthodes utilitaires pour gérer les relations bidirectionnelles
    public void addCreneau(Creneau creneau) {
        creneaux.add(creneau);
        creneau.setEmploiDuTemps(this);
    }

    public void removeCreneau(Creneau creneau) {
        creneaux.remove(creneau);
        creneau.setEmploiDuTemps(null);
    }
}