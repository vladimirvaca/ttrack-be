package com.rvladimir.service.dto;

import io.micronaut.serde.annotation.Serdeable;

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
public class SessionExerciseDTO {
    private Long id;
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
    private Long trainingSessionId;
    private LocalDateTime createdAt;
    private String unitOfMeasurement;
}
