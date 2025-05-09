package com.souldev.security.repositories;

import com.souldev.security.entities.Filiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FiliereRepository extends JpaRepository<Filiere, String> {
    Optional<Filiere> findById(String id);

    @Query("SELECT f FROM Filiere f JOIN f.matieres m WHERE m.id IN :matiereIds")
    List<Filiere> findByMatieresIdIn(List<String> matiereIds);
}
