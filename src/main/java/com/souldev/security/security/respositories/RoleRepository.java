package com.souldev.security.security.respositories;

import com.souldev.security.security.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository <Role, String> {
    Optional<Role> findByName(String name);

}
