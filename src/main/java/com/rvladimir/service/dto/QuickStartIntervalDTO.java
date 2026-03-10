package com.rvladimir.service.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

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
public class QuickStartIntervalDTO {

    @NotNull
    @Schema(description = "The ID of the user starting the interval training", example = "1")
    private Long userId;

    @Schema(
        description = "Type of exercise (e.g. HIIT, BOXING_BAG, SHADOW_BOXING, CARDIO)",
        example = "HIIT"
    )
    private String typeOfExercise;

    @Schema(description = "Number of rounds", example = "5")
    private Integer rounds;

    @Schema(description = "Number of sprints", example = "10")
    private Integer sprints;

    @Schema(description = "Duration per round or exercise (e.g. 00:03:00 for 3 minutes)", example = "00:03:00")
    private LocalTime duration;

    @Schema(description = "Rest time in seconds between rounds", example = "60")
    private Integer restTime;

    @Schema(description = "Optional ID of the exercise from the catalog")
    private Long exerciseId;

    @Schema(description = "Optional free-text notes about this interval session", example = "Focus on speed")
    private String notes;
}
