package com.rvladimir.domain;

import com.rvladimir.constants.TtrackConstants;

import io.micronaut.serde.annotation.Serdeable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a session exercise performed during a training session.
 * Maps to ttrack.session_exercise table.
 */
@Serdeable
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "session_exercise", schema = TtrackConstants.TTRACK_SCHEMA)
public class SessionExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer rounds;

    @Column
    private Integer sets;

    @Column
    private Integer repetitions;

    @Column
    private Integer sprints;

    @Column(name = "time")
    private java.time.LocalTime time;

    @Column
    private Double weight;

    @Column
    private Double distance;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @NotNull
    @Column(name = "rest_time", nullable = false)
    private Integer restTime;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @NotNull
    @Column(name = "exercise_order", nullable = false)
    private Integer exerciseOrder;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "training_session_id", nullable = false)
    private TrainingSession trainingSession;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "unit_of_measurement")
    @Enumerated(EnumType.STRING)
    private UnitOfMeasurement unitOfMeasurement;

    /**
     * Unit of measurement for distance.
     */
    public enum UnitOfMeasurement {
        KILOMETERS,
        MILES
    }

    /**
     * Status of the session exercise.
     */
    public enum Status {
        STARTED,
        IN_PROGRESS,
        FINISHED,
        NOT_FINISHED
    }
}
