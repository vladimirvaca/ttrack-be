package com.rvladimir.service.dto;

import com.rvladimir.domain.TrainingSession;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Serdeable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSessionDTO {
    @Schema(description = "The training session ID", example = "1")
    private Long id;

    @Schema(description = "The training session name", example = "Morning Workout")
    private String name;

    @Schema(description = "The training session description", example = "A quick morning workout routine")
    private String description;

    @Schema(description = "The training session status", example = "STARTED")
    private TrainingSession.Status status;

    @Schema(description = "The user ID", example = "1")
    private Long userId;

    @Schema(description = "The timestamp when the training session was created", example = "2026-01-13T10:30:00")
    private LocalDateTime createdAt;
}
