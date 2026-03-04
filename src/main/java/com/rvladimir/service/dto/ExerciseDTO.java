package com.rvladimir.service.dto;

import com.rvladimir.domain.TypeOfExercise;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Exercise response and creation.
 */
@Serdeable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDTO {

    @Schema(description = "The exercise ID", example = "1")
    private Long id;

    @NotNull
    @NotBlank
    @Schema(description = "The exercise name", example = "Push-ups")
    private String name;

    @NotNull
    @NotBlank
    @Schema(description = "The exercise description", example = "Upper body strength exercise")
    private String description;

    @NotNull
    @Schema(description = "The exercise type", example = "STRENGTH")
    private TypeOfExercise type;

    @NotNull
    @NotBlank
    @Schema(description = "The exercise image URL", example = "https://example.com/pushups.jpg")
    private String image;
}
