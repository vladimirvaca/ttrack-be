package com.rvladimir.service.dto;

import io.micronaut.serde.annotation.Serdeable;

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

    private Integer rounds;

    private Integer sets;

    private Integer repetitions;

    private Integer sprints;

    private LocalTime time;

    private Double weight;

    private Double distance;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer restTime;

    private String status;

    private Integer exerciseOrder;

    private Long exerciseId;

    private LocalDateTime createdAt;

    private String unitOfMeasurement;

    private String typeOfExercise;
}
