package com.rvladimir.service.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Composite response DTO for the Quick Start Interval training operation.
 * Contains both the created TrainingSession and its first SessionExercise.
 */
@Serdeable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuickStartIntervalResponseDTO {

    @Schema(description = "The newly created training session")
    private TrainingSessionDTO trainingSession;

    @Schema(description = "The first session exercise created for the interval training")
    private SessionExerciseDTO sessionExercise;
}

