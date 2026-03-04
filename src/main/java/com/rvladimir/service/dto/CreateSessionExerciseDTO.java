package com.rvladimir.service.dto;

import io.micronaut.serde.annotation.Serdeable;

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
public class CreateSessionExerciseDTO {

    private Integer rounds;

    private Integer sets;

    private Integer repetitions;

    private Integer sprints;

    /** Duration of the exercise (e.g. how long a round or session lasted). */
    private LocalTime duration;

    private Double weight;

    private Double distance;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer restTime;

    private String status;

    private Integer exerciseOrder;

    private Long exerciseId;

    private String unitOfMeasurement;

    private String typeOfExercise;

    /** Free-text notes or observations about this exercise execution. */
    private String notes;
}
