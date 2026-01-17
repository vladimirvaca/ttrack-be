package com.rvladimir.web.rest;

import static com.rvladimir.service.impl.SessionExerciseTestHelper.SESSION_EXERCISE_ID;
import static com.rvladimir.service.impl.SessionExerciseTestHelper.TRAINING_SESSION_ID;
import static com.rvladimir.service.impl.SessionExerciseTestHelper.createCreateSessionExerciseDTO;
import static com.rvladimir.service.impl.SessionExerciseTestHelper.createSessionExerciseDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rvladimir.service.SessionExerciseService;
import com.rvladimir.service.dto.CreateSessionExerciseDTO;
import com.rvladimir.service.dto.SessionExerciseDTO;

import io.micronaut.core.type.Argument;
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
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Test class for SessionExerciseResource.
 */
@MicronautTest
class SessionExerciseResourceTest {

    private static final String ENDPOINT = "/session-exercise/training-session/" + TRAINING_SESSION_ID;
    private static final String CREATE_ENDPOINT =
        "/session-exercise/training-sessions/" + TRAINING_SESSION_ID + "/session-exercise";

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    SessionExerciseService sessionExerciseService;

    @MockBean(SessionExerciseService.class)
    SessionExerciseService sessionExerciseService() {
        return mock(SessionExerciseService.class);
    }

    @Test
    void testGetSessionExercisesByTrainingSessionSuccess() {
        // Given
        SessionExerciseDTO dto = createSessionExerciseDTO();
        List<SessionExerciseDTO> dtos = Collections.singletonList(dto);
        when(sessionExerciseService.getSessionExercisesByTrainingSession(TRAINING_SESSION_ID)).thenReturn(dtos);

        // When
        HttpRequest<?> request = HttpRequest.GET(ENDPOINT);
        HttpResponse<List<SessionExerciseDTO>> response =
            client.toBlocking().exchange(request, Argument.listOf(SessionExerciseDTO.class));

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.OK.getCode());
        assertThat(response.body()).isNotNull();
        assertThat(response.body().size()).isEqualTo(1);
        verify(sessionExerciseService).getSessionExercisesByTrainingSession(TRAINING_SESSION_ID);
    }

    @Test
    void testCreateSessionExerciseSuccess() {
        // Given
        CreateSessionExerciseDTO createDto = createCreateSessionExerciseDTO();
        SessionExerciseDTO dto = createSessionExerciseDTO();
        when(sessionExerciseService.createSessionExercise(TRAINING_SESSION_ID, createDto)).thenReturn(dto);

        // When
        HttpRequest<CreateSessionExerciseDTO> request = HttpRequest.POST(CREATE_ENDPOINT, createDto);
        HttpResponse<SessionExerciseDTO> response = client.toBlocking().exchange(request, SessionExerciseDTO.class);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getId()).isEqualTo(SESSION_EXERCISE_ID);
        verify(sessionExerciseService).createSessionExercise(TRAINING_SESSION_ID, createDto);
    }

    @Test
    void testCreateSessionExerciseValidationError() {
        // Given
        CreateSessionExerciseDTO createDto = null;

        // When
        HttpRequest<CreateSessionExerciseDTO> request = HttpRequest.POST(CREATE_ENDPOINT, createDto);
        HttpClientResponseException thrown = org.junit.jupiter.api.Assertions.assertThrows(
            HttpClientResponseException.class, () -> client.toBlocking().exchange(request, SessionExerciseDTO.class));

        // Then
        assertThat(thrown.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        verify(sessionExerciseService, org.mockito.Mockito.never()).createSessionExercise(
            any(Long.class),
            any(CreateSessionExerciseDTO.class));
    }
}
