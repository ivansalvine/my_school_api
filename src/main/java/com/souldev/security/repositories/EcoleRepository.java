package com.souldev.security.repositories;

import com.souldev.security.entities.Ecole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EcoleRepository extends JpaRepository<Ecole, String> {
    boolean existsByNom(String nom);
    boolean existsByEmail(String email);
    boolean existsByDomaineName(String dmaineName);
    Optional<Ecole> findByNomAndIdNot(String nom, String id);
    Optional<Ecole> findByDomaineNameAndIdNot(String dmaineName, String id);
    @Query("SELECT e FROM Ecole e WHERE " +
            "LOWER(e.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.adresse) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Ecole> findBySearchCriteria(@Param("search") String search, Pageable pageable);

}