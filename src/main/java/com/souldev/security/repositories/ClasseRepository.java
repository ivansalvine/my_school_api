package com.souldev.security.repositories;

import com.souldev.security.entities.AnneeScolaire;
import com.souldev.security.entities.Classe;
import com.souldev.security.entities.Filiere;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClasseRepository extends JpaRepository<Classe, String> {
    Optional<Classe> findById(String id);
    List<Classe> findByAnneeScolaire(String anneeScolaire);

    boolean existsByNomAndFiliereAndAnneeScolaire(String nom, Filiere filiere, AnneeScolaire anneeScolaire);

    List<Classe> findByAnneeScolaire(AnneeScolaire anneeScolaire);

}

