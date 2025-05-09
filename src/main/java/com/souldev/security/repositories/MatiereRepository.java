package com.souldev.security.repositories;

import com.souldev.security.entities.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatiereRepository extends JpaRepository<Matiere, String> {
    List<Matiere> findByFilieresId(String filiereId);
    Optional<Matiere> findByNom(String nom);
    Optional<Matiere> findById(String id);
    List<Matiere> findByTypeMatiereId(String typeMatiereId);
}
