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

@Controller("/exercise")
@Tag(name = "Exercise")
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
        return HttpResponse.created(exerciseService.create(exerciseDTO));
    }

    @Get
    @Operation(summary = "Get all exercises", description = "Retrieves all exercises with pagination support")
    @ApiResponse(
        responseCode = "200",
        description = "Exercises retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
    )
    public HttpResponse<Page<ExerciseDTO>> getAll(Pageable pageable) {
        return HttpResponse.ok(exerciseService.getAll(pageable));
    }
}
