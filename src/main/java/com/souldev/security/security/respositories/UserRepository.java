package com.souldev.security.security.respositories;

import java.util.List;
import java.util.Optional;

import com.souldev.security.security.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    @Modifying
    @Query("UPDATE User u SET u.enabled = true WHERE u.username = :username")
    void enableUser(@Param("username") String username);
    
}
