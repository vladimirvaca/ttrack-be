package com.rvladimir.service.dto;

import com.rvladimir.domain.SessionExercise;
import com.rvladimir.domain.TypeOfExercise;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for SessionExercise response.
 */
@Data
@Serdeable
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Session exercise response payload")
public class SessionExerciseDTO {
    @Schema(description = "The session exercise's unique identifier", example = "1",
        accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Number of rounds performed", example = "5")
    private Integer rounds;

    @Schema(description = "Number of sets performed", example = "3")
    private Integer sets;

    @Schema(description = "Number of repetitions per set", example = "12")
    private Integer repetitions;

    @Schema(description = "Number of sprints performed", example = "8")
    private Integer sprints;

    @Schema(description = "Duration of the exercise or round (HH:mm:ss)", example = "00:03:00")
    private LocalTime duration;

    @Schema(description = "Weight used in kilograms", example = "80.5")
    private Double weight;

    @Schema(description = "Distance covered", example = "5.0")
    private Double distance;

    @Schema(description = "Date and time when the exercise started", example = "2026-01-13T10:00:00")
    private LocalDateTime startTime;

    @Schema(description = "Date and time when the exercise ended", example = "2026-01-13T10:45:00")
    private LocalDateTime endTime;

    @Schema(description = "Rest time in seconds between rounds or sets", example = "60")
    private Integer restTime;

    @Schema(description = "Status of the session exercise", example = "STARTED",
        allowableValues = {"STARTED", "IN_PROGRESS", "FINISHED", "NOT_FINISHED"})
    private SessionExercise.Status status;

    @Schema(description = "Order of this exercise within the training session", example = "1")
    private Integer exerciseOrder;

    @Schema(description = "The ID of the exercise from the catalog", example = "10")
    private Long exerciseId;

    @Schema(description = "The ID of the training session this exercise belongs to", example = "3")
    private Long trainingSessionId;

    @Schema(description = "Timestamp when this session exercise was created", example = "2026-01-13T10:00:00",
        accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Unit of measurement for distance", example = "KILOMETERS",
        allowableValues = {"KILOMETERS", "MILES"})
    private SessionExercise.UnitOfMeasurement unitOfMeasurement;

    @Schema(description = "Type of exercise performed", example = "HIIT")
    private TypeOfExercise typeOfExercise;

    @Schema(description = "Optional notes about this exercise execution", example = "Focus on form")
    private String notes;
}
