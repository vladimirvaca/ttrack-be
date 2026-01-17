package com.rvladimir.service.dto;

import io.micronaut.serde.annotation.Serdeable;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a SessionExercise.
 */
@Serdeable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSessionExerciseDTO {
    @NotNull
    private Integer rounds;

    @NotNull
    private Integer sets;

    @NotNull
    private Integer repetitions;

    private Integer sprints;

    private LocalTime time;

    private Double weight;

    private Double distance;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    private Integer restTime;

    @NotNull
    private String status;

    @NotNull
    private Integer exerciseOrder;

    @NotNull
    private Long exerciseId;

    @NotNull
    private LocalDateTime createdAt;

    private String unitOfMeasurement;
}
