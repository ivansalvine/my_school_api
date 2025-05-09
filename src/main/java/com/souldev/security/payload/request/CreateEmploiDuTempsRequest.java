package com.souldev.security.payload.request;

import com.souldev.security.entities.EmploiDuTemps;

import java.time.LocalDateTime;

public class CreateEmploiDuTempsRequest {

    private EmploiDuTemps emploiDuTemps;
    private LocalDateTime debutPeriode;
    private LocalDateTime finPeriode;

    // Constructeurs
    public CreateEmploiDuTempsRequest() {
    }

    public CreateEmploiDuTempsRequest(EmploiDuTemps emploiDuTemps, LocalDateTime debutPeriode, LocalDateTime finPeriode) {
        this.emploiDuTemps = emploiDuTemps;
        this.debutPeriode = debutPeriode;
        this.finPeriode = finPeriode;
    }

    // Getters et Setters
    public EmploiDuTemps getEmploiDuTemps() {
        return emploiDuTemps;
    }

    public void setEmploiDuTemps(EmploiDuTemps emploiDuTemps) {
        this.emploiDuTemps = emploiDuTemps;
    }

    public LocalDateTime getDebutPeriode() {
        return debutPeriode;
    }

    public void setDebutPeriode(LocalDateTime debutPeriode) {
        this.debutPeriode = debutPeriode;
    }

    public LocalDateTime getFinPeriode() {
        return finPeriode;
    }

    public void setFinPeriode(LocalDateTime finPeriode) {
        this.finPeriode = finPeriode;
    }
}