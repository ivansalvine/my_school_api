package com.souldev.security.repositories;

import com.souldev.security.entities.Lecon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeconRepository extends JpaRepository<Lecon, String> {

    List<Lecon> findByEnseignementId(String enseignementId);
}