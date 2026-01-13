package com.rvladimir.test;

import com.rvladimir.domain.TrainingSession;
import com.rvladimir.domain.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Factory class for creating test data objects.
 * Provides reusable methods to create test entities with default or custom values.
 */
public final class TestDataFactory {

    private static final String DEFAULT_USER_NAME = "John";
    private static final String DEFAULT_USER_LASTNAME = "Doe";
    private static final String DEFAULT_USER_PASSWORD = "hashedPassword";
    private static final int DEFAULT_BIRTH_YEAR = 1990;
    private static final int DEFAULT_BIRTH_MONTH = 5;
    private static final int DEFAULT_BIRTH_DAY = 15;

    private TestDataFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates a user with default values and the specified email.
     * @param email the user's email
     * @return a new User instance
     */
    public static User createUser(String email) {
        return createUser(email, User.Role.USER);
    }

    /**
     * Creates a user with the specified email and role.
     * @param email the user's email
     * @param role  the user's role
     * @return a new User instance
     */
    public static User createUser(String email, User.Role role) {
        return new User(
            null,
            DEFAULT_USER_NAME,
            DEFAULT_USER_LASTNAME,
            LocalDate.of(DEFAULT_BIRTH_YEAR, DEFAULT_BIRTH_MONTH, DEFAULT_BIRTH_DAY),
            email,
            DEFAULT_USER_PASSWORD,
            role
        );
    }

    /**
     * Creates a training session with the specified user and status.
     * @param user   the user associated with the training session
     * @param status the training session status
     * @return a new TrainingSession instance
     */
    public static TrainingSession createTrainingSession(User user, TrainingSession.Status status) {
        return createTrainingSession(user, status, null, null);
    }

    /**
     * Creates a training session with all specified values.
     * @param user        the user associated with the training session
     * @param status      the training session status
     * @param name        the training session name (can be null)
     * @param description the training session description (can be null)
     * @return a new TrainingSession instance
     */
    public static TrainingSession createTrainingSession(
        User user,
        TrainingSession.Status status,
        String name,
        String description
    ) {
        return new TrainingSession(
            null,
            name,
            description,
            status,
            user,
            LocalDateTime.now()
        );
    }
}
