package com.souldev.security.entities;

import com.souldev.security.entities.superclasse.Auditable;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "creneau")
public class Creneau extends Auditable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recurrence_rule_id")
    private RecurrenceRule recurrence;

    @ManyToOne
    @JoinColumn(name = "emploi_du_temps_id", nullable = false)
    private EmploiDuTemps emploiDuTemps;

    @ManyToOne
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;

    @ManyToOne
    @JoinColumn(name = "cours_id")
    private Cours cours;

    @ManyToOne
    @JoinColumn(name = "devoir_id")
    private Devoir devoir;

    // Constructeurs
    public Creneau() {
    }

    public Creneau(LocalDateTime dateDebut, LocalDateTime dateFin, RecurrenceRule recurrence,
                   EmploiDuTemps emploiDuTemps, Salle salle, Cours cours, Devoir devoir) {
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.recurrence = recurrence;
        this.emploiDuTemps = emploiDuTemps;
        this.salle = salle;
        this.cours = cours;
        this.devoir = devoir;
        validateEvent();
    }

    // Validation pour s'assurer qu'un seul événement (Cours ou Devoir) est défini
    private void validateEvent() {
        if ((cours == null && devoir == null) || (cours != null && devoir != null)) {
            throw new IllegalStateException("Un créneau doit être associé à exactement un Cours ou un Devoir");
        }
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public RecurrenceRule getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(RecurrenceRule recurrence) {
        this.recurrence = recurrence;
    }

    public EmploiDuTemps getEmploiDuTemps() {
        return emploiDuTemps;
    }

    public void setEmploiDuTemps(EmploiDuTemps emploiDuTemps) {
        this.emploiDuTemps = emploiDuTemps;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
        validateEvent();
    }

    public Devoir getDevoir() {
        return devoir;
    }

    public void setDevoir(Devoir devoir) {
        this.devoir = devoir;
        validateEvent();
    }
}