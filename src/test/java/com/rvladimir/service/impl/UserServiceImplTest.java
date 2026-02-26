package com.rvladimir.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rvladimir.domain.User;
import com.rvladimir.repository.UserRepository;
import com.rvladimir.service.dto.CreateUserDTO;
import com.rvladimir.service.dto.UserDTO;
import com.rvladimir.service.mapper.UserMapper;
import com.rvladimir.web.error.ValidationException;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for UserServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final String TEST_NAME = "John";
    private static final String TEST_LASTNAME = "Doe";
    private static final String TEST_NICKNAME = "johnd";
    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final String TEST_PLAIN_PASSWORD = "plainPassword";
    private static final String TEST_HASHED_PASSWORD = "hashedPassword";
    private static final String DUPLICATE_EMAIL_MESSAGE = "Duplicate value for email";
    private static final int BIRTH_YEAR = 1990;
    private static final int BIRTH_MONTH = 5;
    private static final int BIRTH_DAY = 15;
    private static final long USER_ID_1 = 1L;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private CreateUserDTO createUserDTO;
    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        createUserDTO = new CreateUserDTO(
            TEST_NAME,
            TEST_LASTNAME,
            TEST_NICKNAME,
            LocalDate.of(BIRTH_YEAR, BIRTH_MONTH, BIRTH_DAY),
            TEST_EMAIL,
            TEST_PLAIN_PASSWORD
        );

        user = new User(
            null,
            TEST_NAME,
            TEST_LASTNAME,
            TEST_NICKNAME,
            LocalDate.of(BIRTH_YEAR, BIRTH_MONTH, BIRTH_DAY),
            TEST_EMAIL,
            TEST_PLAIN_PASSWORD,
            null
        );

        userDTO = new UserDTO(
            USER_ID_1,
            TEST_NAME,
            TEST_LASTNAME,
            TEST_NICKNAME,
            LocalDate.of(BIRTH_YEAR, BIRTH_MONTH, BIRTH_DAY),
            TEST_EMAIL,
            User.Role.USER
        );
    }

    @Test
    void testCreateSuccess() {
        // Given
        when(userRepository.existsByEmail(createUserDTO.getEmail())).thenReturn(false);
        when(userMapper.toEntity(createUserDTO)).thenReturn(user);

        User savedUser = new User(
            USER_ID_1,
            TEST_NAME,
            TEST_LASTNAME,
            TEST_NICKNAME,
            LocalDate.of(BIRTH_YEAR, BIRTH_MONTH, BIRTH_DAY),
            TEST_EMAIL,
            TEST_HASHED_PASSWORD,
            User.Role.USER
        );
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(userDTO);

        // When
        UserDTO result = userService.create(createUserDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(TEST_NAME);
        assertThat(result.getNickname()).isEqualTo(TEST_NICKNAME);
        assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(result.getRole()).isEqualTo(User.Role.USER);

        verify(userRepository).existsByEmail(TEST_EMAIL);
        verify(userMapper).toEntity(createUserDTO);
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(savedUser);
    }

    @Test
    void testCreateAlwaysAssignsUserRole() {
        // Given
        when(userRepository.existsByEmail(createUserDTO.getEmail())).thenReturn(false);
        when(userMapper.toEntity(createUserDTO)).thenReturn(user);

        User savedUser = new User(
            USER_ID_1,
            TEST_NAME,
            TEST_LASTNAME,
            TEST_NICKNAME,
            LocalDate.of(BIRTH_YEAR, BIRTH_MONTH, BIRTH_DAY),
            TEST_EMAIL,
            TEST_HASHED_PASSWORD,
            User.Role.USER
        );
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(userDTO);

        // When
        userService.create(createUserDTO);

        // Then - role must always be USER regardless of anything else
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getRole()).isEqualTo(User.Role.USER);
    }

    @Test
    void testCreatePasswordIsHashed() {
        // Given
        when(userRepository.existsByEmail(createUserDTO.getEmail())).thenReturn(false);
        when(userMapper.toEntity(createUserDTO)).thenReturn(user);

        User savedUser = new User(
            USER_ID_1,
            TEST_NAME,
            TEST_LASTNAME,
            TEST_NICKNAME,
            LocalDate.of(BIRTH_YEAR, BIRTH_MONTH, BIRTH_DAY),
            TEST_EMAIL,
            TEST_HASHED_PASSWORD,
            User.Role.USER
        );
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(userDTO);

        // When
        userService.create(createUserDTO);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getPassword()).isNotEqualTo(TEST_PLAIN_PASSWORD);
        assertThat(BCrypt.checkpw(TEST_PLAIN_PASSWORD, capturedUser.getPassword())).isTrue();
    }

    @Test
    void testCreateThrowsValidationExceptionWhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(createUserDTO.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.create(createUserDTO))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining(DUPLICATE_EMAIL_MESSAGE);

        verify(userRepository).existsByEmail(TEST_EMAIL);
    }
}
