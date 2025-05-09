package com.souldev.security.repositories;

import com.souldev.security.entities.Cycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CycleRepository extends JpaRepository<Cycle, String> {
    Optional<Cycle> findByNomAndEcoleId(String nom, String ecoleId);
    List<Cycle> findByEcoleId(String ecoleId);

    Optional<Cycle> findById(String id);

    // Compter les filières associées à un cycle
    @Query("SELECT COUNT(f) FROM Filiere f WHERE f.cycle.id = :cycleId")
    long countFilieresByCycleId(@Param("cycleId") String cycleId);
}
