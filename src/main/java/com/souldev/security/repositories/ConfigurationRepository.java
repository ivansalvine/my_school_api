package com.souldev.security.repositories;


import com.souldev.security.entities.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfigurationRepository extends JpaRepository<Configuration, String> {
    Optional<Configuration> findByKey(String key);
}