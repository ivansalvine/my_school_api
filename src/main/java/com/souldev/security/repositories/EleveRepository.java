package com.souldev.security.repositories;

import com.souldev.security.entities.Eleve;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EleveRepository extends JpaRepository<Eleve, String> {

    List<Eleve> findByClasseId(String classeId);
    Optional<Eleve> findByUserId(String userId);
    Optional<Eleve> findByNumeroEtudiant(String numeroEtudiant);
    Optional<Eleve> findByCodeAcces(String codeAcces);
    long countByClasseId(String classeId);
}
