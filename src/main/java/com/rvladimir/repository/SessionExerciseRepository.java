package com.rvladimir.repository;

import com.rvladimir.domain.SessionExercise;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for SessionExercise entity.
 */
@Repository
public interface SessionExerciseRepository extends JpaRepository<SessionExercise, Long> {
    /**
     * Finds all session exercises by training session ID.
     * @param trainingSessionId the training session ID
     * @return list of session exercises
     */
    List<SessionExercise> findByTrainingSessionId(Long trainingSessionId);
}
