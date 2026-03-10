package com.rvladimir.web.rest;

import com.rvladimir.service.TrainingSessionService;
import com.rvladimir.service.dto.CreateTrainingSessionDTO;
import com.rvladimir.service.dto.QuickStartIntervalDTO;
import com.rvladimir.service.dto.QuickStartIntervalResponseDTO;
import com.rvladimir.service.dto.TrainingSessionDTO;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Training Session")
@Controller("/training-session")
@Slf4j
@AllArgsConstructor
public class TrainingSessionResource {

    private final TrainingSessionService trainingSessionService;

    @ApiResponse(responseCode = "201", description = "Training session created successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid training session data.")
    @Operation(summary = "Create a new training session", description = "Creates a new training session in the system.")
    @Post(uri = "/create")
    public HttpResponse<TrainingSessionDTO> createTrainingSession(
        @Body @Valid CreateTrainingSessionDTO createTrainingSessionDTO
    ) {
        log.info("Creating training session for user ID: {}", createTrainingSessionDTO.getUserId());
        TrainingSessionDTO trainingSessionDTO = trainingSessionService.create(createTrainingSessionDTO);
        if (trainingSessionDTO == null) {
            log.warn("Training session creation failed for user ID: {}", createTrainingSessionDTO.getUserId());
        } else {
            log.info(
                "Training session created successfully: id={}, userId={}",
                trainingSessionDTO.getId(),
                trainingSessionDTO.getUserId()
            );
        }
        return HttpResponse.created(trainingSessionDTO);
    }

    @ApiResponse(responseCode = "201", description = "Interval training session started successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid request data.")
    @ApiResponse(responseCode = "404", description = "User not found.")
    @Operation(
        summary = "Quick start an interval training session",
        description = "Atomically creates a TrainingSession and its first SessionExercise " +
            "for interval-based training (HIIT, boxing bag, shadow boxing, etc.)."
    )
    @Post(uri = "/quick-start/interval")
    public HttpResponse<QuickStartIntervalResponseDTO> quickStartInterval(
        @Body @Valid QuickStartIntervalDTO quickStartIntervalDTO
    ) {
        log.info("Quick starting interval training for user ID: {}", quickStartIntervalDTO.getUserId());
        QuickStartIntervalResponseDTO response = trainingSessionService.quickStartInterval(quickStartIntervalDTO);
        log.info(
            "Interval training quick started: sessionId={}, exerciseId={}, userId={}",
            response.getTrainingSession().getId(),
            response.getSessionExercise().getId(),
            quickStartIntervalDTO.getUserId()
        );
        return HttpResponse.created(response);
    }

}
