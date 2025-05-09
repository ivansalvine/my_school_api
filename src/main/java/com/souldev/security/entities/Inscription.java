package com.souldev.security.entities;

import com.souldev.security.entities.superclasse.Auditable;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "inscription", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"eleve_id", "annee_scolaire_id"}) // Un élève ne peut s'inscrire qu'une fois par année scolaire
})
public class Inscription extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "date_inscription", nullable = false)
    private LocalDate dateInscription;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutInscription statut; // Ex. : ADMIS, EN_ATTENTE, REDOUBLANT, SUSPENDU

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eleve_id", nullable = false)
    private Eleve eleve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filiere_id", nullable = false)
    private Filiere filiere;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe; // Ex. : Classe BTS 1 Informatique, Master 2 Finance

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_scolaire_id", nullable = false)
    private AnneeScolaire anneeScolaire;

    // Énumération pour le statut
    public enum StatutInscription {
        ADMIS, EN_ATTENTE, REDOUBLANT, SUSPENDU, ANNULE, ADMIS_APRES_CONCOURS
    }

    // Constructeurs
    public Inscription() {
    }

    public Inscription(
            LocalDate dateInscription,
            StatutInscription statut,
            Eleve eleve,
            Filiere filiere,
            Classe classe,
            AnneeScolaire anneeScolaire
    ) {
        this.dateInscription = dateInscription;
        this.statut = statut;
        this.eleve = eleve;
        this.filiere = filiere;
        this.classe = classe;
        this.anneeScolaire = anneeScolaire;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    public StatutInscription getStatut() {
        return statut;
    }

    public void setStatut(StatutInscription statut) {
        this.statut = statut;
    }

    public Eleve getEleve() {
        return eleve;
    }

    public void setEleve(Eleve eleve) {
        this.eleve = eleve;
    }

    public Filiere getFiliere() {
        return filiere;
    }

    public void setFiliere(Filiere filiere) {
        this.filiere = filiere;
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public AnneeScolaire getAnneeScolaire() {
        return anneeScolaire;
    }

    public void setAnneeScolaire(AnneeScolaire anneeScolaire) {
        this.anneeScolaire = anneeScolaire;
    }
}