package com.souldev.security.payload.response;

import com.souldev.security.entities.RecurrenceRule;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

public class CreneauResponseDTO {

    private String id;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private RecurrenceRuleDTO recurrenceRule;
    private String emploiDuTempsId;
    private String salleId;
    private String coursId;
    private String devoirId;

    public CreneauResponseDTO() {
    }

    public CreneauResponseDTO(String id, LocalDateTime dateDebut, LocalDateTime dateFin,
                              RecurrenceRuleDTO recurrenceRule, String emploiDuTempsId,
                              String salleId, String coursId, String devoirId) {
        this.id = id;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.recurrenceRule = recurrenceRule;
        this.emploiDuTempsId = emploiDuTempsId;
        this.salleId = salleId;
        this.coursId = coursId;
        this.devoirId = devoirId;
    }

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

    public RecurrenceRuleDTO getRecurrenceRule() {
        return recurrenceRule;
    }

    public void setRecurrenceRule(RecurrenceRuleDTO recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
    }

    public String getEmploiDuTempsId() {
        return emploiDuTempsId;
    }

    public void setEmploiDuTempsId(String emploiDuTempsId) {
        this.emploiDuTempsId = emploiDuTempsId;
    }

    public String getSalleId() {
        return salleId;
    }

    public void setSalleId(String salleId) {
        this.salleId = salleId;
    }

    public String getCoursId() {
        return coursId;
    }

    public void setCoursId(String coursId) {
        this.coursId = coursId;
    }

    public String getDevoirId() {
        return devoirId;
    }

    public void setDevoirId(String devoirId) {
        this.devoirId = devoirId;
    }

    public static class RecurrenceRuleDTO {
        private String id;
        private RecurrenceRule.Frequence frequence;
        private Integer intervalle;
        private List<String> jours;
        private LocalDate dateFin;
        private Integer nombreOccurrences;

        public RecurrenceRuleDTO() {
        }

        public RecurrenceRuleDTO(String id, RecurrenceRule.Frequence frequence, Integer intervalle,
                                 List<String> jours, LocalDate dateFin, Integer nombreOccurrences) {
            this.id = id;
            this.frequence = frequence;
            this.intervalle = intervalle;
            this.jours = jours;
            this.dateFin = dateFin;
            this.nombreOccurrences = nombreOccurrences;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public RecurrenceRule.Frequence getFrequence() {
            return frequence;
        }

        public void setFrequence(RecurrenceRule.Frequence frequence) {
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
}