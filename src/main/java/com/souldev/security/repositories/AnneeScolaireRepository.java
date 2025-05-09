package com.souldev.security.repositories;

import com.souldev.security.entities.AnneeScolaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnneeScolaireRepository extends JpaRepository<AnneeScolaire, String> {
    Optional<AnneeScolaire> findByNom(String nom);
    boolean existsByNom(String nom);
}