package com.rvladimir.repository;

import com.rvladimir.domain.TrainingSession;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

@Repository
public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {
}
