package com.rvladimir.service.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Serdeable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating a new training session")
public class CreateTrainingSessionDTO {
    @NotNull
    @Positive
    @Schema(description = "The ID of the user creating the training session", example = "1")
    private Long userId;
}
