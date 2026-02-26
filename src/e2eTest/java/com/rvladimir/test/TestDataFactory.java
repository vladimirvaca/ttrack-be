package com.rvladimir.test;

import com.rvladimir.domain.User;

import java.time.LocalDate;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Factory class for creating test data objects.
 * Provides reusable methods to create test entities with default or custom values.
 */
public final class TestDataFactory {

    private static final String DEFAULT_USER_NAME = "John";
    private static final String DEFAULT_USER_LASTNAME = "Doe";
    private static final String DEFAULT_USER_NICKNAME = "johnd";
    private static final String DEFAULT_USER_PASSWORD = "hashedPassword";
    private static final int DEFAULT_BIRTH_YEAR = 1990;
    private static final int DEFAULT_BIRTH_MONTH = 5;
    private static final int DEFAULT_BIRTH_DAY = 15;

    private TestDataFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates a user with default values and the specified email.
     *
     * @param email the user's email
     * @return a new User instance
     */
    public static User createUser(String email) {
        return createUser(email, User.Role.USER);
    }

    /**
     * Creates a user with the specified email and role.
     *
     * @param email the user's email
     * @param role the user's role
     * @return a new User instance
     */
    public static User createUser(String email, User.Role role) {
        return new User(
            null,
            DEFAULT_USER_NAME,
            DEFAULT_USER_LASTNAME,
            DEFAULT_USER_NICKNAME,
            LocalDate.of(DEFAULT_BIRTH_YEAR, DEFAULT_BIRTH_MONTH, DEFAULT_BIRTH_DAY),
            email,
            DEFAULT_USER_PASSWORD,
            role
        );
    }

    /**
     * Creates a user with the specified email and a BCrypt-hashed version of the given plain-text password.
     * Use this factory method when the user needs to authenticate through the login endpoints in tests.
     *
     * @param email the user's email
     * @param plainPassword the plain-text password to hash
     * @return a new User instance with a hashed password
     */
    public static User createUserWithPassword(String email, String plainPassword) {
        return new User(
            null,
            DEFAULT_USER_NAME,
            DEFAULT_USER_LASTNAME,
            DEFAULT_USER_NICKNAME,
            LocalDate.of(DEFAULT_BIRTH_YEAR, DEFAULT_BIRTH_MONTH, DEFAULT_BIRTH_DAY),
            email,
            BCrypt.hashpw(plainPassword, BCrypt.gensalt()),
            User.Role.USER
        );
    }
}
