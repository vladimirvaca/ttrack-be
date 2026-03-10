package com.rvladimir.service.dto;

import com.rvladimir.domain.TypeOfExercise;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for the Quick Start Interval training request.
 * The TrainingSession is created automatically — the client only needs to provide
 * the exercise details (type, rounds, sprints, duration, rest, etc.).
 * All exercise metric fields are optional to support HIIT, boxing bag, shadow boxing, etc.
 */
@Serdeable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for quickly starting an interval training session")
public class QuickStartIntervalDTO {

    @NotNull
    @Positive
    @Schema(description = "The ID of the user starting the interval training", example = "1")
    private Long userId;

    @Schema(
        description = "Type of exercise (e.g. HIIT, BOXING_BAG, SHADOW_BOXING, CARDIO)",
        example = "HIIT"
    )
    private TypeOfExercise typeOfExercise;

    @Positive
    @Schema(description = "Number of rounds", example = "5")
    private Integer rounds;

    @Positive
    @Schema(description = "Number of sprints", example = "10")
    private Integer sprints;

    @Schema(description = "Duration per round or exercise (HH:mm:ss)", example = "00:03:00")
    private LocalTime duration;

    @PositiveOrZero
    @Schema(description = "Rest time in seconds between rounds", example = "60")
    private Integer restTime;

    @Positive
    @Schema(description = "Optional ID of the exercise from the catalog", example = "10")
    private Long exerciseId;

    @Schema(description = "Optional free-text notes about this interval session", example = "Focus on speed")
    private String notes;
}
