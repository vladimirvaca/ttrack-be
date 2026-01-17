package com.rvladimir.web.rest;

import com.rvladimir.service.TrainingSessionService;
import com.rvladimir.service.dto.CreateTrainingSessionDTO;
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
        return HttpResponse.created(trainingSessionDTO);
    }
}
