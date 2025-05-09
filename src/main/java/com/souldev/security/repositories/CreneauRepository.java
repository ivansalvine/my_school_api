package com.souldev.security.repositories;

import com.souldev.security.entities.Cours;
import com.souldev.security.entities.Creneau;
import com.souldev.security.entities.Salle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CreneauRepository extends JpaRepository<Creneau, String> {

    @Query("SELECT s FROM Salle s WHERE s.id = :id")
    Optional<Salle> findSalleById(String id);

    @Query("SELECT c FROM Cours c WHERE c.id = :id")
    Optional<Cours> findCoursById(String id);

}