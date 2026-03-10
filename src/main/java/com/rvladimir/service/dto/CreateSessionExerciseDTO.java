package com.rvladimir.service.dto;

import com.rvladimir.domain.SessionExercise;
import com.rvladimir.domain.TypeOfExercise;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a SessionExercise.
 * All metric fields are optional to support any exercise modality.
 * The {@code createdAt} timestamp is assigned server-side and must not be provided by the client.
 */
@Serdeable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating a new session exercise")
public class CreateSessionExerciseDTO {

    @PositiveOrZero
    @Schema(description = "Number of rounds performed", example = "5")
    private Integer rounds;

    @PositiveOrZero
    @Schema(description = "Number of sets performed", example = "3")
    private Integer sets;

    @PositiveOrZero
    @Schema(description = "Number of repetitions per set", example = "12")
    private Integer repetitions;

    @PositiveOrZero
    @Schema(description = "Number of sprints performed", example = "8")
    private Integer sprints;

    /** Duration of the exercise (e.g. how long a round or session lasted). */
    @Schema(description = "Duration of the exercise or round (HH:mm:ss)", example = "00:03:00")
    private LocalTime duration;

    @PositiveOrZero
    @Schema(description = "Weight used in kilograms", example = "80.5")
    private Double weight;

    @PositiveOrZero
    @Schema(description = "Distance covered", example = "5.0")
    private Double distance;

    @Schema(description = "Date and time when the exercise started", example = "2026-01-13T10:00:00")
    private LocalDateTime startTime;

    @Schema(description = "Date and time when the exercise ended", example = "2026-01-13T10:45:00")
    private LocalDateTime endTime;

    @PositiveOrZero
    @Schema(description = "Rest time in seconds between rounds or sets", example = "60")
    private Integer restTime;

    @Schema(description = "Status of the session exercise",
        example = "STARTED",
        allowableValues = {"STARTED", "IN_PROGRESS", "FINISHED", "NOT_FINISHED"})
    private SessionExercise.Status status;

    @PositiveOrZero
    @Schema(description = "Order of this exercise within the training session", example = "1")
    private Integer exerciseOrder;

    @NotNull
    @PositiveOrZero
    @Schema(description = "The ID of the exercise from the catalog", example = "10")
    private Long exerciseId;

    @Schema(description = "Unit of measurement for distance",
        example = "KILOMETERS",
        allowableValues = {"KILOMETERS", "MILES"})
    private SessionExercise.UnitOfMeasurement unitOfMeasurement;

    @Schema(description = "Type of exercise being performed", example = "HIIT")
    private TypeOfExercise typeOfExercise;

    /** Free-text notes or observations about this exercise execution. */
    @Schema(description = "Optional notes about this exercise execution", example = "Focus on form")
    private String notes;
}
