package com.souldev.security.repositories;

import com.souldev.security.entities.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SemestreRepository extends JpaRepository<Semestre, String> {
}
