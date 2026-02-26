package com.rvladimir.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.rvladimir.domain.User;
import com.rvladimir.service.dto.CreateUserDTO;
import com.rvladimir.service.dto.UserDTO;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for UserMapper.
 */
class UserMapperTest {

    private static final String TEST_NAME_JOHN = "John";
    private static final String TEST_LASTNAME_DOE = "Doe";
    private static final String TEST_NICKNAME_JOHND = "johnd";
    private static final String TEST_NAME_JANE = "Jane";
    private static final String TEST_LASTNAME_SMITH = "Smith";
    private static final String TEST_NICKNAME_JANES = "janes";
    private static final String TEST_NAME_ALICE = "Alice";
    private static final String TEST_LASTNAME_JOHNSON = "Johnson";
    private static final String TEST_NICKNAME_ALICEJ = "alicej";
    private static final String TEST_NAME_BOB = "Bob";
    private static final String TEST_LASTNAME_WILLIAMS = "Williams";
    private static final String TEST_NICKNAME_BOBW = "bobw";
    private static final String TEST_EMAIL_JOHN = "john.doe@example.com";
    private static final String TEST_EMAIL_JANE = "jane.smith@example.com";
    private static final String TEST_EMAIL_ALICE = "alice.johnson@example.com";
    private static final String TEST_EMAIL_BOB = "bob.williams@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_HASHED_PASSWORD = "hashedPassword";
    private static final String TEST_SECRET_PASSWORD = "secretPassword";
    private static final int BIRTH_YEAR_1988 = 1988;
    private static final int BIRTH_YEAR_1985 = 1985;
    private static final int BIRTH_YEAR_1990 = 1990;
    private static final int BIRTH_YEAR_1992 = 1992;
    private static final int BIRTH_MONTH_3 = 3;
    private static final int BIRTH_MONTH_5 = 5;
    private static final int BIRTH_MONTH_7 = 7;
    private static final int BIRTH_MONTH_10 = 10;
    private static final int BIRTH_DAY_12 = 12;
    private static final int BIRTH_DAY_15 = 15;
    private static final int BIRTH_DAY_20 = 20;
    private static final int BIRTH_DAY_25 = 25;
    private static final long USER_ID_1 = 1L;
    private static final long USER_ID_2 = 2L;

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void testToEntityFromCreateUserDTO() {
        // Given
        CreateUserDTO createUserDTO = new CreateUserDTO(
            TEST_NAME_JOHN,
            TEST_LASTNAME_DOE,
            TEST_NICKNAME_JOHND,
            LocalDate.of(BIRTH_YEAR_1990, BIRTH_MONTH_5, BIRTH_DAY_15),
            TEST_EMAIL_JOHN,
            TEST_PASSWORD
        );

        // When
        User user = userMapper.toEntity(createUserDTO);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isEqualTo(TEST_NAME_JOHN);
        assertThat(user.getLastname()).isEqualTo(TEST_LASTNAME_DOE);
        assertThat(user.getNickname()).isEqualTo(TEST_NICKNAME_JOHND);
        assertThat(user.getDateBirth()).isEqualTo(LocalDate.of(BIRTH_YEAR_1990, BIRTH_MONTH_5, BIRTH_DAY_15));
        assertThat(user.getEmail()).isEqualTo(TEST_EMAIL_JOHN);
        assertThat(user.getPassword()).isEqualTo(TEST_PASSWORD);
        // role is not mapped from CreateUserDTO; the service layer sets it to USER
        assertThat(user.getRole()).isNull();
    }

    @Test
    void testToEntityFromCreateUserDtoWhenNullReturnsNull() {
        // When
        User user = userMapper.toEntity((CreateUserDTO) null);

        // Then
        assertThat(user).isNull();
    }

    @Test
    void testToEntityFromUserDto() {
        // Given
        UserDTO userDTO = new UserDTO(
            USER_ID_1,
            TEST_NAME_JANE,
            TEST_LASTNAME_SMITH,
            TEST_NICKNAME_JANES,
            LocalDate.of(BIRTH_YEAR_1985, BIRTH_MONTH_10, BIRTH_DAY_20),
            TEST_EMAIL_JANE,
            User.Role.ADMIN
        );

        // When
        User user = userMapper.toEntity(userDTO);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getName()).isEqualTo(TEST_NAME_JANE);
        assertThat(user.getLastname()).isEqualTo(TEST_LASTNAME_SMITH);
        assertThat(user.getNickname()).isEqualTo(TEST_NICKNAME_JANES);
        assertThat(user.getDateBirth()).isEqualTo(LocalDate.of(BIRTH_YEAR_1985, BIRTH_MONTH_10, BIRTH_DAY_20));
        assertThat(user.getEmail()).isEqualTo(TEST_EMAIL_JANE);
        assertThat(user.getRole()).isEqualTo(User.Role.ADMIN);
    }

    @Test
    void testToEntityFromUserDtoWhenNullReturnsNull() {
        // When
        User user = userMapper.toEntity((UserDTO) null);

        // Then
        assertThat(user).isNull();
    }

    @Test
    void testToDto() {
        // Given
        User user = new User(
            USER_ID_1,
            TEST_NAME_ALICE,
            TEST_LASTNAME_JOHNSON,
            TEST_NICKNAME_ALICEJ,
            LocalDate.of(BIRTH_YEAR_1992, BIRTH_MONTH_3, BIRTH_DAY_12),
            TEST_EMAIL_ALICE,
            TEST_HASHED_PASSWORD,
            User.Role.USER
        );

        // When
        UserDTO userDTO = userMapper.toDto(user);

        // Then
        assertThat(userDTO).isNotNull();
        assertThat(userDTO.getName()).isEqualTo(TEST_NAME_ALICE);
        assertThat(userDTO.getLastname()).isEqualTo(TEST_LASTNAME_JOHNSON);
        assertThat(userDTO.getNickname()).isEqualTo(TEST_NICKNAME_ALICEJ);
        assertThat(userDTO.getDateBirth()).isEqualTo(LocalDate.of(BIRTH_YEAR_1992, BIRTH_MONTH_3, BIRTH_DAY_12));
        assertThat(userDTO.getEmail()).isEqualTo(TEST_EMAIL_ALICE);
        assertThat(userDTO.getRole()).isEqualTo(User.Role.USER);
    }

    @Test
    void testToDtoWhenNullReturnsNull() {
        // When
        UserDTO userDTO = userMapper.toDto(null);

        // Then
        assertThat(userDTO).isNull();
    }

    @Test
    void testToDtoDoesNotIncludePassword() {
        // Given
        User user = new User(
            USER_ID_2,
            TEST_NAME_BOB,
            TEST_LASTNAME_WILLIAMS,
            TEST_NICKNAME_BOBW,
            LocalDate.of(BIRTH_YEAR_1988, BIRTH_MONTH_7, BIRTH_DAY_25),
            TEST_EMAIL_BOB,
            TEST_SECRET_PASSWORD,
            User.Role.ADMIN
        );

        // When
        UserDTO userDTO = userMapper.toDto(user);

        // Then
        assertThat(userDTO).isNotNull();
        assertThat(userDTO.getName()).isEqualTo(TEST_NAME_BOB);
        assertThat(userDTO.getLastname()).isEqualTo(TEST_LASTNAME_WILLIAMS);
        assertThat(userDTO.getNickname()).isEqualTo(TEST_NICKNAME_BOBW);
        assertThat(userDTO.getDateBirth()).isEqualTo(LocalDate.of(BIRTH_YEAR_1988, BIRTH_MONTH_7, BIRTH_DAY_25));
        assertThat(userDTO.getEmail()).isEqualTo(TEST_EMAIL_BOB);
        assertThat(userDTO.getRole()).isEqualTo(User.Role.ADMIN);

        // Verify that UserDTO class doesn't have a password field
        boolean hasPasswordField = Arrays.stream(UserDTO.class.getDeclaredFields())
            .map(Field::getName)
            .anyMatch(fieldName -> fieldName.equalsIgnoreCase("password"));
        assertThat(hasPasswordField).isFalse();
    }
}
