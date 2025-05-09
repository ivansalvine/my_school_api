package com.souldev.security.repositories;

import com.souldev.security.entities.Classe;
import com.souldev.security.entities.EmploiDuTemps;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmploiDuTempsRepository extends JpaRepository<EmploiDuTemps, String> {
    List<EmploiDuTemps> findByClasse(Classe classe);
}
