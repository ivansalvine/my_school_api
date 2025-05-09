package com.souldev.security.repositories;

import com.souldev.security.entities.Direction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectionRepository extends JpaRepository<Direction, String> {

    boolean existsByEmail(String email);
    boolean existsByUserId(String userId);
    boolean existsByEcoleId(String ecoleId);
}