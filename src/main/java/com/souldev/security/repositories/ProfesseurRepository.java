package com.souldev.security.repositories;

import com.souldev.security.entities.Professeur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfesseurRepository extends JpaRepository<Professeur, String> {

    Optional<Professeur> findByUserId(String userId);
}