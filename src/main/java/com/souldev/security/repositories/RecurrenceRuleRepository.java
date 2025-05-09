package com.souldev.security.repositories;

import com.souldev.security.entities.RecurrenceRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurrenceRuleRepository extends JpaRepository<RecurrenceRule, String> {
}