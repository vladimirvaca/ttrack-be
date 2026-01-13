package com.rvladimir.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rvladimir.domain.TrainingSession;
import com.rvladimir.service.TrainingSessionService;
import com.rvladimir.service.dto.CreateTrainingSessionDTO;
import com.rvladimir.service.dto.TrainingSessionDTO;
import com.rvladimir.web.error.ValidationException;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.jupiter.api.Test;

/**
 * Test class for TrainingSessionResource.
 */
@MicronautTest
class TrainingSessionResourceTest {

    private static final long USER_ID_1 = 1L;
    private static final long USER_ID_2 = 2L;
    private static final long TRAINING_SESSION_ID = 1L;
    private static final String TEST_NAME = "Morning Workout";
    private static final String TEST_DESCRIPTION = "A quick morning workout routine";
    private static final String ENDPOINT_TRAINING_SESSION_CREATE = "/training-session/create";
    private static final String VALIDATION_MESSAGE = "User not found";
    private static final String VALIDATION_FIELD = "userId";
    private static final String VALIDATION_CODE = "NOT_FOUND";
    private static final int YEAR_2026 = 2026;
    private static final Month JANUARY = Month.JANUARY;
    private static final int DAY_13 = 13;
    private static final int HOUR_10 = 10;
    private static final int MINUTE_30 = 30;

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    TrainingSessionService trainingSessionService;

    @MockBean(TrainingSessionService.class)
    TrainingSessionService trainingSessionService() {
        return mock(TrainingSessionService.class);
    }

    @Test
    void testCreateTrainingSessionSuccess() {
        // Given
        CreateTrainingSessionDTO createTrainingSessionDTO = new CreateTrainingSessionDTO(USER_ID_1);

        TrainingSessionDTO trainingSessionDTO = new TrainingSessionDTO(
            TRAINING_SESSION_ID,
            TEST_NAME,
            TEST_DESCRIPTION,
            TrainingSession.Status.STARTED,
            USER_ID_1,
            LocalDateTime.of(YEAR_2026, JANUARY, DAY_13, HOUR_10, MINUTE_30)
        );

        when(trainingSessionService.create(any(CreateTrainingSessionDTO.class))).thenReturn(trainingSessionDTO);

        // When
        HttpRequest<CreateTrainingSessionDTO> request = HttpRequest.POST(
            ENDPOINT_TRAINING_SESSION_CREATE, createTrainingSessionDTO
        );
        HttpResponse<TrainingSessionDTO> response = client.toBlocking().exchange(request, TrainingSessionDTO.class);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getId()).isEqualTo(TRAINING_SESSION_ID);
        assertThat(response.body().getUserId()).isEqualTo(USER_ID_1);
        assertThat(response.body().getName()).isEqualTo(TEST_NAME);
        assertThat(response.body().getStatus()).isEqualTo(TrainingSession.Status.STARTED);

        verify(trainingSessionService).create(any(CreateTrainingSessionDTO.class));
    }

    @Test
    void testCreateTrainingSessionValidationErrorNullUserId() {
        // Given
        CreateTrainingSessionDTO createTrainingSessionDTO = new CreateTrainingSessionDTO(null);

        // When & Then
        HttpRequest<CreateTrainingSessionDTO> request =
            HttpRequest.POST(ENDPOINT_TRAINING_SESSION_CREATE, createTrainingSessionDTO);
        assertThatThrownBy(() -> client.toBlocking().exchange(request, TrainingSessionDTO.class))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
            });
    }

    @Test
    void testCreateTrainingSessionUserNotFound() {
        // Given
        CreateTrainingSessionDTO createTrainingSessionDTO = new CreateTrainingSessionDTO(USER_ID_2);

        when(trainingSessionService.create(any(CreateTrainingSessionDTO.class)))
            .thenThrow(new ValidationException(VALIDATION_MESSAGE, VALIDATION_FIELD, VALIDATION_CODE));

        // When & Then
        HttpRequest<CreateTrainingSessionDTO> request =
            HttpRequest.POST(ENDPOINT_TRAINING_SESSION_CREATE, createTrainingSessionDTO);
        assertThatThrownBy(() -> client.toBlocking().exchange(request, TrainingSessionDTO.class))
            .isInstanceOf(HttpClientResponseException.class);
    }
}
