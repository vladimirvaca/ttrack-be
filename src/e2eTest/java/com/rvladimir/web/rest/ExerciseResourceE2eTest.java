package com.rvladimir.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.rvladimir.domain.Exercise;
import com.rvladimir.repository.ExerciseRepository;
import com.rvladimir.service.dto.ExerciseDTO;
import com.rvladimir.test.PostgresTestContainer;

import io.micronaut.data.model.Page;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;

import jakarta.inject.Inject;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * End-to-end tests for ExerciseResource.
 */
@MicronautTest(transactional = false)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExerciseResourceE2eTest implements TestPropertyProvider {

    private static final String ENDPOINT_EXERCISE = "/exercise";
    private static final String TEST_NAME = "Push-ups";
    private static final String TEST_DESCRIPTION = "Upper body strength exercise";
    private static final Exercise.Type TEST_TYPE = Exercise.Type.STRENGTH;
    private static final String TEST_IMAGE = "https://example.com/pushups.jpg";
    private static final String SQUATS_NAME = "Squats";
    private static final String SQUATS_DESCRIPTION = "Lower body strength exercise";
    private static final String SQUATS_IMAGE = "https://example.com/squats.jpg";
    private static final long EXPECTED_EXERCISE_COUNT_ONE = 1L;
    private static final long EXPECTED_EXERCISE_COUNT_TWO = 2L;

    @Container
    static PostgreSQLContainer<?> postgres = PostgresTestContainer.getInstance();

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    ExerciseRepository exerciseRepository;

    @Override
    public Map<String, String> getProperties() {
        return Map.of(
            "datasources.default.url", postgres.getJdbcUrl(),
            "datasources.default.username", postgres.getUsername(),
            "datasources.default.password", postgres.getPassword(),
            "datasources.default.driverClassName", postgres.getDriverClassName()
        );
    }

    @AfterEach
    void cleanup() {
        exerciseRepository.deleteAll();
    }

    @Test
    void testCreateExerciseEndToEndSuccess() {
        // Given
        ExerciseDTO exerciseDTO = new ExerciseDTO(null, TEST_NAME, TEST_DESCRIPTION, TEST_TYPE, TEST_IMAGE);

        // When
        HttpRequest<ExerciseDTO> request = HttpRequest.POST(ENDPOINT_EXERCISE, exerciseDTO);
        HttpResponse<ExerciseDTO> response = client.toBlocking().exchange(request, ExerciseDTO.class);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getId()).isNotNull();
        assertThat(response.body().getName()).isEqualTo(TEST_NAME);
        assertThat(response.body().getDescription()).isEqualTo(TEST_DESCRIPTION);
        assertThat(response.body().getType()).isEqualTo(TEST_TYPE);
        assertThat(response.body().getImage()).isEqualTo(TEST_IMAGE);

        // Verify exercise was actually saved to database
        long count = exerciseRepository.count();
        assertThat(count).isEqualTo(EXPECTED_EXERCISE_COUNT_ONE);
    }

    @Test
    void testCreateExerciseMissingRequiredFieldsReturns400() {
        // Given - null name
        ExerciseDTO exerciseDTO = new ExerciseDTO(null, null, TEST_DESCRIPTION, TEST_TYPE, TEST_IMAGE);

        // When & Then
        HttpRequest<ExerciseDTO> request = HttpRequest.POST(ENDPOINT_EXERCISE, exerciseDTO);
        assertThatThrownBy(() -> client.toBlocking().exchange(request, ExerciseDTO.class))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
            });

        // Given - null type
        ExerciseDTO exerciseDTO2 = new ExerciseDTO(null, TEST_NAME, TEST_DESCRIPTION, null, TEST_IMAGE);
        HttpRequest<ExerciseDTO> request2 = HttpRequest.POST(ENDPOINT_EXERCISE, exerciseDTO2);
        assertThatThrownBy(() -> client.toBlocking().exchange(request2, ExerciseDTO.class))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
            });
    }

    @Test
    void testMultipleExercisesCanBeCreated() {
        // Given
        ExerciseDTO exercise1 = new ExerciseDTO(null, TEST_NAME, TEST_DESCRIPTION, TEST_TYPE, TEST_IMAGE);
        ExerciseDTO exercise2 = new ExerciseDTO(null, SQUATS_NAME, SQUATS_DESCRIPTION, TEST_TYPE, SQUATS_IMAGE);

        // When
        client.toBlocking().exchange(HttpRequest.POST(ENDPOINT_EXERCISE, exercise1), ExerciseDTO.class);
        client.toBlocking().exchange(HttpRequest.POST(ENDPOINT_EXERCISE, exercise2), ExerciseDTO.class);

        // Then
        long count = exerciseRepository.count();
        assertThat(count).isEqualTo(EXPECTED_EXERCISE_COUNT_TWO);
    }

    @Test
    void testGetAllExercisesReturnsPaginatedResults() {
        // Given
        ExerciseDTO exercise1 = new ExerciseDTO(null, TEST_NAME, TEST_DESCRIPTION, TEST_TYPE, TEST_IMAGE);
        ExerciseDTO exercise2 = new ExerciseDTO(null, SQUATS_NAME, SQUATS_DESCRIPTION, TEST_TYPE, SQUATS_IMAGE);
        client.toBlocking().exchange(HttpRequest.POST(ENDPOINT_EXERCISE, exercise1), ExerciseDTO.class);
        client.toBlocking().exchange(HttpRequest.POST(ENDPOINT_EXERCISE, exercise2), ExerciseDTO.class);

        // When
        HttpRequest<?> request = HttpRequest.GET(ENDPOINT_EXERCISE + "?size=1&page=0");
        @SuppressWarnings("unchecked")
        HttpResponse<Page<ExerciseDTO>> response = client.toBlocking().exchange(
            request,
            (Class<Page<ExerciseDTO>>) (Class<?>) Page.class
        );

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.OK.getCode());
        assertThat(response.body()).isNotNull();
        // Page size is 1, so total size should be at least 2
        assertThat(response.body().getTotalSize()).isGreaterThanOrEqualTo(2);
    }
}

