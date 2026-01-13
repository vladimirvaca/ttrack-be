package com.rvladimir.test;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Shared PostgreSQL test container for reuse across E2E tests.
 * This singleton pattern ensures only one container is started for all tests,
 * improving test execution speed and resource usage.
 */
public final class PostgresTestContainer {

    private static final String POSTGRES_IMAGE = "postgres:16-alpine";
    private static final String DATABASE_NAME = "testdb";
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "testpass";

    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>(POSTGRES_IMAGE)
        .withDatabaseName(DATABASE_NAME)
        .withUsername(USERNAME)
        .withPassword(PASSWORD)
        .withReuse(true);

    private PostgresTestContainer() {
        // Private constructor to prevent instantiation
    }

    /**
     * Returns the shared PostgreSQL container instance.
     * Starts the container if it's not already running.
     *
     * @return the PostgreSQL container
     */
    public static PostgreSQLContainer<?> getInstance() {
        if (!CONTAINER.isRunning()) {
            CONTAINER.start();
        }
        return CONTAINER;
    }
}
