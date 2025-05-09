package com.souldev.security.repositories;

import com.souldev.security.entities.Classe;
import com.souldev.security.entities.Cours;
import com.souldev.security.entities.Enseignement;
import com.souldev.security.entities.Professeur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnseignementRepository extends JpaRepository<Enseignement, String> {
    boolean existsByCoursAndClasseAndProfesseur(Cours cours, Classe classe, Professeur professeur);

    Enseignement findByCoursAndClasseAndProfesseur(Cours cours, Classe classe, Professeur professeur);

}