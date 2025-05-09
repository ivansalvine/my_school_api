package com.souldev.security.repositories;

import com.souldev.security.entities.Inscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscriptionRepository extends JpaRepository<Inscription, String> {
    boolean existsByEleveIdAndAnneeScolaireId(String eleveId, String anneeScolaireId);
    List<Inscription> findByEleveId(String eleveId);
}
