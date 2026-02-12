package com.rvladimir.web.rest;

import com.rvladimir.service.SessionExerciseService;
import com.rvladimir.service.dto.CreateSessionExerciseDTO;
import com.rvladimir.service.dto.SessionExerciseDTO;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST resource for SessionExercise operations.
 */
@Tag(name = "SessionExercise", description = "Session Exercise management API")
@Controller("/session-exercise")
@RequiredArgsConstructor
@Slf4j
public class SessionExerciseResource {
    private final SessionExerciseService sessionExerciseService;

    /**
     * Retrieves all SessionExercises for a training session.
     */
    @Get("/training-session/{trainingSessionId}")
    @Operation(
        summary = "Get session exercises by training session",
        description = "Retrieves all session exercises for a given training session.")
    @ApiResponse(responseCode = "200", description = "List of SessionExercises")
    @ApiResponse(responseCode = "404", description = "Training session not found.")
    public HttpResponse<List<SessionExerciseDTO>> getSessionExercisesByTrainingSession(
        @PathVariable Long trainingSessionId
    ) {
        log.info("Retrieving session exercises for trainingSessionId: {}", trainingSessionId);
        List<SessionExerciseDTO> result =
            sessionExerciseService.getSessionExercisesByTrainingSession(trainingSessionId);
        if (result == null || result.isEmpty()) {
            log.warn("No session exercises found for trainingSessionId: {}", trainingSessionId);
        } else {
            log.info("Found {} session exercises for trainingSessionId: {}", result.size(), trainingSessionId);
        }
        return HttpResponse.ok(result);
    }

    /**
     * Creates a new SessionExercise for a training session.
     */
    @Operation(
        summary = "Create a new session exercise for a training session",
        description = "Creates a new session exercise for a specific training session."
    )
    @ApiResponse(responseCode = "201", description = "SessionExercise created successfully.")
    @ApiResponse(responseCode = "400", description = "Invalid input.")
    @Post(uri = "/training-sessions/{trainingSessionId}/session-exercise")
    public HttpResponse<SessionExerciseDTO> createSessionExercise(
        @PathVariable Long trainingSessionId,
        @Body @Valid CreateSessionExerciseDTO dto
    ) {
        log.info("Creating session exercise for trainingSessionId: {}", trainingSessionId);
        SessionExerciseDTO createdDTO = sessionExerciseService.createSessionExercise(trainingSessionId, dto);
        if (createdDTO == null) {
            log.warn("Session exercise creation failed for trainingSessionId: {}", trainingSessionId);
        } else {
            log.info(
                "Session exercise created successfully: id={}, trainingSessionId={}",
                createdDTO.getId(),
                trainingSessionId
            );
        }
        return HttpResponse.created(createdDTO);
    }

}
