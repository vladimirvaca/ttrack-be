package com.rvladimir.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rvladimir.service.ExerciseService;
import com.rvladimir.service.dto.ExerciseDTO;

import io.micronaut.core.type.Argument;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

import jakarta.inject.Inject;

import java.util.Collections;

import org.junit.jupiter.api.Test;

/**
 * Test class for ExerciseResource.
 */
@MicronautTest
class ExerciseResourceTest {

    private static final Long EXERCISE_ID = 1L;
    private static final String EXERCISE_NAME = "Push-ups";
    private static final String EXERCISE_DESCRIPTION = "Upper body strength exercise";
    private static final String EXERCISE_TYPE = "STRENGTH";
    private static final String EXERCISE_IMAGE = "https://example.com/pushups.jpg";
    private static final String ENDPOINT_EXERCISE = "/exercise";

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    ExerciseService exerciseService;

    @MockBean(ExerciseService.class)
    ExerciseService exerciseService() {
        return mock(ExerciseService.class);
    }

    @Test
    void testCreateExerciseSuccess() {
        // Given
        ExerciseDTO exerciseDTO =
            new ExerciseDTO(
                EXERCISE_ID,
                EXERCISE_NAME,
                EXERCISE_DESCRIPTION,
                com.rvladimir.domain.Exercise.Type.valueOf(EXERCISE_TYPE),
                EXERCISE_IMAGE);
        when(exerciseService.create(any(ExerciseDTO.class))).thenReturn(exerciseDTO);

        // When
        HttpRequest<ExerciseDTO> request = HttpRequest.POST(ENDPOINT_EXERCISE, exerciseDTO);
        HttpResponse<ExerciseDTO> response = client.toBlocking().exchange(request, ExerciseDTO.class);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getName()).isEqualTo(EXERCISE_NAME);
        assertThat(response.body().getDescription()).isEqualTo(EXERCISE_DESCRIPTION);
        assertThat(response.body().getType().name()).isEqualTo(EXERCISE_TYPE);
        assertThat(response.body().getImage()).isEqualTo(EXERCISE_IMAGE);
        verify(exerciseService).create(any(ExerciseDTO.class));
    }

    @Test
    void testCreateExerciseValidationErrorEmptyName() {
        // Given
        ExerciseDTO exerciseDTO =
            new ExerciseDTO(
                EXERCISE_ID,
                "",
                EXERCISE_DESCRIPTION,
                com.rvladimir.domain.Exercise.Type.valueOf(EXERCISE_TYPE),
                EXERCISE_IMAGE);

        // When & Then
        HttpRequest<ExerciseDTO> request = HttpRequest.POST(ENDPOINT_EXERCISE, exerciseDTO);
        HttpClientResponseException thrown = org.junit.jupiter.api.Assertions.assertThrows(
            HttpClientResponseException.class, () -> client.toBlocking()
                .exchange(request, ExerciseDTO.class));
        assertThat(thrown.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());

        // Optionally verify service was never called
        verify(exerciseService, org.mockito.Mockito.never()).create(any(ExerciseDTO.class));
    }

    @Test
    void testGetAllExercisesSuccess() {
        // Given
        Page<ExerciseDTO> page =
            Page.of(
                Collections.singletonList(
                    new ExerciseDTO(
                        EXERCISE_ID,
                        EXERCISE_NAME,
                        EXERCISE_DESCRIPTION,
                        com.rvladimir.domain.Exercise.Type.valueOf(EXERCISE_TYPE),
                        EXERCISE_IMAGE)),
                Pageable.from(0), 1L);
        when(exerciseService.getAll(any(Pageable.class))).thenReturn(page);

        // When
        HttpRequest<?> request = HttpRequest.GET(ENDPOINT_EXERCISE);
        @SuppressWarnings("unchecked")
        HttpResponse<Page<ExerciseDTO>> response =
            (HttpResponse<Page<ExerciseDTO>>) (HttpResponse<?>) client.toBlocking()
                .exchange(request, Argument.of(Page.class, Argument.of(ExerciseDTO.class)));

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.OK.getCode());
        assertThat(response.body()).isNotNull();
        verify(exerciseService).getAll(any(Pageable.class));
    }
}
