package com.souldev.security.repositories;

import com.souldev.security.entities.Diplome;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiplomeRepository extends JpaRepository<Diplome, String> {

    List<Diplome> findByEleveId(String eleveId);
    List<Diplome> findByDelivred(boolean delivre);
}
