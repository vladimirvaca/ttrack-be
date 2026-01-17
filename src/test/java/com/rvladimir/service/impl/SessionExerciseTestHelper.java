package com.rvladimir.service.impl;

import com.rvladimir.service.dto.CreateSessionExerciseDTO;
import com.rvladimir.service.dto.SessionExerciseDTO;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Helper class for creating test DTOs for SessionExercise related tests.
 */
public final class SessionExerciseTestHelper {

    private SessionExerciseTestHelper() {
        // Utility class
    }

    // Test constants for SessionExercise
    public static final int ROUNDS = 1;
    public static final int SETS = 2;
    public static final int REPETITIONS = 10;
    public static final int SPRINTS = 0;
    public static final int MINUTES = 30;
    public static final double WEIGHT = 50.0;
    public static final double DISTANCE = 100.0;
    public static final int REST_TIME = 60;
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final int EXERCISE_ORDER = 1;
    public static final long EXERCISE_ID = 1L;
    public static final long TRAINING_SESSION_ID = 2L;
    public static final long SESSION_EXERCISE_ID = 1L;
    public static final String UNIT_KG = "kg";

    /**
     * Creates a CreateSessionExerciseDTO with the provided parameters.
     */
    public static CreateSessionExerciseDTO createCreateSessionExerciseDTO(
        int rounds,
        int sets,
        int repetitions,
        int sprints,
        LocalTime duration,
        double weight,
        double distance,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int restTime,
        String status,
        int exerciseOrder,
        long exerciseId,
        LocalDateTime createdAt,
        String unit
    ) {
        return new CreateSessionExerciseDTO(
            rounds, sets, repetitions, sprints, duration, weight, distance, startTime,
            endTime, restTime, status, exerciseOrder, exerciseId, createdAt, unit);
    }

    /**
     * Creates a CreateSessionExerciseDTO with default test values.
     */
    public static CreateSessionExerciseDTO createCreateSessionExerciseDTO() {
        return new CreateSessionExerciseDTO(
            ROUNDS, SETS, REPETITIONS, SPRINTS, LocalTime.of(0, MINUTES), WEIGHT, DISTANCE, LocalDateTime.now(),
            LocalDateTime.now().plusHours(1), REST_TIME, STATUS_ACTIVE, EXERCISE_ORDER, EXERCISE_ID,
            LocalDateTime.now(), UNIT_KG);
    }

    /**
     * Creates a SessionExerciseDTO with the provided parameters.
     */
    public static SessionExerciseDTO createSessionExerciseDTO(
        long id,
        int rounds,
        int sets,
        int repetitions,
        int sprints,
        LocalTime duration,
        double weight,
        double distance,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int restTime,
        String status,
        int exerciseOrder,
        long exerciseId,
        long trainingSessionId,
        LocalDateTime createdAt,
        String unit
    ) {
        return new SessionExerciseDTO(
            id, rounds, sets, repetitions, sprints, duration, weight, distance, startTime,
            endTime, restTime, status, exerciseOrder, exerciseId, trainingSessionId, createdAt, unit);
    }

    /**
     * Creates a SessionExerciseDTO with default test values.
     */
    public static SessionExerciseDTO createSessionExerciseDTO() {
        return new SessionExerciseDTO(
            SESSION_EXERCISE_ID, ROUNDS, SETS, REPETITIONS, SPRINTS, LocalTime.of(0, MINUTES), WEIGHT, DISTANCE,
            LocalDateTime.now(), LocalDateTime.now().plusHours(1), REST_TIME, STATUS_ACTIVE, EXERCISE_ORDER,
            EXERCISE_ID, TRAINING_SESSION_ID, LocalDateTime.now(), UNIT_KG);
    }
}
