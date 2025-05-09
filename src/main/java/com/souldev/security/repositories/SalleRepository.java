package com.souldev.security.repositories;

import com.souldev.security.entities.Salle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalleRepository extends JpaRepository<Salle, String> {
}
