package com.rvladimir.service.dto;

import com.rvladimir.domain.TrainingSession;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Serdeable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Training session response payload")
public class TrainingSessionDTO {
    @Schema(description = "The training session's unique identifier", example = "1",
        accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "The training session name", example = "Morning Workout")
    private String name;

    @Schema(description = "The training session description", example = "A quick morning workout routine")
    private String description;

    @NotNull
    @Schema(description = "The current status of the training session", example = "STARTED",
        allowableValues = {"STARTED", "IN_PROGRESS", "FINISHED", "IS_TEMPLATE"})
    private TrainingSession.Status status;

    @NotNull
    @Schema(description = "The ID of the user who owns this training session", example = "1")
    private Long userId;

    @Schema(description = "The timestamp when the training session was created", example = "2026-01-13T10:30:00",
        accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
}
