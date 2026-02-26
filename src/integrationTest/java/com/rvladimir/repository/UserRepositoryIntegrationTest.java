package com.rvladimir.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.rvladimir.domain.User;
import com.rvladimir.test.PostgresTestContainer;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;

import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@MicronautTest(transactional = false)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryIntegrationTest implements TestPropertyProvider {

    private static final String TEST_JOHN = "John";
    private static final String TEST_DOE = "Doe";
    private static final String TEST_JANE = "Jane";
    private static final String TEST_SMITH = "Smith";
    private static final String TEST_ALICE = "Alice";
    private static final String TEST_JOHNSON = "Johnson";
    private static final String TEST_BOB = "Bob";
    private static final String TEST_WILLIAMS = "Williams";
    private static final String TEST_USER = "User";
    private static final String TEST_ONE = "One";
    private static final String TEST_TWO = "Two";
    private static final String TEST_ORIGINAL = "Original";
    private static final String TEST_NAME = "Name";
    private static final String TEST_UPDATED = "Updated";
    private static final String TEST_NAME_CHANGED = "NameChanged";
    private static final String TEST_DELETE = "Delete";
    private static final String TEST_ME = "Me";
    private static final String TEST_NICKNAME_JOHN = "johnd";
    private static final String TEST_NICKNAME_JANE = "janes";
    private static final String TEST_NICKNAME_ALICE = "alicej";
    private static final String TEST_NICKNAME_BOB = "bobw";
    private static final String TEST_NICKNAME_USER1 = "user1";
    private static final String TEST_NICKNAME_USER2 = "user2";
    private static final String TEST_NICKNAME_ORIGINAL = "origuser";
    private static final String TEST_NICKNAME_UPDATED = "upduser";
    private static final String TEST_NICKNAME_DELETE = "deluser";
    private static final String TEST_EMAIL_JOHN = "john.doe@example.com";
    private static final String TEST_EMAIL_JANE = "jane.smith@example.com";
    private static final String TEST_EMAIL_ALICE = "alice.johnson@example.com";
    private static final String TEST_EMAIL_BOB = "bob.williams@example.com";
    private static final String TEST_EMAIL_DUPLICATE = "duplicate@example.com";
    private static final String TEST_EMAIL_ORIGINAL = "original@example.com";
    private static final String TEST_EMAIL_DELETE = "delete@example.com";
    private static final String TEST_EMAIL_NONEXISTENT = "nonexistent@example.com";
    private static final String TEST_PASSWORD = "hashedPassword";
    private static final String TEST_PASSWORD_1 = "password1";
    private static final String TEST_PASSWORD_2 = "password2";
    private static final String TEST_PASSWORD_GENERIC = "password";
    private static final String DUPLICATE_KEYWORD = "duplicate";
    private static final int BIRTH_YEAR_1988 = 1988;
    private static final int BIRTH_YEAR_1990 = 1990;
    private static final int BIRTH_YEAR_1991 = 1991;
    private static final int BIRTH_YEAR_1985 = 1985;
    private static final int BIRTH_YEAR_1992 = 1992;
    private static final int BIRTH_MONTH_2 = 2;
    private static final int BIRTH_MONTH_3 = 3;
    private static final int BIRTH_MONTH_5 = 5;
    private static final int BIRTH_MONTH_7 = 7;
    private static final int BIRTH_MONTH_10 = 10;
    private static final int BIRTH_DAY_12 = 12;
    private static final int BIRTH_DAY_15 = 15;
    private static final int BIRTH_DAY_20 = 20;
    private static final int BIRTH_DAY_25 = 25;

    @Container
    static PostgreSQLContainer<?> postgres = PostgresTestContainer.getInstance();

    @Inject
    UserRepository userRepository;

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
        userRepository.deleteAll();
    }

    @Test
    void testSaveUser() {
        // Given
        User user = new User(
            null,
            TEST_JOHN,
            TEST_DOE,
            TEST_NICKNAME_JOHN,
            LocalDate.of(BIRTH_YEAR_1990, BIRTH_MONTH_5, BIRTH_DAY_15),
            TEST_EMAIL_JOHN,
            TEST_PASSWORD,
            User.Role.USER
        );

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo(TEST_JOHN);
        assertThat(savedUser.getLastname()).isEqualTo(TEST_DOE);
        assertThat(savedUser.getNickname()).isEqualTo(TEST_NICKNAME_JOHN);
        assertThat(savedUser.getEmail()).isEqualTo(TEST_EMAIL_JOHN);
    }

    @Test
    void testFindById() {
        // Given
        User user = new User(
            null,
            TEST_JANE,
            TEST_SMITH,
            TEST_NICKNAME_JANE,
            LocalDate.of(BIRTH_YEAR_1985, BIRTH_MONTH_10, BIRTH_DAY_20),
            TEST_EMAIL_JANE,
            TEST_PASSWORD,
            User.Role.ADMIN
        );
        User savedUser = userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo(TEST_JANE);
        assertThat(foundUser.get().getNickname()).isEqualTo(TEST_NICKNAME_JANE);
        assertThat(foundUser.get().getEmail()).isEqualTo(TEST_EMAIL_JANE);
    }

    @Test
    void testFindByEmail() {
        // Given
        User user = new User(
            null,
            TEST_ALICE,
            TEST_JOHNSON,
            TEST_NICKNAME_ALICE,
            LocalDate.of(BIRTH_YEAR_1992, BIRTH_MONTH_3, BIRTH_DAY_12),
            TEST_EMAIL_ALICE,
            TEST_PASSWORD,
            User.Role.USER
        );
        userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findByEmail(TEST_EMAIL_ALICE);

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo(TEST_ALICE);
        assertThat(foundUser.get().getLastname()).isEqualTo(TEST_JOHNSON);
        assertThat(foundUser.get().getNickname()).isEqualTo(TEST_NICKNAME_ALICE);
    }

    @Test
    void testFindByEmailNotFound() {
        // When
        Optional<User> foundUser = userRepository.findByEmail(TEST_EMAIL_NONEXISTENT);

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void testExistsByEmailReturnsTrue() {
        // Given
        User user = new User(
            null,
            TEST_BOB,
            TEST_WILLIAMS,
            TEST_NICKNAME_BOB,
            LocalDate.of(BIRTH_YEAR_1988, BIRTH_MONTH_7, BIRTH_DAY_25),
            TEST_EMAIL_BOB,
            TEST_PASSWORD,
            User.Role.USER
        );
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByEmail(TEST_EMAIL_BOB);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByEmailReturnsFalse() {
        // When
        boolean exists = userRepository.existsByEmail(TEST_EMAIL_NONEXISTENT);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void testUniqueEmailConstraint() {
        // Given
        User user1 = new User(
            null,
            TEST_USER,
            TEST_ONE,
            TEST_NICKNAME_USER1,
            LocalDate.of(BIRTH_YEAR_1990, 1, 1),
            TEST_EMAIL_DUPLICATE,
            TEST_PASSWORD_1,
            User.Role.USER
        );
        userRepository.save(user1);

        User user2 = new User(
            null,
            TEST_USER,
            TEST_TWO,
            TEST_NICKNAME_USER2,
            LocalDate.of(BIRTH_YEAR_1991, BIRTH_MONTH_2, BIRTH_MONTH_2),
            TEST_EMAIL_DUPLICATE,
            TEST_PASSWORD_2,
            User.Role.USER
        );

        // When & Then
        try {
            userRepository.save(user2);
            userRepository.flush();
            // Should not reach here
            assertThat(false).isTrue();
        } catch (Exception e) {
            assertThat(e).hasMessageContaining(DUPLICATE_KEYWORD);
        }
    }

    @Test
    void testUniqueNicknameConstraint() {
        // Given
        User user1 = new User(
            null,
            TEST_USER,
            TEST_ONE,
            TEST_NICKNAME_USER1,
            LocalDate.of(BIRTH_YEAR_1990, 1, 1),
            TEST_EMAIL_JOHN,
            TEST_PASSWORD_1,
            User.Role.USER
        );
        userRepository.save(user1);

        User user2 = new User(
            null,
            TEST_USER,
            TEST_TWO,
            TEST_NICKNAME_USER1,
            LocalDate.of(BIRTH_YEAR_1991, BIRTH_MONTH_2, BIRTH_MONTH_2),
            TEST_EMAIL_JANE,
            TEST_PASSWORD_2,
            User.Role.USER
        );

        // When & Then
        try {
            userRepository.save(user2);
            userRepository.flush();
            // Should not reach here
            assertThat(false).isTrue();
        } catch (Exception e) {
            assertThat(e).hasMessageContaining(DUPLICATE_KEYWORD);
        }
    }

    @Test
    void testUpdateUser() {
        // Given
        User user = new User(
            null,
            TEST_ORIGINAL,
            TEST_NAME,
            TEST_NICKNAME_ORIGINAL,
            LocalDate.of(BIRTH_YEAR_1990, 1, 1),
            TEST_EMAIL_ORIGINAL,
            TEST_PASSWORD_GENERIC,
            User.Role.USER
        );
        User savedUser = userRepository.save(user);

        // When
        savedUser.setName(TEST_UPDATED);
        savedUser.setLastname(TEST_NAME_CHANGED);
        savedUser.setNickname(TEST_NICKNAME_UPDATED);
        User updatedUser = userRepository.update(savedUser);

        // Then
        assertThat(updatedUser.getName()).isEqualTo(TEST_UPDATED);
        assertThat(updatedUser.getLastname()).isEqualTo(TEST_NAME_CHANGED);
        assertThat(updatedUser.getNickname()).isEqualTo(TEST_NICKNAME_UPDATED);

        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());
        assertThat(retrievedUser).isPresent();
        assertThat(retrievedUser.get().getName()).isEqualTo(TEST_UPDATED);
        assertThat(retrievedUser.get().getNickname()).isEqualTo(TEST_NICKNAME_UPDATED);
    }

    @Test
    void testDeleteUser() {
        // Given
        User user = new User(
            null,
            TEST_DELETE,
            TEST_ME,
            TEST_NICKNAME_DELETE,
            LocalDate.of(BIRTH_YEAR_1990, 1, 1),
            TEST_EMAIL_DELETE,
            TEST_PASSWORD_GENERIC,
            User.Role.USER
        );
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        // When
        userRepository.deleteById(userId);

        // Then
        Optional<User> foundUser = userRepository.findById(userId);
        assertThat(foundUser).isEmpty();
    }
}
