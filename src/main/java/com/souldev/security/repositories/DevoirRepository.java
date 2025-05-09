package com.souldev.security.repositories;

import com.souldev.security.entities.Devoir;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DevoirRepository extends JpaRepository<Devoir, String> {

    List<Devoir> findByClasseId(String classeId);
    List<Devoir> findByProfesseurId(String professeurId);
}
