package com.souldev.security.repositories;

import com.souldev.security.entities.Cours;
import com.souldev.security.entities.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoursRepository extends JpaRepository<Cours, String> {
    List<Cours> findByMatiere(Matiere matiere);
}
