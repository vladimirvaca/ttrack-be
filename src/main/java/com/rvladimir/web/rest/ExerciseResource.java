package com.rvladimir.web.rest;

import com.rvladimir.service.ExerciseService;
import com.rvladimir.service.dto.ExerciseDTO;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller("/exercise")
@Tag(name = "Exercise")
@Slf4j
@AllArgsConstructor
public class ExerciseResource {

    private final ExerciseService exerciseService;

    @Post
    @Operation(summary = "Create a new exercise", description = "Creates a new exercise with the provided details")
    @ApiResponse(
        responseCode = "201",
        description = "Exercise created successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExerciseDTO.class))
    )
    public HttpResponse<ExerciseDTO> create(@Body @Valid ExerciseDTO exerciseDTO) {
        log.info("Creating exercise with name: {}", exerciseDTO.getName());
        ExerciseDTO created = exerciseService.create(exerciseDTO);
        if (created == null) {
            log.warn("Exercise creation failed for name: {}", exerciseDTO.getName());
        } else {
            log.info("Exercise created successfully: id={}, name={}", created.getId(), created.getName());
        }
        return HttpResponse.created(created);
    }

    @Get
    @Operation(summary = "Get all exercises", description = "Retrieves all exercises with pagination support")
    @ApiResponse(
        responseCode = "200",
        description = "Exercises retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
    )
    public HttpResponse<Page<ExerciseDTO>> getAll(Pageable pageable) {
        log.info("Retrieving all exercises with pageable: {}", pageable);
        Page<ExerciseDTO> page = exerciseService.getAll(pageable);
        if (page == null || page.isEmpty()) {
            log.warn("No exercises found for pageable: {}", pageable);
        } else {
            log.info("Found {} exercises.", page.getContent().size());
        }
        return HttpResponse.ok(page);
    }

}
