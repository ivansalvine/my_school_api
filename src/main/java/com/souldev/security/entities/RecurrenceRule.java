package com.souldev.security.entities;

import com.souldev.security.entities.superclasse.Auditable;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "recurrence_rule")
public class RecurrenceRule extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @NotNull(message = "La fréquence est requise")
    @Enumerated(EnumType.STRING)
    @Column(name = "frequence", nullable = false)
    private Frequence frequence; // DAILY, WEEKLY, MONTHLY

    @Column(name = "intervalle")
    private Integer intervalle; // Ex. : 1 pour chaque semaine, 2 pour toutes les 2 semaines

    @ElementCollection
    @CollectionTable(name = "recurrence_rule_jours", joinColumns = @JoinColumn(name = "recurrence_rule_id"))
    @Column(name = "jour")
    private List<String> jours; // Ex. : ["MONDAY", "WEDNESDAY"] pour les lundis et mercredis

    @Column(name = "date_fin")
    private LocalDate dateFin; // Date de fin de la récurrence

    @Column(name = "nombre_occurrences")
    private Integer nombreOccurrences; // Nombre maximum d’occurrences (optionnel)

    // Enum pour la fréquence
    public enum Frequence {
        DAILY, WEEKLY, MONTHLY
    }

    // Constructeurs
    public RecurrenceRule() {
    }

    public RecurrenceRule(Frequence frequence, Integer intervalle, List<String> jours,
                          LocalDate dateFin, Integer nombreOccurrences) {
        this.frequence = frequence;
        this.intervalle = intervalle;
        this.jours = jours;
        this.dateFin = dateFin;
        this.nombreOccurrences = nombreOccurrences;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Frequence getFrequence() {
        return frequence;
    }

    public void setFrequence(Frequence frequence) {
        this.frequence = frequence;
    }

    public Integer getIntervalle() {
        return intervalle;
    }

    public void setIntervalle(Integer intervalle) {
        this.intervalle = intervalle;
    }

    public List<String> getJours() {
        return jours;
    }

    public void setJours(List<String> jours) {
        this.jours = jours;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Integer getNombreOccurrences() {
        return nombreOccurrences;
    }

    public void setNombreOccurrences(Integer nombreOccurrences) {
        this.nombreOccurrences = nombreOccurrences;
    }
}