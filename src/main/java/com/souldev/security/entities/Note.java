package com.souldev.security.entities;

import com.souldev.security.entities.superclasse.Auditable;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "note")
public class Note extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @NotNull(message = "La valeur de la note est requise")
    @Min(value = 0, message = "La note ne peut pas être négative")
    @Max(value = 20, message = "La note ne peut pas dépasser 20")
    @Column(name = "valeur", nullable = false)
    private Double valeur;

    @Column(name = "commentaire")
    private String commentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eleve_id", nullable = false)
    private Eleve eleve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devoir_id", nullable = false)
    private Devoir devoir;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecon_id")
    private Lecon lecon;

    // Constructeurs
    public Note() {
    }

    public Note(Double valeur, String commentaire, Eleve eleve, Devoir devoir, Lecon lecon) {
        this.valeur = valeur;
        this.commentaire = commentaire;
        this.eleve = eleve;
        this.devoir = devoir;
        this.lecon = lecon;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getValeur() {
        return valeur;
    }

    public void setValeur(Double valeur) {
        this.valeur = valeur;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Eleve getEleve() {
        return eleve;
    }

    public void setEleve(Eleve eleve) {
        this.eleve = eleve;
    }

    public Devoir getDevoir() {
        return devoir;
    }

    public void setDevoir(Devoir devoir) {
        this.devoir = devoir;
    }

    public Lecon getLecon() {
        return lecon;
    }

    public void setLecon(Lecon lecon) {
        this.lecon = lecon;
    }
}