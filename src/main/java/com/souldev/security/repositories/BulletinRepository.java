package com.souldev.security.repositories;

import com.souldev.security.entities.Bulletin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BulletinRepository extends JpaRepository<Bulletin, String> {
    Optional<Bulletin> findByEleveIdAndSemestreId(String eleveId, String semestreId);
}
